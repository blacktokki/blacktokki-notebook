package com.blacktokki.feedynote.core.migration;

import org.hibernate.boot.Metadata;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class HibernateInfoHolder {

    private static Metadata metadata;

    private static SessionFactoryServiceRegistry serviceRegistry;

    public static Metadata getMetadata() {
        return metadata;
    }

    public static void setMetadata(Metadata metadata) {
        HibernateInfoHolder.metadata = metadata;
    }

    public static SessionFactoryServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static void setServiceRegistry(SessionFactoryServiceRegistry serviceRegistry) {
        HibernateInfoHolder.serviceRegistry = serviceRegistry;
    }
}