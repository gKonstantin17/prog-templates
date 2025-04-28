package oop.hibernate.dao.interfaces.objs;

import oop.hibernate.entity.Stat;
import oop.hibernate.entity.User;

public interface StatDAO {
    Stat getByUser(String email);
    Stat getByUser(User user);
}
