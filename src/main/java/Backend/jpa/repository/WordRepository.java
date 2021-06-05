package Backend.jpa.repository;
import Backend.jpa.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word,Long> {

	@Query(value = "SELECT * FROM words where name=:word" ,nativeQuery = true)
    public Optional<Word> findWordByName(@Param("word")String word);
    @Query(value = "SELECT ID FROM words where name=:word" ,nativeQuery = true)
    public Long findIdbyName(@Param("word")String word);
}
