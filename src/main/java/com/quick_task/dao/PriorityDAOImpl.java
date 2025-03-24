package com.quick_task.dao;

import com.quick_task.entity.CommentTask;
import com.quick_task.entity.Priority;
import com.quick_task.entity.Project;
import com.quick_task.entity.Task;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.Optional;

public class PriorityDAOImpl implements PriorityDAO, Dao{
    public PriorityDAOImpl() {
    }
    @Override
    public Priority findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(Priority.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CommentTask with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        Priority priority = session.get(Priority.class, id);
        session.remove(priority);
    }

    @Override
    public void deleteByUser(Long userId) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM priority " +
                        " WHERE id_web_user = :id_web_user AND is_default = 0")
                .setParameter("id_web_user", userId)
                .executeUpdate();
    }
}
