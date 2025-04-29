package micros.plannertodo.service;

import jakarta.transaction.Transactional;
import micros.plannerentity.entity.Stat;
import micros.plannertodo.repository.StatRepository;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class StatService {
    private StatRepository repository;
    public StatService(StatRepository repository) { this.repository = repository;}

    public Stat findByEmail(String id) {
        return repository.findByUserId(id);
    }
}
