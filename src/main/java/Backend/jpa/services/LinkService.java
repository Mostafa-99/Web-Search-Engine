package Backend.jpa.services;

import Backend.jpa.repository.LinkRepository;
import Backend.jpa.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

@Service
public class LinkService {
    @Autowired
    private WordRepository word_repo;
    @Autowired
    private LinkRepository link_repo;
    public ResponseEntity<?> GetLinksCount(String word)
    {
        Long ID=word_repo.findIdbyName(word);
        Long Count=link_repo.findCountByID(ID);
        Hashtable<String,Long>My_Dic=new Hashtable<>();
        My_Dic.put("Count",Count);
        return new ResponseEntity<>(My_Dic,HttpStatus.OK);
    }

}
