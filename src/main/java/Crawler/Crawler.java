package Crawler;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Crawler.CrawlerDB.*;

import java.util.LinkedList;
import java.util.Queue;

public class Crawler {
    int pageNumbers = 1;
    String downloadPath = "./downloaded/page_"+pageNumbers+".html";
    CrawlerDB DB ;
    Queue<linkAndID> queue = new LinkedList<>();
    final int crawlingSize = 2;
    int numberOfLinksInDB =0;
    int numberOfDownloadedLinks =0;

    public Crawler(){
        DB= new CrawlerDB();
        numberOfLinksInDB = DB.getTotalNumberOfLinks();
        numberOfDownloadedLinks = DB.getTotalNumberOfDownloadedLinks();
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
                    if(DB.checkLink(linkHref)){
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
        uri = uri.replaceAll("^(http://)", "");
        uri = uri.replaceAll("^(https://)", "");
        uri = uri.replaceAll("^(www\\.)", "");
        URI x= URI.create(uri.toLowerCase()).normalize();
        return x.toString();
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
    public static void main(String args[]) throws IOException {
        Crawler c =new Crawler();
        System.out.println(c.normalizeURI("http://www.jaVwww.a2s.com/./"));
         c.DB= new CrawlerDB();
        //init DB and queue
        c.runSeeds();

        Queue<linkAndID> queue = new LinkedList<>();

        queue = c.DB.getLinksToVisit();
        while(true){
            if(c.numberOfDownloadedLinks == c.crawlingSize){
                break;
            }
            if(queue.size()==0){
                queue = c.DB.getLinksToVisit();
                if(queue.size()==0){
                    System.out.println("qqqqq: "+queue);
                    break;
                }
            }
            linkAndID linkToVisit = queue.poll();
            c.download(linkToVisit.link, linkToVisit.id);
            c.getURLs(linkToVisit.link,linkToVisit.id);
        }

    }

}
