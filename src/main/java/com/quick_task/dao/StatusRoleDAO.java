package com.quick_task.dao;

import com.quick_task.entity.Status;
import com.quick_task.entity.StatusRole;
import com.quick_task.entity.StatusTask;

import java.sql.SQLException;

public interface StatusRoleDAO extends Dao{
    StatusRole findById(Long id) throws SQLException;
    StatusRole findByIdStatus(Long id) throws SQLException;
    StatusRole update(StatusRole statusRole) throws SQLException;

}
