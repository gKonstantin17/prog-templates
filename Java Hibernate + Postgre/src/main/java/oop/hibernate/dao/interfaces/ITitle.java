package oop.hibernate.dao.interfaces;

import java.util.List;

public interface ITitle<T> {
    List<T> getByTitle(String title);
}
