package Backend.jpa.repository;

import Backend.jpa.model.Links;
import Backend.jpa.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LinkRepository extends JpaRepository<Links,String> {
    //han3adelha lama a3mel integrate el total count sabet
    @Query(value = "select count() from links " ,nativeQuery = true)
    public Float getAllLinkCount();
    @Query(value = "select count() from links where word_id=:WORDID",nativeQuery = true)
    public Float getLinkCountByID(@Param("WORDID")Long WORDID);
    @Query(value = "select count(*) from links where word_id=:WORDID",nativeQuery = true)
    public Long findCountByID(@Param("WORDID")Long WORDID);
    @Query(value = "select * from links where word_id=:WORDID",nativeQuery = true)
    public Links[] findALLLinks(@Param("WORDID")Long WORDID);
}
