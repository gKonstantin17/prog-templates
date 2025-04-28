package oop.hibernate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

@Log4j2
public class HQL_API {
    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        // SELECT


        Query query = session.createQuery("from User", User.class);
        List<User> users = query.getResultList();

       // log.info(users);


        // WHERE

        // в качестве 1 аргумента запрос
        // SELECT * User u
        // WHERE u.email like :text
        // где :text - параметр, который можно установить позже
        Query query = session.createQuery("from User u " +
                "where u.email like:text");
        query.setParameter("text", "%a%");
        // LIMIT - OFFSET
        query.setFirstResult(1);
        query.setMaxResults(5);
        List<User> users = query.getResultList();
        log.info(users);


        // UNIQUE (Едиственный) RESULT

//
        Query<User> query = session.createQuery("from User where id = :id");
        query.setParameter("id", 15002);
        User user = query.uniqueResult();
        log.info(user);



        // Агрегатные функции

        Query<Long> query = session.createQuery("select " +
                "count(u.id) from User u " +
                "where u.id > 15000");
        Long countUsers = query.uniqueResult();
        log.info(countUsers);


        // выбор нескольких полей
        Query<User> query = session.createQuery("select " +
                "new User(u.username, u.email) from User u " +
                "where id = :id", User.class);
        query.setParameter("id", 15002);
        User user = query.uniqueResult();
        log.info(user);



        Query<UserDTO> query = session.createQuery("select new UserDTO(u.id, u.email) from User u " +
                "where id > 15000", UserDTO.class);
        List<UserDTO> users = query.getResultList();
        log.info(users);
        session.close();
        HibernateUtil.close();


    }
}
@Data
@AllArgsConstructor
class UserDTO {
    private Long id;
    private String email;
}
