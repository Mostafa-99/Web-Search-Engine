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

class NumberOfDownloads{
    private int numberOfDownloads = 0;
    void increment(){numberOfDownloads++;}
    void setValue(int val){numberOfDownloads = val;}
    int getValue(){ return numberOfDownloads;}
}
class robotsObj {
    public ArrayList<String> allowed;
    public ArrayList<String> disAllowed;
    public robotsObj(){
        allowed = new ArrayList<String>();
        disAllowed = new ArrayList<String>();
    }
}


public class Crawler implements Runnable {
    CrawlerDB DB ;
    Queue<linkAndID> queue;
    final int crawlingSize = 68;
    //int numberOfLinksInDB =0;
    NumberOfDownloads numberOfDownloadedLinks;
    int numberOfThreads;

    Hashtable<String, robotsObj> robots;
 
   public Crawler(CrawlerDB DB){this.DB = DB;}
    public Crawler(CrawlerDB DB, Queue<linkAndID> queue, int numberOfThreads, NumberOfDownloads numberOfDownloadedLinks, Hashtable<String, robotsObj> robots){
        this.DB= DB;
        this.numberOfDownloadedLinks = numberOfDownloadedLinks;
        this.numberOfThreads = numberOfThreads;
        this.queue = queue;        
        this.robots = robots;
    }
    
