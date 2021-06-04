package Backend.jpa.services;
import Backend.jpa.model.Links;
import Backend.jpa.model.Word;
// import Backend.jpa.model.searchPagingModel;
import Backend.jpa.repository.LinkRepository;
import Backend.jpa.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class SearchService {
	@Autowired
	private WordRepository word_repo;
	@Autowired
	private LinkRepository link_repo;
	@Autowired
	private LinkService linkservice;


	public ResponseEntity<?> GetLinks(String word, int pageNumber, int pageSize) {

		Long ID = word_repo.findIdbyName(word);
		Links Links[] = link_repo.findALLLinks(ID);
		//int x=pageNumber*pageSize;
		Set <Links>LinksPaged = new HashSet<>();
		for(int i=pageNumber*pageSize;i<(pageNumber*pageSize)+pageSize;i++)
		{
			if(i>=Links.length)
				break;
			else
				LinksPaged.add(Links[i]);
		}
		return new ResponseEntity<>(LinksPaged, HttpStatus.OK);
	}
	
	public ResponseEntity<?> searchWordSuggestion(String word, int pageSize)
    {
        Word Words []=word_repo.searchByName(word);
        Set<Word> searchedWords=new HashSet<>();
        for(int i=0;i<pageSize;i++)
        {
            if(i>=Words.length)
                break;
            else
            searchedWords.add(Words[i]);
        }
        return new ResponseEntity<>(searchedWords, HttpStatus.OK);
    }
}

