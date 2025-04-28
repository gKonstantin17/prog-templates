package oop.hibernate.dao.interfaces.objs;

import oop.hibernate.dao.interfaces.CommonDAO;
import oop.hibernate.dao.interfaces.ITitle;
import oop.hibernate.entity.Task;

import java.util.List;

public interface TaskDAO extends CommonDAO<Task>, ITitle<Task> {
    List<Task> get(boolean completed, String email);
}
