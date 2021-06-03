package Crawler;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import DBManager.DBManager;
import DBManager.DBManager.linkAndID;

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
    DBManager DB ;
    Queue<linkAndID> queue;
    int crawlingSize;
    //int numberOfLinksInDB =0;
    NumberOfDownloads numberOfDownloadedLinks;
    int numberOfThreads;
    int state;
    static final int RESTART = 0;
    static final int RESUME = 1;
    Hashtable<String, robotsObj> robots;
 
   public Crawler(int state, int numberOfThreads,  int crawlingSize){
       this.DB = new DBManager();
       
       this.state = state;
       this.crawlingSize = crawlingSize;
       this.numberOfThreads = numberOfThreads;
    }

    public Crawler(DBManager DB, Queue<linkAndID> queue, int numberOfThreads, int crawlingSize, NumberOfDownloads numberOfDownloadedLinks, Hashtable<String, robotsObj> robots){
        this.DB= DB;
        this.numberOfDownloadedLinks = numberOfDownloadedLinks;
        this.numberOfThreads = numberOfThreads;
        this.queue = queue;        
        this.robots = robots;
        this.crawlingSize = crawlingSize;
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
                        boolean isValid = checkValidityOfLink(linkHref);
                        
                        if(isValid == true){
                            /*if(numberOfLinksInDB == crawlingSize){
                                break;
                            }*/
                            //System.out.println("a= " +linkHref);
                            //System.out.println("DBCheckLink: "+linkHref+"   "+isValid);
                            DB.addLink_CrawlerTable(linkHref);
                           // numberOfLinksInDB++;
                        }
                    }
                }
            }
            DB.markVisitedLink_CrawlerTable(linkIn);
        }
        catch (IOException e) {
           // e.printStackTrace();
        }
    }
    
    /*TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
    String normalizeURI(String uri) {
        //uri = uri.replaceAll("^(http://)", "");
        //uri = uri.replaceAll("^(https://)", "");
        //uri = uri.replaceAll("^(www\\.)", "");
        //URI x= URI.create(uri.toLowerCase()).normalize();
        //return x.toString();
        uri = uri.replaceAll("\\s", ""); 
        uri = uri.toLowerCase(); 
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
        return  DB.checkLink_CrawlerTable(link) && !disAllowedFlag;
    }

    void crawl(){
        
        try{
            while(true){
                linkAndID linkToVisit = null;
                synchronized(queue){
                    if(queue.size()==0 && DB.getTotalNumberOfBatchedLinks_CrawlerTable() <= crawlingSize && DB.getTotalNumberOfLinks_CrawlerTable()>=numberOfThreads){                        
                        System.out.println("Thread crawl "+ Thread.currentThread().getName());
                        int sizeRequired = crawlingSize-DB.getTotalNumberOfBatchedLinks_CrawlerTable() >= numberOfThreads ? numberOfThreads :  crawlingSize-DB.getTotalNumberOfBatchedLinks_CrawlerTable();
                        System.out.println("sizeRequired__"+sizeRequired);
                        System.out.println("crawlingSize__"+crawlingSize);
                        System.out.println("batchedLinks__"+DB.getTotalNumberOfBatchedLinks_CrawlerTable());
                        queue = DB.getLinksToVisitCrawler_CrawlerTable(sizeRequired);
                        //System.out.println("!!!!!!!!!!!!!!!__numberOfThreads: "+numberOfThreads+"!!!!!!!!!!!!!!!!!!!____total batches: "+DB.getTotalNumberOfBatchedLinks_CrawlerTable()+"!!!!!!!!!!!!!!!!!!!!____ "+sizeRequired);
                        if(queue.size()==0 || DB.getTotalNumberOfDownloadedLinks_CrawlerTable() == crawlingSize ){
                            break;
                        }
                    }
                    if(queue.size()!=0){
                        linkToVisit = queue.poll();
                        /*synchronized(numberOfDownloadedLinks){
                            numberOfDownloadedLinks.increment();
                        }*/
                    }
                    
                }
                if(linkToVisit !=null ){
                    System.out.println("Start download "+ Thread.currentThread().getName());
                    download(linkToVisit.link, linkToVisit.id);
                    getURLs(linkToVisit.link,linkToVisit.id);
                    System.out.println("Finished download "+ Thread.currentThread().getName());
                }
            }
        }
        catch(Exception e){ }
    }

    void runSeeds(){
        try {
            DB.dropTables();
            DB.createTables();
            //numberOfLinksInDB = DB.getTotalNumberOfLinks_CrawlerTable();
            
            //numberOfDownloadedLinks.setValue(DB.getTotalNumberOfDownloadedLinks_CrawlerTable());
            File myObj = new File("./seeds/seeds.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                DB.addLink_CrawlerTable(data);
                //numberOfLinksInDB++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            //System.out.println("An error occurred.");
            //e.printStackTrace();
        }
        catch(Exception e){}
    }
    
    void resumeCrawling(){
        DB.resetBatchedLinks_CrawlerTable();
    }
   
    public void run(){
        if(Thread.currentThread().getName() == "Thread 0"){
            try {
                crawlerMain();
            } catch (Exception e) {}
        }
        else{
            System.out.println("Thread "+ Thread.currentThread().getName() + " Hi");
            System.out.println("************************************************************");
            crawl();
            System.out.println("************************************************************");
            System.out.println("Thread "+ Thread.currentThread().getName() + " Finished a link");
            System.out.println("************************************************************");
        }
    }

    public void crawlerMain() throws InterruptedException{
        if(state == RESTART){

            runSeeds();
        }
        else{
            resumeCrawling();
        }

        Queue<linkAndID> queue = new LinkedList<>();
        int sizeRequired = crawlingSize-DB.getTotalNumberOfBatchedLinks_CrawlerTable() >= numberOfThreads ? numberOfThreads :  crawlingSize-DB.getTotalNumberOfBatchedLinks_CrawlerTable();

        queue = DB.getLinksToVisitCrawler_CrawlerTable(sizeRequired);
        NumberOfDownloads numberOfDownloadedLinks= new NumberOfDownloads();
        numberOfDownloadedLinks.setValue(DB.getTotalNumberOfDownloadedLinks_CrawlerTable());
        Hashtable<String, robotsObj> robots =  new Hashtable<String, robotsObj>();

        Thread[] threads = new Thread[numberOfThreads];
        for(int i=0;i<numberOfThreads;i++){
            threads[i] = new Thread(new Crawler(DB,queue,numberOfThreads,crawlingSize,numberOfDownloadedLinks,robots));
            threads[i].setName("Thread "+(i+1));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        
    }
}



class CrawlerMain{
    /*static final int RESTART = 0;
    static final int RESUME = 1;
    static final int state = 0;
    static final int numberOfThreads = 9;*/

    public void main(String args[]) throws InterruptedException {
        
        /*DBManager DB= new DBManager();
        Crawler c0 = new Crawler(DB);
        if(state == RESTART){
            c0.runSeeds();
        }
        else{
            c0.resumeCrawling();
        }*/

        /*Queue<linkAndID> queue = new LinkedList<>();
        queue = DB.getLinksToVisitCrawler_CrawlerTable(numberOfThreads);
        NumberOfDownloads numberOfDownloadedLinks= new NumberOfDownloads();
        numberOfDownloadedLinks.setValue(DB.getTotalNumberOfDownloadedLinks_CrawlerTable());
        Hashtable<String, robotsObj> robots =  new Hashtable<String, robotsObj>();

        Thread[] threads = new Thread[numberOfThreads];
        for(int i=0;i<numberOfThreads;i++){
            threads[i] = new Thread(new Crawler(DB,queue,numberOfThreads,numberOfDownloadedLinks,robots));
            threads[i].setName("Thread "+(i+1));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }*/
        
    
    }
}