package micros.plannertodo.service;

import jakarta.transaction.Transactional;
import micros.plannerentity.entity.Task;
import micros.plannertodo.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private TaskRepository repository;
    public TaskService(TaskRepository repository) {this.repository = repository;}
    public List<Task> findAll(String id) {
        return repository.findByUserIdOrderByTitleAsc(id);
    }

    public Task add(Task task) {
        return repository.save(task);
    }

    public Task update(Task task) {
        return repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<Task> findByParam(String title, Boolean completed,
                                  Long priorityId, Long categoryId,
                                  Date dateFrom, Date dateTo,
                                  String id, PageRequest paging)
    {
        return repository.findByParams(title,completed,priorityId,
                categoryId, dateFrom, dateTo,id,paging);
    }

    public Task findById(Long id) {
        return repository.findById(id).get();
    }
}
