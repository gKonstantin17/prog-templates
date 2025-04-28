package oop.hibernate.dao.interfaces.objs;

import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.entity.Activity;
import oop.hibernate.entity.User;

public interface ActivityDAO extends CommonDAO<Activity> {
    Activity getByUser(String email);
    Activity getByUser(User user);
}
