package micros.plannerusers.service;


import org.springframework.stereotype.Service;

import java.util.Optional;


//@Service
////@Transactional
//public class UserService {
//    private final UserRepository repository;
//    public UserService(UserRepository repository) {this.repository = repository;}
//
//    public User findByEmail(String email)
//    {
//        return repository.findByEmail(email);
//    }
//
//    public User add(User user) {
//        return repository.save(user);
//    }
//
//    public User update(User user) {
//        return repository.save(user);
//    }
//
//    public void deleteByUserId(Long id) {
//        repository.deleteById(id);
//    }
//
//    public void deleteByUserEmail(String email) {
//        repository.deleteByEmail(email);
//    }
//
//    public Optional<User> findById(Long id){
//        return repository.findById(id);
//    }
//
//    public Page<User> findByParams(String email, String username, PageRequest paging) {
//        return repository.findByParam(email,username,paging);
//    }
//}
