package com.blacktokki.spreadocs.core.service.restful;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.ReflectionUtils;




public abstract class GenericService<T, E, ID> {
    private JpaRepository<E, ID> repository;

    private BiConsumer<E, ID> entityIdSetter;

    private List<BiConsumer<E, E>> notNullFieldsSetter = new ArrayList<>();

    @Autowired
    private JpaSpecificationExecutor<E> executor;


    public final void setEntityId(E entity, ID id) {
        this.entityIdSetter.accept(entity, id);
    }

    public final void setEntityNotNullFields(E oldEntity, E newEntity){
        notNullFieldsSetter.forEach(f->f.accept(oldEntity, newEntity));
    }

    @Autowired
    public void setRepository(JpaRepository<E, ID> repository){
        this.repository = repository;
        Class<?> entityClass = ResolvableType.forClass(GenericService.class, getClass()).getGeneric(1).resolve();
        ReflectionUtils.doWithFields(entityClass, field -> {
                if (field.getAnnotation(Id.class) != null) {
                    this.entityIdSetter = (entity, id) -> {
                        try {
                            field.set(entity, id);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                        }
                    };
                }
                else if(field.getAnnotation(Column.class) != null) {
                    this.notNullFieldsSetter.add((entity, newEntity)->{
                        try {
                            Object value = field.get(newEntity);
                            if(value != null){
                                field.set(entity, value);
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                        }
                    });
                }
            });
    }

    public JpaRepository<E, ID> getRepository() {
        return repository;
    }

    public JpaSpecificationExecutor<E> getExecutor() {
        return executor;
    }

    abstract public T toDto(E t);

    abstract public E toEntity(T b);
}
