package Crawler;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Crawler.CrawlerDB.*;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;



public class Crawler {
    CrawlerDB DB ;
    Queue<linkAndID> queue = new LinkedList<>();
    final int crawlingSize = 2;
    int numberOfLinksInDB =0;
    int numberOfDownloadedLinks =0;
    final int numberOfThreads = 1;


    public class robotsObj {
        public ArrayList<String> allowed;
        public ArrayList<String> disAllowed;
        public robotsObj(){
            allowed = new ArrayList<String>();
            disAllowed = new ArrayList<String>();
        }
    }

    Hashtable<String, robotsObj> robots;

    public Crawler(){
        DB= new CrawlerDB();
        numberOfLinksInDB = DB.getTotalNumberOfLinks();
        numberOfDownloadedLinks = DB.getTotalNumberOfDownloadedLinks();
        robots = new Hashtable<String, robotsObj>();
        //System.out.println("******************************************************"+numberOfLinksInDB);
    }
    
    
    public void download(String urlString, int id) throws IOException {

        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            BufferedWriter writer = new BufferedWriter(new FileWriter("./downloaded/page_"+Integer.toString(id)+".html"));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
            writer.close();
            System.out.println("*****************************************************************************");
            
            numberOfDownloadedLinks++;
            System.out.println("******numberOfDownloadedLinks: "+numberOfDownloadedLinks);
            System.out.println("Page downloaded. "+(id));
            System.out.println("*****************************************************************************");

        } catch (MalformedURLException e) {
            System.out.println("Error");
        } catch (IOException io) {
            System.out.println("Error");
        }
    }

    void getURLs(String linkIn,int id){
        Document doc;
        String title="";
        Elements links;
        try {
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader("./downloaded/page_"+Integer.toString(id)+".html"));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
            String content = contentBuilder.toString();

            doc = Jsoup.parse(content);
            title = doc.title();

            links = doc.select("a");
            Element link;
            for(int j=0;j<links.size();j++){
                link=links.get(j);
                String linkHref = link.attr("abs:href").toString();
                if(linkHref!=""){
                    //System.out.println(j + " "+linkHref);
                    if(checkValidityOfLink(linkHref)){
                        /*if(numberOfLinksInDB == crawlingSize){
                            break;
                        }*/
                        //System.out.println("a= " +linkHref);
                        DB.addLink(linkHref);
                        numberOfLinksInDB++;
                    }
                }
            }
            DB.markVisitedLink(linkIn);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //to be used
    String normalizeURI(String uri) {
        //uri = uri.replaceAll("^(http://)", "");
        //uri = uri.replaceAll("^(https://)", "");
        //uri = uri.replaceAll("^(www\\.)", "");
        URI x= URI.create(uri.toLowerCase()).normalize();
        return x.toString();
    }

    void crawl(){
        try{
            Queue<linkAndID> queue = new LinkedList<>();
            queue = DB.getLinksToVisit(numberOfThreads);
            while(true){
                if(numberOfDownloadedLinks == crawlingSize){
                    break;
                }
                if(queue.size()==0){
                    queue = DB.getLinksToVisit(numberOfThreads);
                    if(queue.size()==0){
                        break;
                    }
                }
                linkAndID linkToVisit = queue.poll();
                download(linkToVisit.link, linkToVisit.id);
                getURLs(linkToVisit.link,linkToVisit.id);
            }
        }
        catch(Exception e){}
    }

    void runSeeds(){
        try {
            DB.dropTable();
            DB.createTable();
            //numberOfLinksInDB = DB.getTotalNumberOfLinks();
            numberOfDownloadedLinks = DB.getTotalNumberOfDownloadedLinks();
            File myObj = new File("./seeds/seeds.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                DB.addLink(data);
                //numberOfLinksInDB++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        catch(Exception e){}
    }
    
    String reformRobotsPath(String path){
        path = normalizeURI(path);
        path = path.replaceAll("\\*", ".*");
        path = path.replaceAll("=", "=.*");
        path = path.replaceAll("\\?", "\\\\"+"?");
        path = path.replaceAll("\\+", "\\\\"+"+");
        if(path.substring(path.length() - 1) == "/"){
            path += ".*";
        }
        return path;
    }
    
    void downloadRobotTxt(String url){
        try{
            URL temp = new URL(url);
            String hostname = temp.getHost();
            if(robots.containsKey(hostname) == false){//not saved before
                BufferedReader reader = null;
                try {
                    String robotURL ="https://" + hostname+"/robots.txt";
                    URL hostURL = new URL(robotURL);
                    reader = new BufferedReader(new InputStreamReader(hostURL.openStream()));
                    String line;
                    robotsObj r = new robotsObj();
                    System.out.println("Host: "+ robotURL);                    
                    while ((line = reader.readLine()) != null) {
                        if(line.startsWith("User-agent: *")){
                            while ((line = reader.readLine()) != null) {                                
                                if(line.startsWith("Allow: ")){
                                    String path = line.replaceAll("^(Allow: )", "");
                                    path = reformRobotsPath(path);
                                    r.allowed.add(path);
                                }
                                else if(line.startsWith("Disallow: ")){
                                    String path = line.replaceAll("^(Disallow: )", "");
                                    path = reformRobotsPath(path);
                                    r.disAllowed.add(path);
                                }
                                else if(!line.startsWith("User-agent: *") && !line.isEmpty()){
                                    System.out.println("Break: "+line);
                                    break;
                                }
                            }
                        }
                    }
                    robots.put(hostname, r);
                    reader.close();
                } catch (MalformedURLException e) {
                    System.out.println("Error in: "+url);
                } catch (Exception e) {
                    System.out.println("Error in: "+url);
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Already added");
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    boolean checkValidityOfLink(String link){
        boolean disAllowedFlag = false;
        try{
            downloadRobotTxt(link);
            URL temp = new URL(link);
            String hostname = temp.getHost();
            robotsObj r = robots.get(hostname);
            //First check if disallowed
            List<String> disAllowed = r.disAllowed;
            for (int i=0;i<disAllowed.size();i++){
                if(link.matches(".*"+disAllowed.get(i))==true){//found match
                    disAllowedFlag = true;
                    break;
                }
            }
            if(disAllowedFlag==true){ //check in allowed if matched in disallowed
                List<String> allowed = r.allowed;
                for (int i=0;i<allowed.size();i++){
                    if(link.matches(".*"+allowed.get(i))==true){//found match
                        disAllowedFlag = false;
                        break;
                    }
                }
            }

        }
        catch(Exception e){}
        return  DB.checkLink(link) && !disAllowedFlag;
    }
    public static void main(String args[]) throws IOException {
        Crawler c =new Crawler();
        c.runSeeds();
        c.crawl();
        //String link1 = c.normalizeURI("https://www.amazon.com/slp/safcsdavdsv/b");      
        
        //System.out.println("1- "+c.checkValidityOfLink(link1)); 
        
        
       

    }
}
