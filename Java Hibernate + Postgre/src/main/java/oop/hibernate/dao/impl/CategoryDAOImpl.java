package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.ITitle;
import oop.hibernate.dao.interfaces.objs.CategoryDAO;
import oop.hibernate.entity.Category;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    @Override
    public List<Category> get() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Category> query = session.createQuery("from Category", Category.class);
        List<Category> categories = query.getResultList();
        session.close();
        return categories;
    }

    @Override
    public Category get(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Category category = session.get(Category.class,id);
        session.close();
        return category;
    }
    @Override
    public List<Category> getByTitle(String title) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Category> query = session.createQuery("from Category " +
                "where title = :title", Category.class);
        query.setParameter("title", title);
        List<Category> categories = query.getResultList();
        session.close();
        return categories;
    }

    @Override
    public void create(Category obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.persist(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(Category obj) {
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
        Category c = new Category();
        c.setId(id);
        session.remove(c);
        session.getTransaction().commit();
        session.close();
    }


}
