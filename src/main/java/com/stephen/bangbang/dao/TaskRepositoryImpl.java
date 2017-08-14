package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.Pagination;
import com.stephen.bangbang.dto.TaskSnapshot;
import com.stephen.bangbang.dto.TasksResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
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
                session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.id < :lastId order by h.id desc").setMaxResults(number),
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
                session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.id < :lastId and h.user.id = :userId order by h.id desc").setParameter("userId", userId).setMaxResults(number),
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
        Query<TaskSnapshot> queryWithoutLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user in (select u.friends from User u where u.id = :userId) order by h.id desc")
                .setParameter("userId", userId)
                .setMaxResults(number);
        Query<TaskSnapshot> queryWithLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user in (select u.friends from User u where u.id = :userId) and h.id < :lastId order by h.id desc")
                .setParameter("userId", userId)
                .setParameter("lastId", lastTaskId)
                .setMaxResults(number);
        return baseQueryStructure(queryWithoutLastTaskId, queryWithLastTaskId, !lastTaskId.equals(0L), new PageWorker() {
            @Override
            public int countPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user in (select u.friends from User u where u.id = :userId)").setParameter("userId", userId);
                long total = query.getSingleResult();
                return TaskRepositoryImpl.this.countPage((int) total, number);
            }

            @Override
            public int getCurrentPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user in (select u.friends from User u where u.id = :userId) and h.id >= :lastTaskId").setParameter("userId", userId).setParameter("lastTaskId", lastTaskId);
                long count = query.getSingleResult() + 1;
                return TaskRepositoryImpl.this.countPage((int) count, number);
            }
        });
    }

    @Override
    public TasksResponse findTasksPublishedByStrangers(Long userId, Long lastTaskId, int number) {
        Session session = getCurrentSession();
        Query<TaskSnapshot> queryWithoutLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user not in (select u.friends from User u where u.id = :userId) order by h.id desc")
                .setParameter("userId", userId)
                .setMaxResults(number);
        Query<TaskSnapshot> queryWithLastTaskId = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user not in (select u.friends from User u where u.id = :userId) and h.id < :lastId order by h.id desc")
                .setParameter("userId", userId)
                .setParameter("lastId", lastTaskId)
                .setMaxResults(number);
        return baseQueryStructure(queryWithoutLastTaskId, queryWithLastTaskId, !lastTaskId.equals(0L), new PageWorker() {
            @Override
            public int countPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user not in (select u.friends from User u where u.id = :userId)").setParameter("userId", userId);
                long total = query.getSingleResult();
                return TaskRepositoryImpl.this.countPage((int) total, number);
            }

            @Override
            public int getCurrentPage() {
                Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user not in (select u.friends from User u where u.id = :userId) and h.id >= :lastTaskId").setParameter("userId", userId).setParameter("lastTaskId", lastTaskId);
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
        try {
            HelpingTask reference = session.getReference(HelpingTask.class, taskId);
            return reference != null;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    private int countPage(int total, int numberPerPage) {
        return total % numberPerPage == 0 ?
                total / numberPerPage :
                total / numberPerPage + 1;
    }
}
