package Main;

import Crawler.Crawler;
import Indexer.Indexer;

public class Main {
    static final int RESTART = 0;
    static final int RESUME = 1;
    static final int state = RESTART;
    static final int numberOfThreads = 7;
    static final int crawlingSize = 20;

    public static void main(String[] args) throws Exception {
        Crawler c0 = new Crawler(state,numberOfThreads, crawlingSize);
        Indexer i0 = new Indexer();
        //c0.crawlerMain();
        Thread mainCrawlerThread = new Thread(c0);
        Thread mainIndexerThread = new Thread(i0);

        mainCrawlerThread.setName("Thread 0");
        mainCrawlerThread.start();
        
        
        mainCrawlerThread.join();
        mainIndexerThread.start();
        mainIndexerThread.join();
    }
}
