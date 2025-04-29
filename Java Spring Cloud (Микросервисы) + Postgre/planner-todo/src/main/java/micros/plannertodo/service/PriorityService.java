package micros.plannertodo.service;

import jakarta.transaction.Transactional;
import micros.plannerentity.entity.Priority;
import micros.plannertodo.repository.PriorityRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class PriorityService {
    PriorityRepository repository;
    public PriorityService(PriorityRepository repository) { this.repository = repository;}

    public List<Priority> findAll(String id) {
        return repository.findByUserIdOrderByIdAsc(id);
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
    public List<Priority> find(String title, String id) {
        return repository.findByTitle(title,id);
    }
}
