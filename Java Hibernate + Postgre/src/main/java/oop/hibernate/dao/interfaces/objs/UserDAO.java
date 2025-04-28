package oop.hibernate.dao.interfaces.objs;

import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.IEmail;
import oop.hibernate.entity.User;

public interface UserDAO extends CommonDAO<User>, IEmail<User> {

}
