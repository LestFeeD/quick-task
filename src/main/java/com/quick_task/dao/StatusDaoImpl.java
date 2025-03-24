package com.quick_task.dao;

import com.quick_task.entity.Project;
import com.quick_task.entity.Status;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StatusDaoImpl implements StatusDAO, Dao{
    @Override
    public Status findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(Status.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Status with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public Long findByDefaultName() throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT id_status FROM status WHERE name_status = 'beginning'", Long.class)
                .getSingleResult();

    }


    public Long findIdByProjectId(Long idProject) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT s.id_status FROM status s " +
                        "JOIN status_project sp ON sp.id_status = s.id_status " +
                        "WHERE sp.id_project = :id_project", Long.class)
                .setParameter("id_project", idProject)
                .getSingleResult();
    }

    @Override
    public void createStatus(Status status) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(status);
    }

    @Override
    public List<Status> findAllStatusUser(Long userId) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT DISTINCT s.* " +
                        "FROM status s " +
                        "LEFT JOIN status_task st ON st.id_status = s.id_status " +
                        "LEFT JOIN task t ON st.id_task = t.id_task " +
                        "LEFT JOIN task_participants tp ON tp.id_task = t.id_task " +
                        "LEFT JOIN web_user wu ON wu.id_web_user = tp.id_web_user " +
                        "WHERE " +
                        "    s.id_web_user IS NULL " +
                        "    OR s.id_web_user = :id_web_user  ", Status.class)
                .setParameter("id_web_user", userId)
                .setCacheable(true)
                .list();
    }

    @Override
    public Status update(Status status) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(status);
        return status;
    }

    @Override
    public void deleteByUser(Long userId) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM confirmation_token " +
                        " WHERE id_web_user = :id_web_user ")
                .setParameter("id_web_user", userId)
                .executeUpdate();
    }

}
