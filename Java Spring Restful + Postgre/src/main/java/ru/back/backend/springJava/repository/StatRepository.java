package ru.back.backend.springJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.back.backend.springproj.entity.Stat;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    Stat findByUserEmail(String email);
}
