package Main;


import Crawler.Crawler;
import Indexer.Indexer;

public class Main {
    static final int RESTART = 0;
    static final int RESUME = 1;
    static final int state = RESUME;
    static final int numberOfThreads = 10;
    static final int crawlingSize = 5000;
    static final int DBMaxSize = 10000;
    public static void main(String[] args) throws Exception {
        Crawler c0 = new Crawler(state,numberOfThreads, crawlingSize, DBMaxSize);
        Indexer i0 = new Indexer();
        //Run as threads 

        Thread mainCrawlerThread = new Thread(c0);
        Thread mainIndexerThread = new Thread(i0);
        mainCrawlerThread.setName("Thread 0");
        mainCrawlerThread.start();
        mainCrawlerThread.join();
        System.out.println("Crawler finished "+crawlingSize+" link");
        mainIndexerThread.start();
        mainIndexerThread.join();
        System.out.println("Indexer finished");
        
    }
}
