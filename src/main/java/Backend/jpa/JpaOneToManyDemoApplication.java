package Backend.jpa;

import Backend.jpa.model.Word;
import Backend.jpa.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class JpaOneToManyDemoApplication  {

	@Autowired
	private WordRepository word_repo;

	public static void main(String[] args) {
		SpringApplication.run(JpaOneToManyDemoApplication.class, args);

	}

	public void addWord(Long ID, String word)
	{
		Word w1=new Word(ID,word);
		w1.setIdf(0F);
		word_repo.save(w1);
	}

}
