package oop.hibernate.dao.impl;

import oop.hibernate.HibernateUtil;
import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.objs.RoleDAO;
import oop.hibernate.entity.Role;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class RoleDAOImpl implements RoleDAO {
    @Override
    public List<Role> get() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Role> query = session.createQuery("from Role", Role.class);
        List<Role> roles = query.getResultList();
        session.close();
        return roles;
    }

    @Override
    public Role get(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Role role = session.get(Role.class,id);
        session.close();
        return role;
    }

    @Override
    public void create(Role obj) {
        throw new IllegalStateException("u cant create");
    }

    @Override
    public void update(Role obj) {
        throw new IllegalStateException("u cant update");
    }

    @Override
    public void delete(long id) {
        throw new IllegalStateException("u cant delete");
    }
}
