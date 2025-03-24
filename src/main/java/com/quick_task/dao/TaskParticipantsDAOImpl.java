package com.quick_task.dao;

import com.quick_task.entity.TaskParticipants;
import com.quick_task.utils.DBService;
import org.hibernate.Session;

import java.sql.SQLException;

public class TaskParticipantsDAOImpl implements Dao, TaskParticipantsDAO{
    @Override
    public void create(TaskParticipants entity) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(entity);
    }

    @Override
    public void deleteByTask(Long idTask) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM task_participants " +
                        " WHERE id_task  = :id_task")
                .setParameter("id_task", idTask)
                .executeUpdate();
    }

    @Override
    public void deleteByUser(Long userId) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM task_participants " +
                        " WHERE id_web_user = :id_web_user AND owner_task = 1")
                .setParameter("id_web_user", userId)
                .executeUpdate();
    }
}
