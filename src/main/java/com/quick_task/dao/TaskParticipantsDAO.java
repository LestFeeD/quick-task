package com.quick_task.dao;

import com.quick_task.entity.TaskParticipants;

import java.sql.SQLException;

public interface TaskParticipantsDAO extends Dao{
    void create(TaskParticipants entity) throws SQLException;
    void deleteByTask(Long idTask) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;
}
