package dk.lyngby.controller;

import io.javalin.http.Context;

public interface IController<T, D> {
    void read(Context ctx);
    void readAll(Context ctx);
    void create(Context ctx);
    void update(Context ctx);
    void delete(Context ctx);

    //her tjekker vi om det er et valid hotel objekt
    boolean validatePrimaryKey(Integer integer);


    T validateEntity(Context ctx);

}
