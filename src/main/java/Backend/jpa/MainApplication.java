package Backend.jpa;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class MainApplication  {

	public static void main(String[] args) {
		//History_Repo.deleteAll();
		SpringApplication.run(MainApplication.class, args);

	}


}
