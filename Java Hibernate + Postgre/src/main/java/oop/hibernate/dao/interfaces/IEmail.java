package oop.hibernate.dao.interfaces;

import java.util.List;

public interface IEmail<T> {
    T getByEmail(String email);
}
