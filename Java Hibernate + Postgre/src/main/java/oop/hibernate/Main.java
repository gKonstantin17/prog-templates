package oop.hibernate;
import lombok.extern.log4j.Log4j2;
import oop.hibernate.entity.User;
import org.hibernate.Session;

@Log4j2
public class Main {
    public static void main(String[] args) {
        log.info("Hibernate started");
        // сразу получаем готовый SessionFactory и создаем сессию
        // как только создается сессия, сразу считывается файл конфигурации
        Session session = HibernateUtil.getSessionFactory().openSession();

        // выполнить транзакцию по созданию пользователя
        session.getTransaction().begin(); // начало транзакции
        User user = new User(); //  INSERT
        user.setEmail("hellod@mail.com");
        user.setUserpassword("skldsdf");
        user.setUsername("короче, меченый");
        session.persist(user);
        session.getTransaction().commit(); // конец транзакции

        System.out.println("USER ID: "+ user.getId());

        session.close();
        HibernateUtil.close();
    }
}