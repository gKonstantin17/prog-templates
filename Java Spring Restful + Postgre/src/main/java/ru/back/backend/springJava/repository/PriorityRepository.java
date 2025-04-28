package ru.back.backend.springJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.back.backend.springproj.entity.Priority;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority,Long> {
    @Query("select p from Priority p " +
            "where (:title = null or :title='' or " +
            "lower(p.title) like lower(concat('%',:title,'%'))) " +
            "and p.user.email = :email " +
            "order by p.title asc")
    List<Priority> findByTitle(@Param("title") String title, @Param("email") String email);

    List<Priority> findByUserEmailOrderByIdAsc(String email);
}