    public void download(String urlString, int id){
        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA___"+id+"__AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: "+ Thread.currentThread().getName());

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
            //System.out.println("*****************************************************************************");
            //numberOfDownloadedLinks++;
            /*(numberOfDownloadedLinks){
                numberOfDownloadedLinks.increment();;
            }*/
            //System.out.println("******numberOfDownloadedLinks: "+numberOfDownloadedLinks.getValue());
            //System.out.println("Page downloaded. "+(id));
            //System.out.println("*****************************************************************************");

        } catch (MalformedURLException e) {
            System.out.println("Error");
        } catch (IOException io) {
            System.out.println("Error");
        }
    }

    void getURLs(String linkIn,int id){
        Document doc;
        Elements links;
        try {
            String title="";
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
                    synchronized(DB){
                        if(checkValidityOfLink(linkHref)){
                            /*if(numberOfLinksInDB == crawlingSize){
                                break;
                            }*/
                            //System.out.println("a= " +linkHref);
                            DB.addLink(linkHref);
                           // numberOfLinksInDB++;
                        }
                    }
                }
            }
            DB.markVisitedLink(linkIn);
        }
        catch (IOException e) {
           // e.printStackTrace();
        }
    }
    
    String normalizeURI(String uri) {
        //uri = uri.replaceAll("^(http://)", "");
        //uri = uri.replaceAll("^(https://)", "");
        //uri = uri.replaceAll("^(www\\.)", "");
        //URI x= URI.create(uri.toLowerCase()).normalize();
        //return x.toString();
        return uri;
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
                    //System.out.println("Host: "+ robotURL);                    
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
                                    //System.out.println("Break: "+line);
                                    break;
                                }
                            }
                        }
                    }
                    synchronized(robots){
                        robots.put(hostname, r);
                    }
                    reader.close();
                } catch (MalformedURLException e) {
                    //System.out.println("Error in: "+url);
                } catch (Exception e) {
                   // System.out.println("Error in: "+url);
                   // e.printStackTrace();
                }
            }
            /*else{
                System.out.println("Already added");
            }*/
        }
        catch(Exception e){
            //e.printStackTrace();
        }
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

    void crawl(){
        
        try{
            while(true){
                //System.out.println("hi");
                /*if(DB.getTotalNumberOfDownloadedLinks()== crawlingSize){
                    break;
                }*/
                linkAndID linkToVisit;
                synchronized(queue){
                    if(queue.size()==0 && DB.getTotalNumberOfBatchedLinks() <= crawlingSize){                        
                        System.out.println("Thread crawl"+ Thread.currentThread().getName());
                        int sizeRequired = crawlingSize-DB.getTotalNumberOfBatchedLinks() >= numberOfThreads ? numberOfThreads :  crawlingSize-DB.getTotalNumberOfBatchedLinks();
                        queue = DB.getLinksToVisit(sizeRequired);
                        System.out.println("!!!!!!!!!!!!!!!__numberOfThreads: "+numberOfThreads+"!!!!!!!!!!!!!!!!!!!____total batches: "+DB.getTotalNumberOfBatchedLinks()+"!!!!!!!!!!!!!!!!!!!!____ "+sizeRequired);
                        if(queue.size()==0 || DB.getTotalNumberOfDownloadedLinks() == crawlingSize ){
                            break;
                        }
                    }
                    linkToVisit = queue.poll();
                    
                }
                synchronized(numberOfDownloadedLinks){
                    numberOfDownloadedLinks.increment();
                }
                download(linkToVisit.link, linkToVisit.id);
                getURLs(linkToVisit.link,linkToVisit.id);
                System.out.println("Finished download "+ Thread.currentThread().getName());
            }
        }
        catch(Exception e){ }
    }

    void runSeeds(){
        try {
            DB.dropTable();
            DB.createTable();
            //numberOfLinksInDB = DB.getTotalNumberOfLinks();
            
            //numberOfDownloadedLinks.setValue(DB.getTotalNumberOfDownloadedLinks());
            File myObj = new File("./seeds/seeds.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                DB.addLink(data);
                //numberOfLinksInDB++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            //System.out.println("An error occurred.");
            //e.printStackTrace();
        }
        catch(Exception e){}
    }
    
   
    public void run(){
        //runSeeds();
        /*try {
            Thread.sleep(100);
        } catch (Exception e) {
            //TODO: handle exception
        }*/
        crawl();
        System.out.println("************************************************************");
        System.out.println("Thread "+ Thread.currentThread().getName() + "Finished a link");
        System.out.println("************************************************************");
    }

}
class CrawlerMain{
    public static void main(String args[]) throws InterruptedException {
        CrawlerDB DB= new CrawlerDB();
        Crawler c0 = new Crawler(DB);
        c0.runSeeds();
        final int numberOfThreads = 10;
        Queue<linkAndID> queue = new LinkedList<>();
        
        queue = DB.getLinksToVisit(numberOfThreads);
        NumberOfDownloads numberOfDownloadedLinks= new NumberOfDownloads();
        numberOfDownloadedLinks.setValue(DB.getTotalNumberOfDownloadedLinks());
        Hashtable<String, robotsObj> robots =  new Hashtable<String, robotsObj>();

        Thread[] threads = new Thread[numberOfThreads];
        for(int i=0;i<numberOfThreads;i++){
            //System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ"+i);
            threads[i] = new Thread(new Crawler(DB,queue,numberOfThreads,numberOfDownloadedLinks,robots));
            threads[i].setName("Thread "+(i+1));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        //String link1 = c.normalizeURI("https://www.amazon.com/slp/safcsdavdsv/b");      
        
        //System.out.println("1- "+c.checkValidityOfLink(link1)); 
      /* int numberOfBatches =0;
       int numberOfThreads = 4;
       int crawlingSize = 39;
        for(int i =0;i<20;i++){
            if(numberOfBatches == crawlingSize)break;
            int numberOfBatchesTemp = numberOfThreads - ((crawlingSize-numberOfBatches)%(numberOfThreads));
            System.out.println( "4-"+(crawlingSize-numberOfBatches)+"%4"+"="+numberOfBatchesTemp);
            numberOfBatches += numberOfBatchesTemp;
        }
        System.out.println(numberOfBatches);*/
       // System.out.println("!!!!!!!!!!!!!!!__numberOfThreads: "+numberOfThreads+"!!!!!!!!!!!!!!!!!!!____total batches: "+DB.getTotalNumberOfBatchedLinks()+"!!!!!!!!!!!!!!!!!!!!____ "+(crawlingSize-DB.getTotalNumberOfBatchedLinks()+numberOfThreads)%(numberOfThreads));
       
    
    }
}