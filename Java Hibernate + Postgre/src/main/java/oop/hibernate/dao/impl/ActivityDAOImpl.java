package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.objs.ActivityDAO;
import oop.hibernate.entity.Activity;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ActivityDAOImpl implements ActivityDAO {
    @Override
    public List<Activity> get() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Activity> query = session.createQuery("from Activity", Activity.class);
        List<Activity> activities = query.getResultList();
        session.close();
        return activities;
    }

    @Override
    public Activity get(long id) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Activity activity = session.get(Activity.class,id);
        session.close();
        return activity;
    }

    @Override
    public Activity getByUser(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Activity> query = session.createQuery("from Activity " +
                "where user.email = :email", Activity.class);
        query.setParameter("email",email);
        Activity activity = query.getSingleResult();
        session.close();
        return activity;
    }

    @Override
    public Activity getByUser(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Activity> query = session.createQuery("from Activity " +
                "where user.email = :email", Activity.class);
        query.setParameter("email",user.getEmail());
        Activity activity = query.getSingleResult();
        session.close();
        return activity;

    }
    @Override
    public void create(Activity obj) {
       throw new IllegalStateException("u cant create");
    }

    @Override
    public void update(Activity obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.merge(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(long id) {
        throw new IllegalStateException("u cant delete");
    }


}
