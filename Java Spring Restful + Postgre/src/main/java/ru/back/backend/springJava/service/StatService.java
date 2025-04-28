package ru.back.backend.springJava.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.back.backend.springproj.entity.Stat;
import ru.back.backend.springproj.repository.StatRepository;

@Service
@Transactional
public class StatService {
    private StatRepository repository;
    public StatService(StatRepository repository) { this.repository = repository;}

    public Stat findByEmail(String email) {
        return repository.findByUserEmail(email);
    }
}
