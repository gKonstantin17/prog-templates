package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.ITitle;
import oop.hibernate.dao.interfaces.objs.TaskDAO;
import oop.hibernate.entity.Task;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class TaskDAOImpl implements TaskDAO {
    @Override
    public List<Task> get() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Task> query = session.createQuery("from Task", Task.class);
        List<Task> tasks = query.getResultList();
        session.close();
        return tasks;
    }

    @Override
    public Task get(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Task task = session.get(Task.class,id);
        session.close();
        return task;
    }
    @Override
    public List<Task> getByTitle(String title) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Task> query = session.createQuery("from Task " +
                "where title = :title", Task.class);
        query.setParameter("title", title);
        List<Task> tasks = query.getResultList();
        session.close();
        return tasks;
    }

    @Override
    public void create(Task obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.persist(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(Task obj) {
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
        Task t = new Task();
        t.setId(id);
        session.remove(t);
        session.getTransaction().commit();
        session.close();
    }


    @Override
    public List<Task> get(boolean completed, String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<Task> query = session.createQuery("from Task t " +
                "where t.completed = :completed and " +
                "t.user.email = :email");
        query.setParameter("completed", completed);
        query.setParameter("email", email);
        List<Task> tasks = query.getResultList();
        session.close();
        return tasks;
    }
}
