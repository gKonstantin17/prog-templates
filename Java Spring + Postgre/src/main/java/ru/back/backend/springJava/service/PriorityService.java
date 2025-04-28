package ru.back.backend.springJava.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.back.backend.springproj.entity.Priority;
import ru.back.backend.springJava.repository.PriorityRepository;
import java.util.List;

@Service
@Transactional
public class PriorityService {
    PriorityRepository repository;
    public PriorityService(PriorityRepository repository) { this.repository = repository;}

    public List<Priority> findAll(String email) {
        return repository.findByUserEmailOrderByIdAsc(email);
    }

    public Priority add(Priority priority) {
        return repository.save(priority);
    }

    public Priority update(Priority priority) {
        return repository.save(priority);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Priority findById(Long id) {
        return repository.findById(id).get();
    }
    public List<Priority> find(String title, String email) {
        return repository.findByTitle(title,email);
    }
}
