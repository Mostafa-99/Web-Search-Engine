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


public class Crawler {
    int pageNumbers = 1;
    String downloadPath = "./downloaded/page"+pageNumbers+".html";

    public void download(String urlString) throws IOException {

        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            BufferedWriter writer = new BufferedWriter(new FileWriter(downloadPath));
            pageNumbers++;
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
            writer.close();
            System.out.println("Page downloaded.");
        } catch (MalformedURLException e) {
            System.out.println("Error");
        } catch (IOException io) {
            System.out.println("Error");
        }


    }
    void getURLs(){
        Document doc;
        String title="";
        Elements links;
        try {
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(downloadPath));
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
            for(int j=0;j<150;j++){
                link=links.get(j);
                String linkHref = link.attr("abs:href").toString();
                if(linkHref!=""){
                    System.out.println("a= " +linkHref);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Jsoup Can read HTML page from URL, title : " +title);
        downloadPath = "./downloaded/page"+pageNumbers+".html";


    }
    /*private static String normalizeURI(String uri) {
        return URI.create(uri).normalize().toString();
    }*/

    public static void main(String args[]) throws IOException {
        Crawler c =new Crawler();
        try {
            File myObj = new File("./seeds/seeds.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println("Crawling: "+data);
                c.download(data);
                c.getURLs();
                System.out.println("*****************************************************************************");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
