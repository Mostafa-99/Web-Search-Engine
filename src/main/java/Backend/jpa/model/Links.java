package Backend.jpa.model;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "Links")
public class Links {
    public void setWord(Word word) {
        this.word = word;
    }

    @ManyToOne
    @JoinColumn(name="word_id", nullable=false)
    private Word word;


    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long ID;
    String URL;
    long TF;
    long Plain;
    long Header;
    long Title;

    public Links() { }

    public Links(String URL, long TF, long plain, long header, long title) {
        this.URL = URL;
        this.TF = TF;
        Plain = plain;
        Header = header;
        Title = title;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getURL() {
        return URL;
    }

    public long getTF() {
        return TF;
    }

    public long getPlain() {
        return Plain;
    }

    public long getHeader() {
        return Header;
    }

    public long getTitle() {
        return Title;
    }


    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setTF(long TF) {
        this.TF = TF;
    }

    public void setPlain(long plain) {
        Plain = plain;
    }

    public void setHeader(long header) {
        Header = header;
    }

    public void setTitle(long title) {
        Title = title;
    }


}
