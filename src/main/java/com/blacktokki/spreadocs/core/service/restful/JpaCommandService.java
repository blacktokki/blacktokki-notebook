package com.blacktokki.spreadocs.core.service.restful;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommandService<T, E, ID> extends CommandService<T, ID> {
    public JpaRepository<E, ID> getRepository();

    public ModelMapper getNotNullModelMapper();

    public T toDto(E e);

    public E toEntity(T t);

    public void setEntityId(E entity, ID id);

    @Override
    @Transactional
    default T create(T newDomain){
        return toDto(getRepository().save(toEntity(newDomain)));
    }

    @Override
    @Transactional
    default T update(ID id, T updated){
        E entity = toEntity(updated);
        setEntityId(entity, id);
        E saved = getRepository().save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    default T bulkUpdateFields(List<ID> ids, T updated) {
        List<E> entityList = getRepository().findAllById(ids);
        for (E entity: entityList){
            getNotNullModelMapper().map(updated, entity);
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
