package com.quick_task.dao;

import com.quick_task.entity.Status;
import com.quick_task.entity.StatusRole;

import java.sql.SQLException;
import java.util.List;

public interface StatusDAO extends Dao{

    Status findById(Long id) throws SQLException;
    Long findByDefaultName() throws SQLException;
    Long findIdByProjectId(Long idProject) throws SQLException;
    void createStatus(Status status) throws SQLException;
    List<Status> findAllStatusUser(Long userId) throws SQLException;
    Status update(Status status) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;






}
