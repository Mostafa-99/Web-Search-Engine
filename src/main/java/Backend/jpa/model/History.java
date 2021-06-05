package Backend.jpa.model;
import javax.persistence.*;


@Entity
@Table(name = "History")
public class History {
    @Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long ID;
	String Word;
	public void setID(Long ID) {
		this.ID = ID;
	}
    public void setWord(String Word) {
		this.Word = Word;
	}
    
	public Long getID() {
		return ID;
	}

	public String getWord() {
		return Word;
	}
    
}
