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
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public class TaskRepositoryImpl extends BaseRepositoryImpl implements TaskRepository {

    @Inject
    public TaskRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public TasksResponse findAllTasks(Long lastTaskId, int number) {
        Session session = getCurrentSession();
        int totalPage = getPageCount(number);
        int currentPage;
        if (lastTaskId == 0) {
            currentPage = 1;
            if (currentPage > totalPage)
                currentPage = totalPage;
            Query<TaskSnapshot> snapshotQuery = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h order by h.id desc").setMaxResults(number);
            List<TaskSnapshot> snapshots = snapshotQuery.getResultList();
            return new TasksResponse(new Pagination(currentPage, totalPage), snapshots);
        } else {
            currentPage = getCurrentPage(lastTaskId, number);
            if (currentPage > totalPage) {
                currentPage = totalPage;
                return new TasksResponse(new Pagination(currentPage, totalPage), null);
            }
            Query<TaskSnapshot> snapshotQuery = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.id < :lastId order by h.id desc").setMaxResults(number);
            List<TaskSnapshot> snapshots = snapshotQuery.getResultList();
            return new TasksResponse(new Pagination(currentPage, totalPage), snapshots);
        }
    }

    @Override
    public TasksResponse findAllTasksByUserId(Long userId, Long lastTaskId, int number) {
        Session session = getCurrentSession();
        int totalPage = getPageCount(userId, number);
        int currentPage;
        if (lastTaskId == 0) {
            currentPage = 1;
            Query<TaskSnapshot> snapshotQuery = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.user.id = :userId order by h.id desc").setParameter("userId", userId).setMaxResults(number);
            List<TaskSnapshot> snapshots = snapshotQuery.getResultList();
            return new TasksResponse(new Pagination(currentPage, totalPage), snapshots);
        } else {
            currentPage = getCurrentPage(userId, lastTaskId, number);
            if (currentPage > totalPage) {
                currentPage = totalPage;
                return new TasksResponse(new Pagination(currentPage, totalPage), null);
            }
            Query<TaskSnapshot> snapshotQuery = session.createQuery("select new com.stephen.bangbang.dto.TaskSnapshot(h) from HelpingTask h where h.id < :lastId and h.user.id = :userId order by h.id desc").setParameter("userId", userId).setMaxResults(number);
            List<TaskSnapshot> snapshots = snapshotQuery.getResultList();
            return new TasksResponse(new Pagination(currentPage, totalPage), snapshots);
        }
    }

    @Override
    public void publish(Long userId, HelpingTask helpingTask) {
        Session session = getCurrentSession();
        User user = session.getReference(User.class, userId);
        user.getTasks().add(helpingTask);
        session.flush();
    }

    @Override
    public HelpingTask findTask(Long taskId) {
        Session session = getCurrentSession();
        TypedQuery<HelpingTask> taskQuery = session.createQuery("select i from HelpingTask i where i.id = :id").setParameter("id", taskId);
        taskQuery.setMaxResults(1);
        List<HelpingTask> taskList = taskQuery.getResultList();
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }
        return taskList.get(0);
    }

    private int getCurrentPage(Long lastTaskId, int numberPerPage) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.id >= :lastId").setParameter("lastId", lastTaskId);
        long count = query.getSingleResult() + 1;
        return countPage((int) count, numberPerPage);
    }

    private int getCurrentPage(Long userId, Long lastTaskId, int numberPerPage) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.id >= :lastId and h.user.id = :userId").setParameter("lastId", lastTaskId).setParameter("userId", userId);
        long count = query.getSingleResult() + 1;
        return countPage((int) count, numberPerPage);
    }

    private int getPageCount(int numberPerPage) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("select count(h) from HelpingTask h");
        long total = query.getSingleResult();
        return countPage((int) total, numberPerPage);
    }

    private int getPageCount(Long userId, int numberPerPage) {
        Session session = getCurrentSession();
        Query<Long> query = session.createQuery("select count(h) from HelpingTask h where h.user.id = :userId").setParameter("userId", userId);
        long total = query.getSingleResult();
        return countPage((int) total, numberPerPage);
    }

    private int countPage(int total, int numberPerPage) {
        if (total % numberPerPage == 0) {
            return total / numberPerPage;
        }

        return total / numberPerPage + 1;
    }

}
