package com.blacktokki.notebook.core.service.restful;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.blacktokki.notebook.core.dto.PageResponseDto;

public interface JpaQueryService<T, E, ID> extends QueryService<T, ID> {
    public JpaRepository<E, ID> getRepository();

    public JpaSpecificationExecutor<E> getExecutor();

    public T toDto(E e);

    @Override
    default Optional<T> getOptional(ID id) {
        return Optional.ofNullable(getRepository().findById(id).map(this::toDto).orElse(null));
    }

    @Override
    default PageResponseDto<T> getPage(Object param, Pageable pageable) {
        Page<E> result = getExecutor().findAll(toSpecification(param), pageable);
        Page<T> mappedResult = result.map((data) -> toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    @Override
    default List<T> getList(Object param, Sort sort) {
        Iterable<E> result = getExecutor().findAll(toSpecification(param), sort);
        return StreamSupport.stream(result.spliterator(), false).map((data) -> toDto(data))
                .collect(Collectors.toList());
    }

    default Specification<E> toSpecification(Object param){
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = toPredicates(param, (k, v)->JpaQueryService.this.toPredicate(k, v, root, criteriaBuilder)).filter(Objects::nonNull).toArray(Predicate[]::new);
                if (predicates.length > 0){
                    return criteriaBuilder.and(predicates);
                }
                return null;
            }
        };
    }

    default Predicate toPredicate(String key, Object value, Root<E> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        return builder.equal(root.get(key), value);
    }

    default Stream<Predicate> toPredicates(Object param, BiFunction<String, Object, Predicate> callback) {
        try {
            if (param.getClass().isRecord()){
                return Arrays.stream(param.getClass().getRecordComponents()).map(rc->{
                    try {
                        return callback.apply(rc.getName(), rc.getAccessor().invoke(param));
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                });

            }

            return Arrays.stream(Introspector.getBeanInfo(param.getClass()).getPropertyDescriptors()).map(pd -> {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    try {
                        return callback.apply(pd.getName(), pd.getReadMethod().invoke(param));
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }).filter(Objects::nonNull);
        } catch (IntrospectionException e) {
            return Stream.of();
        }
    }

}
