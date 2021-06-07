package Indexer;

import java.io.*;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import DBManager.DBManager;

import opennlp.tools.stemmer.PorterStemmer;

public class Indexer implements Runnable {
    
    DBManager DB = new DBManager();
    Hashtable<String,Integer> headersWordsFrequency = new Hashtable<String,Integer>();
    Hashtable<String,Integer> textWordsFrequency = new Hashtable<String,Integer>();
    Hashtable<String,Integer> titleWordsFrequency = new Hashtable<String,Integer>();

    public ArrayList<String> loadStopwords() throws IOException {
        Scanner s = new Scanner(new File("./src/main/resources/stopWords.txt"));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()){
            list.add(s.next());
        }
        s.close();
        return list;
    }

    public Elements[] extractWordFromPage(String content){
        Elements[] elements = new Elements[3];
        Document doc = Jsoup.parse(content);
        elements[0]= doc.select("h1, h2, h3, h4, h5, h6");
        elements[1]= doc.select("p");
        elements[2]= doc.select("title");
        return elements;
    }

    public ArrayList<String> removeStopWords(Elements elements){
        ArrayList<String> words = new ArrayList<String>();
        for (Element e : elements){
            String  string = e.text().toLowerCase().replaceAll("'|`|’|,", "").replaceAll("[^a-z]", " ").trim();
            String[] s = string.split("\\s+");
            for(int i =0; i< s.length;i++)
            {
                if(s[i]!="")
                    words.add(s[i]);
            }
        }
        try {
            words.removeAll(loadStopwords());
        } catch (IOException e1) { }
        return words;
    }

    public void stemWords(Hashtable<String,Integer> hTable,ArrayList<String> words){
        PorterStemmer porterStemmer = new PorterStemmer();
        for(String word : words){
            word = porterStemmer.stem(word);
            manageHashTables(hTable, word);
        }
    }

    public void manageHashTables(Hashtable<String,Integer> hTable,String word){
        if(hTable.containsKey(word)){
            int freq = hTable.get(word);
            hTable.replace(word, freq+1);
         }
         else {
            hTable.put(word, 1);
         }
    }

    public void addToDataBase(int pageID){
        Set<String> headerWords = headersWordsFrequency.keySet();
        Set<String> textWords = textWordsFrequency.keySet();
        Set<String> titleWords = titleWordsFrequency.keySet();
        Set<String> allWords = new HashSet<String>();
        allWords.addAll(headerWords);
        allWords.addAll(textWords);
        allWords.addAll(titleWords);
        for (String word : allWords){
            int header = 0;
            int text = 0;
            int title = 0;
            if(headerWords.contains(word)){
                header = headersWordsFrequency.get(word);
            }
            if(textWords.contains(word)){
                text = textWordsFrequency.get(word);
            }
            if(titleWords.contains(word)){
                title = titleWordsFrequency.get(word);
            }
            String link = DB.getLinkFromID_CrawlerTable(pageID);
            DB.addLink_WordsTable(link, text+header+title, text, header, title, word);
        }
    }

    public void index(String p){
        try{
            String indexPath = p;
            int pageId = Integer.parseInt(indexPath.replaceAll("[^0-9]", ""));
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(indexPath));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
            String content = contentBuilder.toString();
            Elements[] elements = extractWordFromPage(content);
            for (int i =0; i < elements.length;i++){
                ArrayList<String> words = removeStopWords(elements[i]);
                if(i==0){
                    stemWords(headersWordsFrequency, words);
                }
                else if(i==1){
                    stemWords(textWordsFrequency, words);
                }
                else {
                    stemWords(titleWordsFrequency, words);
                }
                
            }
            addToDataBase(pageId);
        }
        catch (IOException e) { }
    }

    public void indexerMain(){
        Queue<Integer> queue = DB.getLinksToVisitIndexer_CrawlerTable(5);
        int batchedLinks = DB.getTotalNumberOfBatchedLinks();
        int downloadedLinks = DB.getTotalNumberOfDownloadedLinks_CrawlerTable();
        int indexedLinks = DB.getTotalNumberOfIndexedLinks_CrawlerTable();

        while( !((batchedLinks == downloadedLinks) && (indexedLinks == downloadedLinks) && (batchedLinks == downloadedLinks)) ){
            if(queue.size()!=0){
                String filePath = "./downloaded/page_";
                int id = queue.poll();
                filePath = filePath + Integer.toString(id) + ".html";
                index(filePath);
                DB.markIndexedLink_CrawlerTable(id);
                batchedLinks = DB.getTotalNumberOfBatchedLinks();
                downloadedLinks = DB.getTotalNumberOfDownloadedLinks_CrawlerTable();
                indexedLinks = DB.getTotalNumberOfIndexedLinks_CrawlerTable();
                headersWordsFrequency.clear();
                textWordsFrequency.clear();
                titleWordsFrequency.clear();
                //System.out.println("Link with id "+id+" finished indexing!");
            }
            else if(queue.size()==0){
                queue = DB.getLinksToVisitIndexer_CrawlerTable(5);
            }
        }
    }
    
    public void run(){
        indexerMain();
    }
    public static void main(String args[]) throws IOException {
        //Indexer i1 = new Indexer();
        //i1.index("./downloaded/page_5.html");
      
    }
}

