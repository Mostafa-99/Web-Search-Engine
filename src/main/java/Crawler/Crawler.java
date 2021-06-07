package Crawler;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
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
    int numberOfThreads;
    int state;
    static final int RESTART = 0;
    static final int RESUME = 1;
    Hashtable<String, robotsObj> robots;
    int DBMaxSize = 0;
 
   public Crawler(int state, int numberOfThreads,  int crawlingSize, int DBMaxSize){
       this.DB = new DBManager();
       this.state = state;
       this.crawlingSize = crawlingSize;
       this.numberOfThreads = numberOfThreads;
       this.DBMaxSize = DBMaxSize;
    }

    public Crawler(Queue<linkAndID> queue, int numberOfThreads, int crawlingSize, int DBMaxSize, Hashtable<String, robotsObj> robots){
        this.DB= new DBManager();
        this.numberOfThreads = numberOfThreads;
        this.queue = queue;        
        this.robots = robots;
        this.crawlingSize = crawlingSize;
        this.DBMaxSize = DBMaxSize;
    }
    
    void getURLs(String linkIn,int id){
        Elements links;
        try {
            Document doc = Jsoup.connect(linkIn).get();
            
            try {
                FileWriter myWriter = new FileWriter("./downloaded/page_"+id+".html");
                myWriter.write(doc.toString());
                myWriter.close();
            } catch (IOException e) {}
            if(DB.getTotalNumberOfLinks_CrawlerTable()<DBMaxSize){
                links = doc.select("a");
                Element link;
                for(int j=0;j<links.size();j++){
                    link=links.get(j);
                    String linkHref = link.attr("abs:href").toString();
                    if(linkHref!=""){
                        linkHref = normalizeURI(linkHref);
                        try {
                            //check if link is valid
                            Jsoup.connect(linkHref).get();
                            boolean isValid = checkValidityOfLink(linkHref);
                            
                            if(isValid == true){
                                DB.addLink_CrawlerTable(linkHref);
                            }
                        } catch (Exception e) {  }    

                    
                    }
                }
            }
            DB.markVisitedLink_CrawlerTable(linkIn);
        }
        catch (Exception e) {  getURLs(linkIn,id); }
    }
    
    String normalizeURI(String urlStr) {
        try {
            urlStr = urlStr.replaceAll("/index.html", "/");
            urlStr = urlStr.replaceAll("/#", "/");
            URL url= new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost().toLowerCase(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            urlStr=uri.toASCIIString();
        } catch (Exception e) {}
        return urlStr;
    }
    
    String reformRobotsPath(String path){
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
                try {
                    String robotURL ="https://" + hostname+"/robots.txt";
                    Document doc = Jsoup.connect(robotURL).get();
                    String arr[] = (doc.text()).split(" ");
                    robotsObj r = new robotsObj();
                    int i=0;
                    while(i<arr.length){
                        if(arr[i].equals("User-agent:") && arr[i+1].equals("*")){
                            i+=2;
                            while(!arr[i].equals("User-agent:") && i<arr.length){
                                if(arr[i].equals("Allow:")){
                                    String path = arr[i+1];
                                    path = reformRobotsPath(path);
                                    r.allowed.add(path);
                                }
                                else if(arr[i].equals("Disallow:")){
                                    String path = arr[i+1];
                                    path = reformRobotsPath(path);
                                    r.disAllowed.add(path);
                                }
                                i+=2;
                            }
                        }
                        i+=2;
                    }
                    synchronized(robots){
                        robots.put(hostname, r);
                    }
                } catch (Exception e) { }
            }
        }
        catch(Exception e){ }
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
                        int sizeRequired = crawlingSize-DB.getTotalNumberOfBatchedLinks_CrawlerTable() >= numberOfThreads ? numberOfThreads :  crawlingSize-DB.getTotalNumberOfBatchedLinks_CrawlerTable();
                        queue = DB.getLinksToVisitCrawler_CrawlerTable(sizeRequired);
                        if(queue.size()==0 || DB.getTotalNumberOfDownloadedLinks_CrawlerTable() >= crawlingSize){
                            break;
                        }
                    }
                    if(queue.size()!=0){
                        linkToVisit = queue.poll();
                    }
                    
                }
                if(linkToVisit !=null ){
                    //System.out.println("Start download "+ Thread.currentThread().getName()+" link id="+linkToVisit.id);
                    getURLs(linkToVisit.link,linkToVisit.id);
                    //System.out.println("Finished download "+ Thread.currentThread().getName());
                    //System.out.println("************************************************************************");
                }
            }
        }
        catch(Exception e){ }
    }

    void runSeeds(){
        try {
            DB.dropTables();
            DB.createTables();

            File myObj = new File("./seeds/seeds.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                DB.addLink_CrawlerTable(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {}
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
            crawl();
            //System.out.println("************************************************************");
            //System.out.println("Thread "+ Thread.currentThread().getName() + " Finished");
            //System.out.println("************************************************************");
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
        //System.out.println(DB.getTotalNumberOfLinks_CrawlerTable());
        if(DB.getTotalNumberOfDownloadedLinks_CrawlerTable() >= crawlingSize){
            sizeRequired=0;
        }
        queue = DB.getLinksToVisitCrawler_CrawlerTable(sizeRequired);
        Hashtable<String, robotsObj> robots =  new Hashtable<String, robotsObj>();

        Thread[] threads = new Thread[numberOfThreads];
        for(int i=0;i<numberOfThreads;i++){
            threads[i] = new Thread(new Crawler(queue,numberOfThreads,crawlingSize, DBMaxSize,robots));
            threads[i].setName("Thread "+(i+1));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        
    }


    public static void main(String args[]) throws IOException {
        Hashtable<String, robotsObj> robots =  new Hashtable<String, robotsObj>();
        Queue<linkAndID> queue = new LinkedList<>();
        int x = 0;
        Crawler c =new Crawler(queue,x,x,0,robots);
        c.downloadRobotTxt("https://www.amazon.com/");
    }

}
