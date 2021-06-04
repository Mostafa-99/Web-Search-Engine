package Backend.jpa.model;
import javax.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Words")
public class Word {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long ID;
	String Name;
	Float IDF;
	@OneToMany(mappedBy="word")
	private Set<Links> links;

	public Word(Long ID, String name) {
		this.ID = ID;
		Name = name;
	}
	public Word() {
	}
	public Set<Links> getLinks() {
		return links;
	}

	public void setLinks(Set<Links> links) {
		this.links = links;
	}

	public void setID(Long ID) {
		this.ID = ID;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setIdf(Float df) {
		this.IDF = df;
	}

	public Long getID() {
		return ID;
	}

	public String getName() {
		return Name;
	}

	public Float getIdf() {
		return IDF;
	}





}
