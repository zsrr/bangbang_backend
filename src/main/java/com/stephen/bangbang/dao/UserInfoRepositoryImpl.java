package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.FriendsResponse;
import com.stephen.bangbang.dto.SingleFriendInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserInfoRepositoryImpl extends BaseRepositoryImpl implements UserInfoRepository {

    @Inject
    public UserInfoRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public User findUser(String name) {
        Session session = getCurrentSession();
        TypedQuery<User> userQuery = session.createQuery("select i from User i where i.username = :name").setParameter("name", name);
        userQuery.setHint("org.hibernate.cacheable", true);
        try {
            return userQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findUser(Long id) {
        Session session = getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public User register(String name, String password, String nickname) {
        Session session = getCurrentSession();
        User user = new User(name, password);
        user.setNickname(nickname);
        session.persist(user);
        return user;
    }

    @Override
    public void update(User updatedUser) {
        Session session = getCurrentSession();
        session.update(updatedUser);
    }

    @Override
    public boolean hasUser(Long id) {
        Session session = getCurrentSession();
        try {
            User user = session.getReference(User.class, id);
            return user != null;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean hasUser(String username) {
        Session session = getCurrentSession();
        TypedQuery<Long> userQuery = session.createQuery("select u.id from User u where u.username = :name").setParameter("name", username);
        try {
            Long id = userQuery.getSingleResult();
            return id != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public FriendsResponse getFriends(Long userId) {
        Session session = getCurrentSession();
        List<SingleFriendInfo> infos = new ArrayList<>();
        User user = session.get(User.class, userId);
        Iterator<User> iterator = user.getFriends().iterator();
        while (iterator.hasNext()) {
            User friend = iterator.next();
            SingleFriendInfo info = new SingleFriendInfo(friend);
            infos.add(info);
        }
        return new FriendsResponse(infos);
    }

    @Override
    public void makeFriend(Long userId, Long targetUserId) {
        Session session = getCurrentSession();
        User user = session.get(User.class, userId);
        User targetUser = session.get(User.class, targetUserId);
        user.makeFriend(targetUser);
        session.flush();
    }
}
