package oop.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

// класс для инициализации Hibernate
public class HibernateUtil {
    // фабрика для создания сессий (соединений)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    // метод вызывается автоматически т.к. вызывается из статичной переменной
    private static SessionFactory buildSessionFactory() {
        try {
            // ссылка на конфиг Hibernate
            return new Configuration().configure(new File("src\\main\\resources\\hibernate.cfg.xml")).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Init SessionFactory creation failsd: "+ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static SessionFactory getSessionFactory() {return sessionFactory;}

    // закрыть все соединения
    public static void close() {
        getSessionFactory().close();
    }

}
