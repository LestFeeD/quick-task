package com.quick_task.dao;

import com.quick_task.entity.*;
import com.quick_task.utils.DBService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TagDAOImpl implements Dao, TagDAO{
    @Override
    public Tag findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(Tag.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Tag with ID \"%s\" not found.", id)
                ));
    }

    @Override
    public Tag getByName(String parameter) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT * FROM tag WHERE name_tag LIKE :name", Tag.class)
                .setParameter("name", parameter + "%")
                .getSingleResult();
                  }

    @Override
    public Tag update(Tag tag) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(tag);
        return tag;
    }

    @Override
    public List<Tag> findByIdTask(Long id) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT t.* FROM tag t\n" +
                        "JOIN tag_task tt ON tt.id_tag = t.id_tag\n" +
                        "WHERE tt.id_task = :id_task", Tag.class)
                .setParameter("id_task", id)
                .setCacheable(true)

                .list();
    }

    @Override
    public void deleteFromTask(Long idTask, Long idTag) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        Task task = session.get(Task.class, idTask);
        Tag tag = session.get(Tag.class, idTag);

        task.getTagSet().remove(tag);
        tag.getTaskSet().remove(task);

        session.merge(task);
        session.merge(tag);
        DBService.getSessionFactory().getCache().evict(Task.class, task.getIdTask());
        DBService.getSessionFactory().getCache().evict(Tag.class, tag.getIdTag());

    }
}
