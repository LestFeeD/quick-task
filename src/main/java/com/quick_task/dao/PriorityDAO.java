package com.quick_task.dao;

import com.quick_task.entity.Priority;
import com.quick_task.entity.Project;

import java.sql.SQLException;

public interface PriorityDAO extends Dao{

    Priority findById(Long id) throws SQLException;
    void deleteById(Long id) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;

}
