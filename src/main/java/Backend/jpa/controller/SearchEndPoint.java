package Backend.jpa.controller;

import Backend.jpa.services.LinkService;
import Backend.jpa.services.SearchService;
import opennlp.tools.stemmer.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// import com.example.jpa.services.LinkService;
// import com.example.jpa.services.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/Search")
public class SearchEndPoint {
    @Autowired
    private SearchService searchservice;
    @Autowired
    private LinkService linkservice;

	@CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(method = RequestMethod.GET, value = "/{Word}")
    public ResponseEntity<?> getlinks(@PathVariable String Word,
                                      @RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {

        String  string = Word.toLowerCase().replaceAll("'|`|’|,", "").replaceAll("[^a-z]", " ").trim();

        try {
            if(loadStopwords().contains(string))
                string=null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        PorterStemmer porterStemmer = new PorterStemmer();
        String word2 = porterStemmer.stem(string);
        return searchservice.GetLinks(word2,pageNo,pageSize);
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







/*@RestController
@RequestMapping("/Search")
public class SearchEndPoint {
	@Autowired
	private SearchService searchservice;
	@Autowired
	private LinkService linkservice;

	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(method = RequestMethod.GET, value = "/{Word}")
	public ResponseEntity<?> getlinks(@PathVariable String Word,
									  @RequestParam(defaultValue = "0") Integer pageNo,
									  @RequestParam(defaultValue = "10") Integer pageSize) {

		return searchservice.GetLinks(Word,pageNo,pageSize);
	}



}*/
