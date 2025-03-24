package com.quick_task.dao;

import com.quick_task.entity.CommentTask;
import com.quick_task.entity.Task;

import java.sql.SQLException;
import java.util.List;

public interface CommentTaskDAO extends Dao{
    CommentTask findById(Long id) throws SQLException;
    List<CommentTask> findByIdTask(Long id) throws SQLException;
    CommentTask create(CommentTask commentTask) throws SQLException;
    CommentTask update(CommentTask commentTask) throws SQLException;
    void deleteById (Long idCommentTask) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;


}
