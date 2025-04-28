package oop.hibernate;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CriteriaAPIi {
    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder(); // фабрика
        CriteriaQuery<User> cq_user = cb.createQuery(User.class); // объект
        Root<User> root = cq_user.from(User.class); // куда делать запросы

        // SELECT


         cq_user.select(root); // запрос root (user) - select

        // WHERE


        // исп писать условие в запросе
        // gt >, lt <
        cq_user.select(root).where(cb.gt(root.get("id"),15000)); // select where id > 15000

        // записать условия (предикаты) в переменные
//
        Predicate p1 = cb.gt(root.get("id"),5100); // > 5100
        Predicate p2 = cb.lt(root.get("id"),5200); // < 5200
        cq_user.select(root).where(cb.and(p1,p2));


        Query query = session.createMutationQuery(cq_user); // выполнить запрос
        List users = query.getResultList(); // сохранить результат запроса в users
        System.out.println(users);


        // DELETE

        CriteriaBuilder cb2 = session.getCriteriaBuilder();
        CriteriaDelete<User> cd_user = cb.createCriteriaDelete(User.class);
        Root<User> root2 = cd_user.from(User.class);
        cd_user.where(cb.equal(root2.get("id"),15001)); // where id = 15001

        Transaction trans = session.beginTransaction();
        session.createMutationQuery(cd_user).executeUpdate();
        trans.commit();


        // UPDATE


        CriteriaBuilder cb3 = session.getCriteriaBuilder();
        CriteriaUpdate<User> cu_user = cb.createCriteriaUpdate(User.class);
        Root<User> root3 = cu_user.from(User.class);
        // UPDATE root (user_data)
        cu_user.set("email","МЫЛО");// SET email = 'МЫЛО'
        cu_user.where(cb3.equal(root3.get("id"),15002)); // WHERE

        Transaction trans2 = session.beginTransaction();
        session.createMutationQuery(cu_user).executeUpdate();
        trans2.commit();

        session.close();
        HibernateUtil.close();
    }
}
