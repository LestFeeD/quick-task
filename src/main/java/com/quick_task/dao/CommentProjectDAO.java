package com.quick_task.dao;

import com.quick_task.entity.CommentProject;
import com.quick_task.entity.CommentTask;

import java.sql.SQLException;
import java.util.List;

public interface CommentProjectDAO extends Dao{
    CommentProject findById(Long id) throws SQLException;
    List<CommentProject> findByIdProject(Long id) throws SQLException;
    CommentProject create(CommentProject commentProject) throws SQLException;
    CommentProject update(CommentProject commentProject) throws SQLException;
    void deleteById (Long idCommentProject) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;
}
