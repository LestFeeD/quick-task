package com.quick_task.dao;

import com.quick_task.entity.Status;
import com.quick_task.entity.StatusProject;
import com.quick_task.entity.StatusRole;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.Optional;

public class StatusRoleDAOImpl implements StatusRoleDAO, Dao{
    @Override
    public StatusRole findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(StatusRole.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("StatusRole with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public StatusRole findByIdStatus(Long id) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("select * from status " +
                        "SELECT sr.* FROM status_role sr " +
                        "JOIN status s ON s.id_status_role = sr.id_status_role " +
                        "WHERE s.id_status = :id_status", StatusRole.class)
                .setParameter("id_status", id)
                .getSingleResult();
    }

    @Override
    public StatusRole update(StatusRole statusRole) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(statusRole);
        return statusRole;
    }
}
