package dk.lyngby.controller.impl;

import dk.lyngby.controller.IController;
import dk.lyngby.dao.IDAO;
import io.javalin.http.Context;
import java.util.List;

public abstract class BaseController<T, K> implements IController<T, K> {

    public final IDAO<T, K> dao;
    private final Class<T> entityClass;

    public BaseController(IDAO<T, K> dao, Class<T> entityClass) {
        this.dao = dao;
        this.entityClass = entityClass;
    }

    @Override
    public void read(Context ctx) {
        K id = ctx.pathParamAsClass("id", getIDClass()).check(this::validatePrimaryKey, "Not a valid id").get();
        T entity = dao.read(id);
        // Konverter det opdaterede objekt til et DTO (Data Transfer Object)
        Object dto = convertToDTO(entity);
        ctx.res().setStatus(200);
        ctx.json(dto, dto.getClass());
    }

    @Override
    public void readAll(Context ctx) {
        List<T> entities = dao.readAll();
        List<Object> dtos = convertToDTOList(entities);
        ctx.res().setStatus(200);
        ctx.json(dtos, dtos.getClass());
    }

    @Override
    public void create(Context ctx) {
        T jsonRequest = ctx.bodyAsClass(entityClass);
        T entity = dao.create(jsonRequest);
        Object dto = convertToDTO(entity);
        ctx.res().setStatus(201);
        ctx.json(dto, dto.getClass());
    }

    @Override
    public void update(Context ctx) {
        K id = ctx.pathParamAsClass("id", getIDClass()).check(this::validatePrimaryKey, "Not a valid id").get();
        T update = dao.update((Integer) id, validateEntity(ctx), entityClass);
        Object dto = convertToDTO(update);
        ctx.res().setStatus(200);
        ctx.json(dto, dto.getClass());
    }

    @Override
    public void delete(Context ctx) {
        K id = ctx.pathParamAsClass("id", getIDClass()).check(this::validatePrimaryKey, "Not a valid id").get();
        dao.delete(id);
        ctx.res().setStatus(204);
    }

    // Metoder, der skal implementeres i underklasser
    public abstract boolean validatePrimaryKey(K key);

    public abstract T validateEntity(Context ctx);

    protected abstract Object convertToDTO(T entity);

    protected abstract List<Object> convertToDTOList(List<T> entities);

    // Metode til at få klassen for ID
    protected abstract Class<K> getIDClass();
}