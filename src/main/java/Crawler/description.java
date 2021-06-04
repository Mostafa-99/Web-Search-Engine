package Crawler;

import java.io.IOException;

import javax.sound.sampled.SourceDataLine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Description {
    public static int countMatches(String text, String str)
    {
        if (text.length()==0 || str.length()==0) {
            return 0;
        }
 
        int index = 0, count = 0;
        while (true)
        {
            index = text.indexOf(str, index);
            if (index != -1)
            {
                count ++;
                index += str.length();
            }
            else {
                break;
            }
        }
 
        return count;
    }
    public static void main(String args[])
    {
        Document doc;
        try {
            // doc = Jsoup.connect("https://www.amazon.com/").get();
            // doc = Jsoup.connect("https://www.youtube.com/").get();
            // doc = Jsoup.connect("https://github.com/").get();
            
            // doc = Jsoup.connect("https://www.google.com/").get();
            

doc = Jsoup.connect("https://www.geeksforgeeks.org/analysis-of-algorithems-little-o-and-little-omega-notations/?ref=ghm").get();
            
System.out.println(doc.title());

Elements desc = doc.select("meta[name=description]"); 
System.out.print("desc: " + desc.attr("content"));   


            // String text = doc.body().text();
            // String str = "featured";
            // int count = countMatches(text, str);
            // System.out.println(count);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
