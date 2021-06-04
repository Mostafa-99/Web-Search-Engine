package Backend.jpa.controller;

import Backend.jpa.services.LinkService;
import Backend.jpa.services.SearchService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import opennlp.tools.stemmer.PorterStemmer;


@RestController
@RequestMapping("/Length")
public class LinkEndPoint {
    @Autowired
    private SearchService searchservice;
    @Autowired
    private LinkService linkservice;

//    @RequestMapping(method = RequestMethod.GET, value = "/{WordName}")
//    public ResponseEntity<Long> getNumlinks(@PathVariable String WordName) {
//
//        return linkservice.GetLinksCount(WordName);
//    }

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(method = RequestMethod.GET, value = "/{WordName}")
public ResponseEntity<?> getNumlinks(@PathVariable String WordName) {
    String  string = WordName.toLowerCase().replaceAll("'|`|’|,", "").replaceAll("[^a-z]", " ").trim();

    try {
        if(loadStopwords().contains(string))
            string=null;

    } catch (IOException e) {
        e.printStackTrace();
    }
    PorterStemmer porterStemmer = new PorterStemmer();
    String word2 = porterStemmer.stem(string);
    return linkservice.GetLinksCount(word2);
}

public ArrayList<String> loadStopwords() throws IOException, FileNotFoundException {
    Scanner s = new Scanner(new File("./src/main/resources/stopWords.txt"));
    ArrayList<String> list = new ArrayList<String>();
    while (s.hasNext()){
        list.add(s.next());
    }
    s.close();
    return list;
}
}
