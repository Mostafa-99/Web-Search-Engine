package Backend.jpa.repository;

import Backend.jpa.model.Links;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface LinkRepository extends JpaRepository<Links,String> {
    
    @Query(value = "select count() from links " ,nativeQuery = true)
    public Float getAllLinkCount();
    @Query(value = "select count() from links where word_id=:WORDID",nativeQuery = true)
    public Float getLinkCountByID(@Param("WORDID")Long WORDID);
    @Query(value = "select count(*) from links where word_id=:WORDID",nativeQuery = true)
    public Long findCountByID(@Param("WORDID")Long WORDID);
    @Query(value = "select * from links where word_id=:WORDID LIMIT 10 OFFSET :pageNo",nativeQuery = true)
    public Links[] findALLLinks(@Param("WORDID")Long WORDID,@Param("pageNo")int pageNo);
}
