package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.objs.StatDAO;
import oop.hibernate.entity.Stat;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class StatDAOImpl implements StatDAO {
    @Override
    public Stat getByUser(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Stat> query = session.createQuery("from Stat " +
                "where user.email = :email", Stat.class);
        query.setParameter("email", email);
        Stat stat = query.getSingleResult();
        session.close();
        return stat;
    }

    @Override
    public Stat getByUser(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Stat> query = session.createQuery("from Stat " +
                "where user.email = :email", Stat.class);
        query.setParameter("email", user.getEmail());
        Stat stat = query.getSingleResult();
        session.close();
        return stat;
    }
}
