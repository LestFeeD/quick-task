package com.quick_task.dao;

import com.quick_task.entity.StatusTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StatusTaskDAO extends Dao{

    StatusTask findById(Long id) throws SQLException;
    StatusTask create(StatusTask statusTask) throws SQLException;
    Optional<StatusTask> findStatusTaskByRightStatusTaskIsNullAndUserId(Long projectId) throws SQLException;
    StatusTask update(StatusTask statusTask) throws SQLException;
    StatusTask findByTaskId(Long idTask) throws SQLException;
    List<String> findByAllStatusUser(Long userId) throws SQLException;
    void delete(Long idStatusTask) throws SQLException;








}
