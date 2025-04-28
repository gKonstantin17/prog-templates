package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.IEmail;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAOImpl implements CommonDAO<User>, IEmail<User> {
    @Override
    public List<User> get() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<User> query = session.createQuery("from User", User.class);
        List<User> users = query.getResultList();
        session.close();
        return users;
    }

    @Override
    public User getByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<User> query = session.createQuery("from User " +
                "where email = :email", User.class);
        query.setParameter("email", email);
        User user = query.getSingleResult();
        session.close();
        return user;
    }



    @Override
    public User get(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User user = session.get(User.class,id);
        session.close();
        return user;
    }

    @Override
    public void create(User obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.persist(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(User obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.merge(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        User u = new User();
        u.setId(id);
        session.remove(u);
        session.getTransaction().commit();
        session.close();
    }
}
