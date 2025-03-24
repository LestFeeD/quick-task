package com.quick_task.dao;

import com.quick_task.entity.*;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StatusTaskDAOImpl implements StatusTaskDAO, Dao{

    @Override
    public StatusTask findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(StatusTask.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("StatusTask with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public StatusTask create(StatusTask statusTask) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(statusTask);
        return statusTask;
    }

    @Override
    public Optional<StatusTask> findStatusTaskByRightStatusTaskIsNullAndUserId(Long projectId) throws SQLException {
        List<StatusTask> resultList = DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery(" SELECT st.* FROM status_task st \n" +
                        "JOIN task t ON t.id_task = st.id_task \n" +
                        "JOIN status s ON s.id_status = st.id_status " +
                        "JOIN task_participants tp ON tp.id_task = t.id_task " +
                        "LEFT JOIN  project p ON p.id_project = t.id_project\n" +
                        "JOIN status_task stt ON stt.id_status_task = st.id_status_task\n" +
                        "WHERE tp.id_web_user = :id_web_user AND stt.id_right_task_status IS NULL " +
                        "ORDER BY st.id_status_task DESC\n" +
                        "LIMIT 1", StatusTask.class)
                .setParameter("id_web_user", projectId)
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));

    }

    @Override
    public StatusTask update(StatusTask statusTask) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(statusTask);
        return statusTask;
    }

    @Override
    public StatusTask findByTaskId(Long idTask) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT st.* FROM status_task st " +
                        "JOIN task t ON t.id_task = st.id_task " +
                        "WHERE t.id_task = :id_task", StatusTask.class)
                .setParameter("id_task", idTask)
                .getSingleResult();
    }



    @Override
    public List<String> findByAllStatusUser(Long userId) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT DISTINCT s.name_status FROM status s " +
                        "                        JOIN status_task st ON st.id_status = s.id_status " +
                        "                        JOIN task t ON t.id_task = st.id_task " +
                        "JOIN task_participants tp ON tp.id_task = t.id_task " +
                        "                        JOIN web_user u ON u.id_web_user = tp.id_web_user" +
                        " WHERE tp.id_web_user = :user_id", String.class)
                .setParameter("user_id", userId)
                .list();
    }



    @Override
    public void delete(Long idStatusTask) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        StatusTask statusTask = session.getReference(StatusTask.class, idStatusTask);
        session.remove(statusTask);
    }


}

