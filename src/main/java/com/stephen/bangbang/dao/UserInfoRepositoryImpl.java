package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;

// 先设置成最严格的，最后再进行优化
@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public class UserInfoRepositoryImpl implements UserInfoRepository {

    private SessionFactory sessionFactory;

    @Inject
    public UserInfoRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public User findUser(String name) {
        Session session = getCurrentSession();
        TypedQuery<User> userQuery = session.createQuery("select i from User i where i.username = :name").setParameter("name", name);
        // 无法理解的api设计
        userQuery.setMaxResults(1);
        List<User> userList = userQuery.getResultList();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public void register(String name, String password) {
        Session session = getCurrentSession();
        User user = new User(name, password);
        session.persist(user);
    }
}
