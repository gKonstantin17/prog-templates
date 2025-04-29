package micros.plannertodo.service;


import jakarta.transaction.Transactional;
import micros.plannertodo.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import micros.plannerentity.entity.Category;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category findById(Long id) {
        return repository.findById(id).get();
    }
    public String findTitleById(Long id) {
        return repository.findById(id).get().getTitle();
    }

    public List<Category> findAll(String id) {
        return repository.findByUserIdOrderByTitleAsc(id);
    }

    public Category add(Category category) {
        return repository.save(category);
    }

    public Category update(Category category) {
        return repository.save(category);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    public List<Category> findByTitle(String title, String id) {
        return repository.findByTitle(title,id);
    }
//    public List<CategoryDto> findAll(String email) {
//        List<Category> categories = repository.findByUserEmailOrderByTitleAsc(email);
//        List<CategoryDto> categoryDtos = categories.stream()
//                .map(value -> new CategoryDto(
//                        value.getId(),
//                        value.getTitle(),
//                        value.getCompletedCount(),
//                        value.getUncompletedCount(),
//                        value.getUser() != null ? value.getUser().getId() : null
//                )).toList();
//
//        return categoryDtos;
//    }
}
