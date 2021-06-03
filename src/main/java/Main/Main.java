package Main;

import Crawler.Crawler;

public class Main {
    static final int RESTART = 0;
    static final int RESUME = 1;
    static final int state = RESTART;
    static final int numberOfThreads = 5;
    static final int crawlingSize = 5;

    public static void main(String[] args) throws Exception {
        Crawler c0 = new Crawler(state,numberOfThreads, crawlingSize);
        //c0.crawlerMain();
        Thread mainCrawlerThread = new Thread(c0);
        mainCrawlerThread.setName("Thread 0");
        mainCrawlerThread.start();

        mainCrawlerThread.join();
    }
}