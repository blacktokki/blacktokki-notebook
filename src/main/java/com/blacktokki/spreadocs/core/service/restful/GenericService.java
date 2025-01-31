package com.blacktokki.spreadocs.core.service.restful;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

import jakarta.persistence.Id;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.ReflectionUtils;




public abstract class GenericService<T, E, ID> {
    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    @Qualifier("notNullModelMapper")
    private ModelMapper notNullModelMapper;

    @Autowired
    private JpaSpecificationExecutor<E> executor;

    private ModelMapper modelMapper;

    private Class<T> dtoClass;

    private Class<E> entityClass;

    private BiConsumer<E, ID> entityIdSetter;

    public final void setEntityId(E entity, ID id) {
        this.entityIdSetter.accept(entity, id);
    }

    @Autowired
    @Qualifier("modelMapper")
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.dtoClass = getGenericClass(0);
        this.entityClass = getGenericClass(1);
        ReflectionUtils.doWithFields(this.entityClass, field -> {
            if (field.getAnnotation(Id.class) != null) {
                this.entityIdSetter = (entity, id) -> {
                    try {
                        field.set(entity, id);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                    }
                };
            }
        });
    }

    public JpaRepository<E, ID> getRepository() {
        return repository;
    }

    public JpaSpecificationExecutor<E> getExecutor() {
        return executor;
    }

    public T toDto(E t) {
        return getModelMapper().map(t, (Type) dtoClass);
    }

    public E toEntity(T b) {
        return getModelMapper().map(b, (Type) getEntityClass());
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public Class<T> getDtoClass() {
        return dtoClass;
    }

    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    public ModelMapper getNotNullModelMapper(){
        return notNullModelMapper;
    }

    protected <S> Class<S> getGenericClass(int index) {
        return getModelMapper().map(
                ResolvableType.forClass(GenericService.class, getClass()).getGeneric(index).resolve(),
                (Type) Class.class);
    }
}
