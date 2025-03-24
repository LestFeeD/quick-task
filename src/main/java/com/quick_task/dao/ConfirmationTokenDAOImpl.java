package com.quick_task.dao;

import com.quick_task.entity.ConfirmationToken;
import com.quick_task.entity.Project;
import com.quick_task.entity.Task;
import com.quick_task.utils.DBService;
import org.hibernate.Session;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfirmationTokenDAOImpl implements ConfirmationTokenDAO, Dao{
    @Override
    public ConfirmationToken findByToken(String token) throws SQLException {
         return DBService.getSessionFactory().getCurrentSession()
                .createNativeQuery("SELECT * FROM confirmation_token WHERE user_token = :user_token", ConfirmationToken.class)
                .setParameter("user_token", token)
                .getSingleResult();
    }
    @Override
    public ConfirmationToken findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return session.get(ConfirmationToken.class, id);
    }

    @Override
    public void createConfirmationToken(ConfirmationToken confirmationToken) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(confirmationToken);
    }
    @Override
    public Set<Long> findAllExpiredToken(Timestamp timestamp) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT id_confirmation_token FROM confirmation_token " +
                        "WHERE expires_at < :now AND confirmed_at is null", Long.class)
                .setParameter("now", timestamp)
                .stream()
                .collect(Collectors.toSet());
    }
    @Override
    public void deleteById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        ConfirmationToken confirmationToken = session.getReference(ConfirmationToken.class, id);
        session.remove(confirmationToken);
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
