package com.quick_task.dao;

import com.quick_task.entity.Project;
import com.quick_task.entity.Status;
import com.quick_task.entity.StatusProject;
import com.quick_task.entity.StatusTask;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StatusProjectDAOImpl implements Dao, StatusProjectDAO{
    @Override
    public StatusProject findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(StatusProject.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("StatusProject with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public StatusProject create(StatusProject statusProject) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(statusProject);
        return statusProject;
    }

    @Override
    public Optional<StatusProject> findStatusProjectByRightStatusTaskIsNullAndUserId(Long idWebUser) throws SQLException {
        List<StatusProject> resultList = DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery(" SELECT sp.* FROM status_project sp \n" +
                        "                        JOIN project p ON p.id_project = sp.id_project \n" +
                        "                        JOIN status s ON s.id_status = sp.id_status \n" +
                        "                        JOIN project_participants pp ON pp.id_project = p.id_project \n" +
                        "                        WHERE pp.id_web_user = :id_web_user AND sp.id_right_project_status IS NULL \n" +
                        "                        ORDER BY sp.id_status_project DESC \n" +
                        "                        LIMIT 1", StatusProject.class)
                .setParameter("id_web_user", idWebUser)
                .getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    @Override
    public StatusProject update(StatusProject statusProject) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(statusProject);
        return statusProject;    }

    @Override
    public void delete(Long idStatusProject) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        StatusProject statusProject = session.getReference(StatusProject.class, idStatusProject);
        session.remove(statusProject);
    }

    @Override
    public StatusProject findByProjectId(Long idProjectId) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT sp.* FROM status_project sp\n" +
                        "JOIN project p ON p.id_project = sp.id_project\n" +
                        "WHERE sp.id_project = :id_project", StatusProject.class)
                .setParameter("id_project", idProjectId)
                .getSingleResult();
    }



    @Override
    public List<Long> findByAllStatusUser(Long userId) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT DISTINCT s.id_status FROM status s \n" +
                        "JOIN status_project sp ON sp.id_status = s.id_status \n" +
                        " JOIN project p ON p.id_project = sp.id_project\n" +
                        "JOIN project_participants pp ON pp.id_project = p.id_project \n" +
                        "JOIN web_user u ON u.id_web_user = pp.id_web_user\n" +
                        " WHERE pp.id_web_user = :id_web_user", Long.class)
                .setParameter("id_web_user", userId)
                .list();    }


}
