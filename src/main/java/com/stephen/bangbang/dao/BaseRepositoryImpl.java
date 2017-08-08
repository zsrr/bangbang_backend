package com.stephen.bangbang.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BaseRepositoryImpl {
    protected SessionFactory sessionFactory;

    public BaseRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
