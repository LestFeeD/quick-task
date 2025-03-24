package com.quick_task.dao;

import com.quick_task.entity.ConfirmationToken;
import com.quick_task.entity.Project;
import com.quick_task.entity.Tag;
import com.quick_task.entity.WebUser;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.Optional;


public class WebUserDAOImpl implements WebUserDAO, Dao{

    @Override
    public WebUser create(WebUser webUser) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        session.persist(webUser);
        return webUser;
    }

    @Override
    public WebUser update(WebUser webUser) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .merge(webUser);
        return webUser;

    }

    @Override
    public Integer findByEmail(String email) throws SQLException {
       return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT COUNT(*) FROM web_user WHERE mail_user = :email", Integer.class)
                .setParameter("email", email)
               .getSingleResult();
    }

    @Override
    public WebUser findUserByEmail(String email) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        try {
            WebUser user = (WebUser) session.createNativeQuery("SELECT * FROM web_user WHERE mail_user = :email", WebUser.class)
                    .setParameter("email", email)
                    .getSingleResult();

            return user;
        } catch (NoResultException e) {
            throw new com.quick_task.exception.EntityNotFoundException("Not found user with email: " + email);
        }
    }


    @Override
    public WebUser findById(Long idWebUser) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(WebUser.class,idWebUser))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("WebUser with ID \"%s\" not found.", idWebUser)
                ));

    }

    @Override
    public WebUser findOwnerTaskByIdTask(Long idTask) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("select wu.* FROM web_user wu " +
                        "JOIN task_participants tp ON tp.id_web_user = wu.id_web_user " +
                        "JOIN task t ON t.id_task = tp.id_task " +
                        "WHERE tp.id_task = :id_task AND tp.owner_task = 1", WebUser.class)
                .setParameter("id_task", idTask)
                .getSingleResult();
    }

    @Override
    public Long findByIdToken(Long idToken) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT wu.id_web_user FROM web_user wu " +
                        "JOIN confirmation_token ct ON ct.id_web_user = wu.id_web_user " +
                        "WHERE ct.id_confirmation_token = :id_confirmation_token ", Long.class)
                .setParameter("id_confirmation_token", idToken)
                .getSingleResult();
    }


    @Override
    public void deleteById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        WebUser webUser = session.getReference(WebUser.class, id);
        session.remove(webUser);
    }
}
