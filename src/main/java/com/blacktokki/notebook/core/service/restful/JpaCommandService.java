package com.blacktokki.notebook.core.service.restful;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommandService<T, E, ID> extends CommandService<T, ID> {
    public JpaRepository<E, ID> getRepository();

    public T toDto(E e);

    public E toEntity(T t);

    public void setEntityId(E entity, ID id);

    public void setEntityNotNullFields(E oldEntity, E newEntity);
    
    default Boolean updatable(List<E> oldEntity) {
        return true;
    }

    @Override
    @Transactional
    default T create(T newDomain){
        return toDto(getRepository().save(toEntity(newDomain)));
    }

    @Override
    @Transactional
    default T update(ID id, T updated){
        E entity = toEntity(updated);
        if (!updatable(List.of(entity))){
            return toDto(entity);
        }
        setEntityId(entity, id);
        E saved = getRepository().save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    default T bulkUpdateFields(List<ID> ids, T updated) {
        E newEntity = toEntity(updated);
        List<E> entityList = getRepository().findAllById(ids);
        if (!updatable(entityList)) {
            return updated;
        }
        for (E entity: entityList){
            setEntityNotNullFields(entity, newEntity);
        }
        getRepository().saveAll(entityList);
        return updated;
    }

    @Override
    @Transactional
    default void bulkDelete(List<ID> ids) {
        getRepository().deleteAllByIdInBatch(ids);
    }
}
