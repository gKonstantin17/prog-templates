package oop.hibernate.dao.interfaces;

import java.util.List;

public interface CommonDAO<T> {
    List<T> get();

    T get(long id);
    void create(T obj);
    void update(T obj);
    void delete(long id);
}
