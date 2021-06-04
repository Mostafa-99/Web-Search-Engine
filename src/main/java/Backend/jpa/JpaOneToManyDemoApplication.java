package Backend.jpa;

import Backend.jpa.model.Links;
import Backend.jpa.model.Word;
import Backend.jpa.repository.LinkRepository;
import Backend.jpa.repository.WordRepository;
import Backend.jpa.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing
public class JpaOneToManyDemoApplication  {

	@Autowired
	private WordRepository word_repo;

	@Autowired
	private LinkRepository link_repo;
	public static void main(String[] args) {
		SpringApplication.run(JpaOneToManyDemoApplication.class, args);

	}

	public void addWord(Long ID, String word)
	{
		Word w1=new Word(ID,word);
		w1.setIdf(0F);
		word_repo.save(w1);
	}
	public void addLink(String URL,long TF,long Plain,long Header,long Title,String Name)
	{
		Optional<Word> w1=word_repo.findWordByName(Name);
		if(!w1.isPresent())
		{
			addWord(23L,Name);
			Optional<Word> w2=word_repo.findWordByName(Name);
			Links l1=new Links(URL,TF,Plain,Header,Title);
			l1.setWord(w2.get());
			word_repo.save(w2.get());
			link_repo.save(l1);
			Float totalDocNum=link_repo.getAllLinkCount();
			Float totalDocNUMForWord=link_repo.getLinkCountByID(w2.get().getID());
			Float IDF=totalDocNum/totalDocNUMForWord;
			w2.get().setIdf(IDF);
			//w1.get().getLinks().add(l1);
			word_repo.save(w2.get());
			link_repo.save(l1);


		}
		else
		{
		Links l1=new Links(URL,TF,Plain,Header,Title);
		l1.setWord(w1.get());
		word_repo.save(w1.get());
		link_repo.save(l1);
		Float totalDocNum=link_repo.getAllLinkCount();
		Float totalDocNUMForWord=link_repo.getLinkCountByID(w1.get().getID());
		Float IDF=totalDocNum/totalDocNUMForWord;
		w1.get().setIdf(IDF);
		//w1.get().getLinks().add(l1);
		word_repo.save(w1.get());
		link_repo.save(l1);
		}
	}
//
//	public void run(String... args) throws Exception {
//		//addLink("www.google.com",1,2,3,4,"last test");
//		//addWord(23L, "www.wwww.com");
//	}

}
