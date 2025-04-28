package ru.back.backend.springJava.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.back.backend.springproj.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByUserEmailOrderByTitleAsc(String email);

    // если title - пустой, то WHERE true - отключение фильтрации
    // здесь 3 выражения через or
    // если первое/второе - то where true
    // если третье - перебирает title в нижнем регистре с другими с нижнем регистре
    @Query("SELECT c FROM Category c " +
            "where (:title is null or :title='' " +
            "or lower(c.title) like lower(concat('%', :title, '%'))) " +
            "and c.user.email= :email " +
            "order by c.title asc")
    List<Category> findByTitle(@Param("title") String title, @Param("email") String email);

}
