package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.Pagination;
import com.stephen.bangbang.dto.TaskSnapshot;
import com.stephen.bangbang.dto.TasksResponse;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class TaskRepositoryImpl extends BaseRepositoryImpl implements TaskRepository {

    private interface PageWorker {
        int countPage();
        int getCurrentPage();
    }

    @Inject
    public TaskRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public TasksResponse findAllTasks(Long lastTaskId, int number) {
        Session session = getCurrentSession();
        return baseQueryStructure(session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h order by h.id desc").setMaxResults(number),
                session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.id < :lastId order by h.id desc").setMaxResults(number).setParameter("lastId", lastTaskId),
                !lastTaskId.equals(0L), new PageWorker() {
                    @Override
                    public int countPage() {
                        Query<Long> query = session.createQuery("select count(h) from HelpingTask h");
                        long total = query.getSingleResult();
                        return TaskRepositoryImpl.this.countPage((int) total, number);
                    }

                    @Override
                    public int getCurrentPage() {
                        Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.id >= :lastId").setParameter("lastId", lastTaskId);
                        long count = query.getSingleResult() + 1;
                        return TaskRepositoryImpl.this.countPage((int) count, number);
                    }
                });
    }

    @Override
    public TasksResponse findAllTasksByUserId(Long userId, Long lastTaskId, int number) {
        Session session = getCurrentSession();
        return baseQueryStructure(session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user.id = :userId order by h.id desc").setParameter("userId", userId).setMaxResults(number),
                session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.id < :lastId and h.user.id = :userId order by h.id desc").setParameter("userId", userId).setParameter("lastId", lastTaskId).setMaxResults(number),
                !lastTaskId.equals(0L), new PageWorker() {
                    @Override
                    public int countPage() {
                        Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user.id = :userId").setParameter("userId", userId);
                        long total = query.getSingleResult();
                        return TaskRepositoryImpl.this.countPage((int) total, number);
                    }

                    @Override
                    public int getCurrentPage() {
                        Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.id >= :lastId and h.user.id = :userId").setParameter("lastId", lastTaskId).setParameter("userId", userId);
                        long count = query.getSingleResult() + 1;
                        return TaskRepositoryImpl.this.countPage((int) count, number);
                    }
                });
    }

    @Override
    public TasksResponse findTasksPublishedByFriends(Long userId, Long lastTaskId, int number) {
        Session session = getCurrentSession();
        Query<TaskSnapshot> queryWithoutLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user.id in (select f.id from User u inner join u.friends f where u.id = :userId) order by h.id desc")
                .setParameter("userId", userId)
                .setMaxResults(number);
        Query<TaskSnapshot> queryWithLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user.id in (select f.id from User u inner join u.friends f where u.id = :userId) and h.id < :lastId order by h.id desc")
                .setParameter("userId", userId)
                .setParameter("lastId", lastTaskId)
                .setMaxResults(number);
        return baseQueryStructure(queryWithoutLastTaskId, queryWithLastTaskId, !lastTaskId.equals(0L), new PageWorker() {
            @Override
            public int countPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user.id in (select f.id from User u inner join u.friends f where u.id = :userId)").setParameter("userId", userId);
                long total = query.getSingleResult();
                return TaskRepositoryImpl.this.countPage((int) total, number);
            }

            @Override
            public int getCurrentPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user.id in (select f.id from User u inner join u.friends f where u.id = :userId) and h.id >= :lastTaskId").setParameter("userId", userId).setParameter("lastTaskId", lastTaskId);
                long count = query.getSingleResult() + 1;
                return TaskRepositoryImpl.this.countPage((int) count, number);
            }
        });
    }

    @Override
    public TasksResponse findTasksPublishedByStrangers(Long userId, Long lastTaskId, int number) {
        Session session = getCurrentSession();
        Query<TaskSnapshot> queryWithoutLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user.id not in (select f.id from User u inner join u.friends f where u.id = :userId) and h.user.id != :userId order by h.id desc")
                .setParameter("userId", userId)
                .setMaxResults(number);
        Query<TaskSnapshot> queryWithLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user.id not in (select f.id from User u inner join u.friends f where u.id = :userId) and h.id < :lastId and h.user.id != :userId order by h.id desc")
                .setParameter("userId", userId)
                .setParameter("lastId", lastTaskId)
                .setMaxResults(number);
        return baseQueryStructure(queryWithoutLastTaskId, queryWithLastTaskId, !lastTaskId.equals(0L), new PageWorker() {
            @Override
            public int countPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user.id not in (select f.id from User u inner join u.friends f where u.id = :userId) and h.user.id != :userId").setParameter("userId", userId);
                long total = query.getSingleResult();
                return TaskRepositoryImpl.this.countPage((int) total, number);
            }

            @Override
            public int getCurrentPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user.id not in (select u.friends from User u where u.id = :userId) and h.id >= :lastTaskId and h.user.id != :userId").setParameter("userId", userId).setParameter("lastTaskId", lastTaskId);
                long count = query.getSingleResult() + 1;
                return TaskRepositoryImpl.this.countPage((int) count, number);
            }
        });
    }

    private TasksResponse baseQueryStructure(Query<TaskSnapshot> queryWithNoLastId, Query<TaskSnapshot> queryWithLastId, boolean withLastTaskId, PageWorker pageWorker) {
        int totalPage = pageWorker.countPage();
        int currentPage;
        if (!withLastTaskId) {
            currentPage = 1;
            if (currentPage > totalPage) {
                currentPage = totalPage;
            }
            List<TaskSnapshot> snapshots = queryWithNoLastId.getResultList();
            return new TasksResponse(new Pagination(currentPage, totalPage), snapshots);
        } else {
            currentPage = pageWorker.getCurrentPage();
            if (currentPage > totalPage) {
                currentPage = totalPage;
                return new TasksResponse(new Pagination(currentPage, totalPage), null);
            }
            List<TaskSnapshot> snapshots = queryWithLastId.getResultList();
            return new TasksResponse(new Pagination(currentPage, totalPage), snapshots);
        }
    }

    @Override
    public void publish(Long userId, HelpingTask helpingTask) {
        Session session = getCurrentSession();
        User userRef = session.getReference(User.class, userId);
        helpingTask.setUser(userRef);
        session.persist(helpingTask);
        session.flush();
    }

    @Override
    public HelpingTask findTask(Long taskId) {
        return getCurrentSession().get(HelpingTask.class, taskId);
    }

    @Override
    public boolean hasTask(Long taskId) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("select h.id from HelpingTask h where h.id = :id").setParameter("id", taskId);
        try {
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public Long findResponsiblePersonFor(Long taskId) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("select h.responsiblePerson.id from HelpingTask h where h.id = :id").setParameter("id", taskId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void userInChargeOf(Long userId, Long taskId) {
        Session session = getCurrentSession();
        User user = session.getReference(User.class, userId);
        HelpingTask ht = session.getReference(HelpingTask.class, taskId);
        ht.setResponsiblePerson(user);
        session.flush();
    }

    @Override
    public void deleteTask(Long taskId) {
        Session session = getCurrentSession();
        HelpingTask helpingTask = session.get(HelpingTask.class, taskId);
        session.delete(helpingTask);
        session.flush();
    }

    private int countPage(int total, int numberPerPage) {
        return total % numberPerPage == 0 ?
                total / numberPerPage :
                total / numberPerPage + 1;
    }
}
