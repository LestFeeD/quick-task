package com.quick_task.dao;

import com.quick_task.entity.StatusProject;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StatusProjectDAO extends Dao{

    StatusProject findById(Long id) throws SQLException;
    StatusProject create(StatusProject statusProject) throws SQLException;
    Optional<StatusProject> findStatusProjectByRightStatusTaskIsNullAndUserId(Long idWebUser) throws SQLException;
    StatusProject update(StatusProject statusProject) throws SQLException;
    void delete(Long idStatusProject) throws SQLException;
    StatusProject findByProjectId(Long id) throws SQLException;
    List<Long> findByAllStatusUser(Long userId) throws SQLException;
}
