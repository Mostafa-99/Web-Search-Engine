package Backend.jpa.services;
import Backend.jpa.model.History;
import Backend.jpa.model.Links;
import Backend.jpa.repository.HistoryRepository;
import Backend.jpa.repository.LinkRepository;
import Backend.jpa.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class SearchService {
	@Autowired
	private WordRepository word_repo;
	@Autowired
	private LinkRepository link_repo;
	@Autowired
	private HistoryRepository History_Repo;



	public ResponseEntity<?> GetLinks(String wordOrig,String word, int pageNumber, int pageSize) throws IOException {

		Long ID = word_repo.findIdbyName(word);
		Links Links[] = link_repo.findALLLinks(ID);
		//int x=pageNumber*pageSize;
		Set <JSONObject>jsonObjects = new HashSet<>();
		String URL=null;
		long TF=0;
		long Plain=0;
		long Header=0;
		long TitleNUM=0;
		String Title=null;
		Elements desc;
		Document doc;
		String description ="";
		for(int i=pageNumber*pageSize;i<(pageNumber*pageSize)+pageSize;i++)
		{
			if(i>=Links.length)
				break;
			else
			{

				JSONObject json = new JSONObject();
				URL=Links[i].getURL();
				TF=Links[i].getTF();
				Plain=Links[i].getPlain();
				Header=Links[i].getHeader();
				TitleNUM=Links[i].getTitle();
				doc = Jsoup.connect(URL).get();
				Title=doc.title();
				desc = doc.select("meta[name=description]");
				///////////////////////////////////////////////////////////////

				Elements elements = doc.body().select("*");
				String paragraph = "";
				for (Element element : elements) {
					if(element.ownText().contains(wordOrig)){
						
						String temp = element.ownText();
						if(temp.endsWith(".")){
							temp = temp.replaceAll("\\.",". ");
						}
						else{
							temp += ". ";
						}
						paragraph+=temp;
						
						//System.out.println(element.ownText());
					}
				}
				if((paragraph).length()>1000){
					paragraph = paragraph.substring(0, 200);
				}
				paragraph = paragraph.trim().replaceAll(" +", " ");
		
				//System.out.println("OUT: "+paragraph);
				///////////////////////////////////////////////////////////////
				json.put("URL", URL);
				json.put("TF",TF);
				json.put("Plain", Plain);
				json.put("Header",Header);
				json.put("Title Number",TitleNUM);
				//json.put("description", desc.attr("content"));
				json.put("description", paragraph);
				json.put("Title", Title);
				jsonObjects.add(json);
				
				
			}
				//LinksPaged.add(Links[i]);
		}
		return new ResponseEntity<>(jsonObjects, HttpStatus.OK);
	}
	
	public ResponseEntity<?> searchWordSuggestion(String word, int pageSize)
    {
        History h1 []=History_Repo.searchByName(word);
        Set<History> searchedWords=new HashSet<>();
        for(int i=0;i<pageSize;i++)
        {
            if(i>=h1.length)
                break;
            else
            searchedWords.add(h1[i]);
        }
        return new ResponseEntity<>(searchedWords, HttpStatus.OK);
    }
}

