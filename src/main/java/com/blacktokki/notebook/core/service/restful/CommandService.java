package com.blacktokki.notebook.core.service.restful;

import java.util.List;

public interface CommandService<T, ID>{
    public T create(T newDomain);

    public T update(ID id, T updated);

    public T bulkUpdateFields(List<ID> ids, T updated);

    public void bulkDelete(List<ID> ids);
}