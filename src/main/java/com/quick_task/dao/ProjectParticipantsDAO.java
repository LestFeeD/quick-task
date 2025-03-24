package com.quick_task.dao;

import com.quick_task.entity.*;

import java.sql.SQLException;
import java.util.List;

public interface ProjectParticipantsDAO  extends Dao{
    //also may create to find where users not owner
    List<ProjectParticipants> find(Long userId);
    void create(ProjectParticipants entity) throws SQLException;
    void deleteByProject(Long projectId) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;



}
