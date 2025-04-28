package oop.hibernate;

import lombok.extern.log4j.Log4j2;
import oop.hibernate.entity.User;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.util.List;

@Log4j2
public class NativeSQL {
    public static void main(String[] args) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        String qry = "select * from todolist.user_data";
//        NativeQuery<User> sqlQuery = session.createNativeQuery(qry, User.class);
//        sqlQuery.addEntity(User.class);
//
//        sqlQuery.setMaxResults(10);
//
//        List<User> list = sqlQuery.list();
//        log.info(list);
//
//
//        session.close();
//        HibernateUtil.close();

        Session session = HibernateUtil.getSessionFactory().openSession();
        String qry = "select count(u.id), " +
                "subString(u.email, position('@' in u.email)+1, length(u.email)) as domainemail " +
                "from todolist.user_data u " +
                "where u.email like '%@%' " +
                "group by substring(u.email, position('@' in u.email)+1, length(u.email))";
        NativeQuery query = session.createNativeQuery(qry);
        for (Object obj : query.getResultList())
        {
            Object[] objArray = (Object[]) obj;
            log.info(objArray[0]);
            log.info(objArray[1]);
            log.info("---------");
        }

        // ИЛИ не делать преобразование

        NativeQuery<Object[]> query2 = session.createNativeQuery(qry);
        for (Object[] obj : query2.getResultList())
        {
            log.info(obj[0]);
            log.info(obj[1]);
            log.info("---------");
        }

        session.close();
        HibernateUtil.close();
    }
}
