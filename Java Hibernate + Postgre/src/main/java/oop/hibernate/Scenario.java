package oop.hibernate;

import lombok.extern.log4j.Log4j2;
import oop.hibernate.dao.impl.*;
import oop.hibernate.entity.*;

import java.sql.Timestamp;
import java.util.Date;

@Log4j2
public class Scenario {
    public static void main(String[] args) {
//        создаем пользователя (триггеры создадут сразу же тестовые данные)
//        активируем пользователя (поле activated)
//        создаем новую категорию
//        создаем новый приоритет
//        создаем несколько новых задач (помимо тестовых) с новыми категорией и приоритетом
//        завершаем задачу
//        удаляем задачу
//        считываем статистику по любой категории пользователя
//        считываем общую статистику пользователя



//        // создаем пользователя (триггеры создадут сразу же тестовые данные)
//        User user = new User();
//        user.setUsername("ENDTEST");
//        user.setUserpassword("testuser");
//        user.setEmail("ENDTEST@gmail.com");
//
//        UserDAOImpl userDAO = new UserDAOImpl();
//        userDAO.create(user);
//
//        log.info(user.getId()); // 15036



        // активируем пользователя (поле activated)
//        UserDAOImpl userDAO = new UserDAOImpl();
//        User user = userDAO.get(15036);
//
//        ActivityDAOImpl activityDAO = new ActivityDAOImpl();
//        Activity activity = activityDAO.getByUser(user);
//        activity.setActivated(true);
//        activityDAO.update(activity);
//        log.info(activity.isActivated());

//
//        // создаем справочные значения
        UserDAOImpl userDAO = new UserDAOImpl();
        User user = userDAO.get(15036);
        PriorityDAOImpl priorityDAO = new PriorityDAOImpl();

        Priority priority = new Priority();
        priority.setColor("#fff");
        priority.setTitle("Новый приоритетnn");
        priority.setUser(user);
        priorityDAO.create(priority);
        log.info(priority.getTitle());




        CategoryDAOImpl categoryDAO = new CategoryDAOImpl();

        Category category = new Category();
        category.setTitle("Новая категорияzz");
        category.setUser(user);
        categoryDAO.create(category);
        log.info(category.getTitle());







        TaskDAOImpl taskDAO = new TaskDAOImpl();

        Task task = new Task();
        task.setUser(user);
        task.setTitle("Супер новая задача222");
        task.setCategory(category);
        task.setPriority(priority);
        task.setTaskDate(Timestamp.valueOf("2022-01-01 00:00:00"));
        task.setCompleted(false);
        taskDAO.create(task);

        task.setCompleted(true);
        taskDAO.update(task);

        taskDAO.delete(task.getId());

        StatDAOImpl statDAO = new StatDAOImpl();
        Stat stat = statDAO.getByUser(user);

        log.info(stat.getCompletedTotal());

        log.info(category.getCompletedCount());










        HibernateUtil.close();
    }
}
