package oop.hibernate.dao.interfaces.objs;

import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.ITitle;
import oop.hibernate.entity.Category;

public interface CategoryDAO extends CommonDAO<Category>, ITitle<Category> {
}
