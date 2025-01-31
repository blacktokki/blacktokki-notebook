package com.blacktokki.spreadocs.core.controller;

import com.blacktokki.spreadocs.core.service.restful.RestfulService;
import com.blacktokki.spreadocs.core.service.restful.CommandService;
import com.blacktokki.spreadocs.core.service.restful.QueryService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public abstract class JpaController<T, E, Q, ID> extends RestfulController<T, Q, ID>{
    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    private JpaSpecificationExecutor<E> executor;

    @Autowired
    @Qualifier("modelMapper")
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("notNullModelMapper")
    private ModelMapper notNullModelMapper;

    private final RestfulService<T, E, ID> service = new RestfulService<T, E, ID>(){
        public JpaRepository<E, ID> getRepository(){
            return JpaController.this.repository;
        }

        public JpaSpecificationExecutor<E> getExecutor(){
            return JpaController.this.executor;
        }

        public ModelMapper getModelMapper(){
            return JpaController.this.modelMapper;
        }

        public ModelMapper getNotNullModelMapper(){
            return JpaController.this.notNullModelMapper;
        }
        
        public Class<T> getDtoClass(){
            return this.getGenericClass(0);
        }
        
        public Class<E> getEntityClass(){
            return this.getGenericClass(1);
        }
    };

    @Override
    protected CommandService<T, ID> getCommandService() {
        return service;
    }

    @Override
    protected QueryService<T, ID> getQueryService() {
        return service;
    }

    
}