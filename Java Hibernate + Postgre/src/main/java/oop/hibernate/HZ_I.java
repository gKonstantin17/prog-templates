package oop.hibernate;

import lombok.extern.log4j.Log4j2;
import oop.hibernate.dao.impl.UserDAOImpl;

@Log4j2
public class HZ_I {
    public static void main(String[] args) {

        // GET

//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        User user = session.find(User.class, 15002);
//        session.load(User.class,23);
//        log.info(user);
//        session.close();
//        HibernateUtil.close();


            // CACHE

//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        Query<User> query = session.createQuery("from User where id = 15003");
//        User user = query.getSingleResult();
//        log.info(user);
//
//        session.close();
//
//        session = HibernateUtil.getSessionFactory().openSession();
//
//        Query<User> query2 = session.createQuery("from User where id = 15003");
//        User user2 = query2.getSingleResult();
//        log.info(user2);
//
//        session.close();
//
//
//        log.info("hit" + HibernateUtil.getSessionFactory().getStatistics().getSecondLevelCacheHitCount());
//        log.info("miss" + HibernateUtil.getSessionFactory().getStatistics().getSecondLevelCacheMissCount());
//        log.info("put" + HibernateUtil.getSessionFactory().getStatistics().getSecondLevelCachePutCount());
//
//        for (String s : HibernateUtil.getSessionFactory().getStatistics().getSecondLevelCacheRegionNames())
//            log.info(s);
//        HibernateUtil.close();


        UserDAOImpl userDAO = new UserDAOImpl();

        log.info(userDAO.getByEmail("hellod@mail.com"));
        HibernateUtil.close();
    }
}
