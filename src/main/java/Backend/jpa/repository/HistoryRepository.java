package Backend.jpa.repository;

import Backend.jpa.model.History;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {
    @Query(value="SELECT * FROM History as H WHERE H.word like :search% LIMIT 10",nativeQuery=true)
    public  History []searchByName(@Param("search")String search );
    @Query(value="SELECT word FROM History as H WHERE H.word =:search LIMIT 1",nativeQuery=true)
    public Optional<String> findByName(@Param("search")String search);
}