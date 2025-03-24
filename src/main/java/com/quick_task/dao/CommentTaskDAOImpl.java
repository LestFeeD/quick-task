package com.quick_task.dao;

import com.quick_task.entity.CommentProject;
import com.quick_task.entity.CommentTask;
import com.quick_task.entity.Task;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CommentTaskDAOImpl implements CommentTaskDAO, Dao{
    @Override
    public CommentTask findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(CommentTask.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CommentTask with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public List<CommentTask> findByIdTask(Long id) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("select ct.* from comment_task ct\n" +
                        "WHERE ct.id_task = :id_task", CommentTask.class)
                .setParameter("id_task", id)
                .setCacheable(true)
                .list();
    }

    @Override
    public CommentTask create(CommentTask commentTask) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(commentTask);
        return commentTask;
    }

    @Override
    public CommentTask update(CommentTask commentTask) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(commentTask);
        DBService.getSessionFactory().getCache().evict(CommentTask.class, commentTask.getIdCommentTask());

        return commentTask;
    }

    @Override
    public void deleteById(Long idCommentTask) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        CommentTask commentTask = session.getReference(CommentTask.class, idCommentTask);
        session.remove(commentTask);
    }

    @Override
    public void deleteByUser(Long userId) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM comment_task " +
                        " WHERE id_web_user = :id_web_user ")
                .setParameter("id_web_user", userId)
                .executeUpdate();
    }
}
