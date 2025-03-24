package com.quick_task.dao;

import com.quick_task.entity.CommentProject;
import com.quick_task.entity.CommentTask;
import com.quick_task.entity.Project;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CommentProjectDAOImpl implements Dao, CommentProjectDAO{

    @Override
    public CommentProject findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(CommentProject.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CommentProject with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public List<CommentProject> findByIdProject(Long id) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("select cp.* from comment_project cp\n" +
                        "WHERE cp.id_project = :id_project", CommentProject.class)
                .setParameter("id_project", id)
                .list();    }

    @Override
    public CommentProject create(CommentProject commentProject) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(commentProject);
        return commentProject;
    }

    @Override
    public CommentProject update(CommentProject commentProject) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(commentProject);
        return commentProject;    }

    @Override
    public void deleteById(Long idCommentProject) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        CommentProject commentProject = session.getReference(CommentProject.class, idCommentProject);
        session.remove(commentProject);
    }

    @Override
    public void deleteByUser(Long userId) throws SQLException {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE  FROM comment_project " +
                        " WHERE id_web_user = :id_web_user ")
                .setParameter("id_web_user", userId)
                .executeUpdate();
    }
}
