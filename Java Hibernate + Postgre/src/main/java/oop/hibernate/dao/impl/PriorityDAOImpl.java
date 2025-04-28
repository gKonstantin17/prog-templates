package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.ITitle;
import oop.hibernate.dao.interfaces.objs.PriorityDAO;
import oop.hibernate.entity.Priority;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PriorityDAOImpl implements PriorityDAO {
    @Override
    public List<Priority> get() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Priority> query = session.createQuery("from Priority", Priority.class);
        List<Priority> priorities = query.getResultList();
        session.close();
        return priorities;
    }

    @Override
    public Priority get(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Priority priority = session.get(Priority.class,id);
        session.close();
        return priority;
    }
    @Override
    public List<Priority> getByTitle(String title) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Priority> query = session.createQuery("from Category " +
                "where title = :title", Priority.class);
        query.setParameter("title", title);
        List<Priority> priorities = query.getResultList();
        session.close();
        return priorities;
    }

    @Override
    public void create(Priority obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.persist(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(Priority obj) {
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
        Priority p = new Priority();
        p.setId(id);
        session.remove(p);
        session.getTransaction().commit();
        session.close();
    }


}
