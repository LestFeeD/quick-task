package com.quick_task.dao;

import com.quick_task.entity.*;
import com.quick_task.utils.DBService;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public class ProjectParticipantsDAOImpl implements Dao, ProjectParticipantsDAO{
    @Override
    public List<ProjectParticipants> find(Long userId) {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT * from project_participants WHERE " +
                        " id_web_user = :id_web_user AND owner_project = 1", ProjectParticipants.class)
                .setParameter("id_web_user", userId)
                .list();
    }
    @Override
    public void create(ProjectParticipants entity) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(entity);
    }
    @Override
    public void deleteByProject(Long projectId) throws SQLException {
         DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM project_participants " +
                        " WHERE id_project  = :id_project")
                .setParameter("id_project", projectId)
                .executeUpdate();

    }
    @Override
    public void deleteByUser(Long userId) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM project_participants " +
                        " WHERE id_web_user = :id_web_user AND owner_project = 1")
                .setParameter("id_web_user", userId)
                .executeUpdate();
    }

}
