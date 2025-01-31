package com.blacktokki.spreadocs.core.service.restful;




public abstract class RestfulService<T, E, ID> extends GenericService<T, E, ID> implements JpaQueryService<T, E, ID>,  JpaCommandService<T, E, ID>{
}