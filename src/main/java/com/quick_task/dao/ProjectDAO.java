package com.quick_task.dao;

import com.quick_task.entity.Project;
import com.quick_task.entity.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ProjectDAO extends Dao{

    Project findById(Long id) throws SQLException;
    Project create(Project project) throws SQLException;
    List<Project> getAllByIdUser(Long idWebUser) throws SQLException;
    List<Project> findAllProjectsByStatus(Long userId, String nameStatus)  throws SQLException;
    List<Project> findProjectGroupedByStatus(Long userId, String nameStatus) throws SQLException;
    List<Project> getByName(String parameters) throws SQLException;
    Project update(Project project) throws SQLException;
    void removeById(Long idProject);





}
