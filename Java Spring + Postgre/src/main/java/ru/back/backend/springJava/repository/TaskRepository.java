package ru.back.backend.springJava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.back.backend.springproj.entity.Task;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    // поля - или null, или введенные
    // дата сравнивается с указанным промежутком
    // и обязательно email пользователя
    @Query("select t from Task t " +
            "where (:title is null or :title ='' or lower(t.title) like lower(concat('%',:title,'%'))) " +
            "and (:completed is null or t.completed=:completed) " +
            "and (:priorityId is null or t.priority.id = :priorityId) " +
            "and (:categoryId is null or t.category.id = :categoryId) " +
            "and ( " +
                "(cast(:dateFrom as timestamp) is null or t.taskDate >= :dateFrom) " +
                "and (cast (:dateTo as timestamp) is null or t.taskDate <=: dateTo )" +
            ") and " +
            "(t.user.email= :email)")
    Page<Task> findByParams(@Param("title") String title,
                            @Param("completed") Boolean completed,
                            @Param("priorityId") Long priorityId,
                            @Param("categoryId") Long categoryId,
                            @Param("dateFrom") Date dateFrom,
                            @Param("dateTo") Date dateTo,
                            @Param("email") String email,
                            Pageable pageable);// настройки для page

    // page имеет методы
    // getTotalPages()
    // getTotalElements()
    // getContent()
    // getSort()
    // hasNext()
    // hasPrevious()
    List<Task> findByUserEmailOrderByTitleAsc(String email);
}
