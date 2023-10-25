package dk.lyngby.dao;

import java.util.List;

public interface IDAO<T, D> {

    T read(D d);
    List<T> readAll();
    T create(T t);

    <T2> T2 update(Integer id, T2 entity, Class<T2> entityType);

    //T update(D key, T entity);

    boolean delete(D d);
    boolean validatePrimaryKey(D d);

}
