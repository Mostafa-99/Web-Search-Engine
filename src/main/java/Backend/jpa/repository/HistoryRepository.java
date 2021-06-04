package Backend.jpa.repository;

import Backend.jpa.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {
    @Query(value="SELECT * FROM History as H WHERE H.word like :search% LIMIT 10",nativeQuery=true)
    public  History []searchByName(@Param("search")String search );
}