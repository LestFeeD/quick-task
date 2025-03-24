package com.quick_task.dao;

import com.quick_task.entity.Priority;
import com.quick_task.entity.Status;
import com.quick_task.entity.Tag;
import com.quick_task.utils.DBService;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public interface TagDAO extends Dao{
    Tag findById(Long id) throws SQLException;
    Tag getByName(String parameter) throws SQLException;
    Tag update(Tag tag) throws SQLException;
    List<Tag> findByIdTask(Long id) throws SQLException;
    void deleteFromTask(Long idTask, Long idTag) throws SQLException;

}
