package com.quick_task.dao;

import com.quick_task.entity.Task;

import java.sql.SQLException;
import java.util.List;

public interface TaskDAO extends Dao{

    Task findById(Long id) throws SQLException;
    Task create(Task task) throws SQLException;
    Task update(Task task) throws SQLException;
    List<Task> getAllByIdUser(Long idUser) throws SQLException;
    List<Task> getAllTaskWithoutProjectByIdUser(Long idUser) throws SQLException;
    List<Task> getByName(String parameter) throws SQLException;
    List<Task> getByProject(Long idProject) throws SQLException;
    List<Task> findAllTasksByStatus(Long userId, String nameStatus);
    void deleteById(Long idTask) throws SQLException;
    void deleteByUser(Long idUser) throws SQLException;
    List<Task> findSortedTasksWithFilters(Long userId, String nameStatus, Long idProject) throws SQLException;




}
