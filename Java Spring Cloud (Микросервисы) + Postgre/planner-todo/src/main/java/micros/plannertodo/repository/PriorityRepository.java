package micros.plannertodo.repository;

import micros.plannerentity.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority,Long> {
    @Query("select p from Priority p " +
            "where (:title = null or :title='' or " +
            "lower(p.title) like lower(concat('%',:title,'%'))) " +
            "and p.userId = :userId " +
            "order by p.title asc")
    List<Priority> findByTitle(@Param("title") String title, @Param("userId") String userId);

    List<Priority> findByUserIdOrderByIdAsc(String id);
}
