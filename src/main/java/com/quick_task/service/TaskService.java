package com.quick_task.service;

import com.quick_task.dto.create.CreateTaskDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionTask;
import com.quick_task.dto.update.UpdateTaskDtoRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TaskService {

    CompletableFuture<List<TaskDtoResponse>> findAllTaskUser( String nameStatus);


    List<TaskDtoResponse> getByName(String parameter);

    CompletableFuture<TaskDtoResponse> createTask(CreateTaskDtoRequest project);

    CompletableFuture<TaskDtoResponse> updateTask(Long idTask, UpdateTaskDtoRequest dtoRequest);

    CompletableFuture<AllTaskByProjectDtoResponse> getTasksByProject(Long idProject, Boolean enable);

    CompletableFuture<List<AllTaskByProjectDtoResponse>> getAllTasksByProject();

    CompletableFuture<List<TaskDtoResponse>> getAllTaskWithoutProject( String nameStatus);

    CompletableFuture<FullInformationTaskDtoResponse>  getFullInformationTask(Long idTask) throws SQLException;

    CompletableFuture<List<StatusTaskDtoResponse>> getAllTaskByStatus();

    CompletableFuture<List<StatusTaskDtoResponse>> changeStatusPosition( UpdateChangePositionTask dtoRequest) throws SQLException;

    CompletableFuture<Void> changeProjectPosition(Long idTask, Long idProject) throws SQLException;

    CompletableFuture<Void> removeTaskById(Long IdProject) throws SQLException;

    CompletableFuture<Void> removeTaskFromProject(Long idTask) throws SQLException;

}
