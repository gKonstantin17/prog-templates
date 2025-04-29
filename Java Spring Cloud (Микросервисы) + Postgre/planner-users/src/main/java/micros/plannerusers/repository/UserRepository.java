package micros.plannerusers.repository;

import org.springframework.stereotype.Repository;

//@Repository
//public interface UserRepository
// extends JpaRepository<User,Long>
//{
//    User findByEmail(String email);
//    void deleteByEmail(String email);
//    @Query("SELECT u FROM User u  " +
//            "where (:email is null or :email='' or lower(u.email) like lower(concat('%', :email,'%'))) " +
//            "and (:username is null or :username='' or lower(u.username) like lower(concat('%', :username,'%')))")
//    Page<User> findByParam(@Param("email") String email,
//                           @Param("username") String username,
//                           Pageable pageable);
//}
