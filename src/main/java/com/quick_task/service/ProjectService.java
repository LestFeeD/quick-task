package com.quick_task.service;

import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionProject;
import com.quick_task.dto.update.UpdateProjectDtoRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProjectService {

    CompletableFuture<List<ProjectDtoResponse>> findAllProject();

    CompletableFuture<List<ProjectDtoResponse>> findAllProjectByStatus(Long idStatus, Boolean enable);

    String getProjectName(Long idProject) throws SQLException;

    CompletableFuture<List<ProjectDtoResponse>> getByName(String parameter);

    CompletableFuture<ProjectDtoResponse> createProject(CreateProjectDtoRequest project);

    CompletableFuture<ProjectDtoResponse> updateProject(Long idProject, UpdateProjectDtoRequest project);

    CompletableFuture<InformationProjectDtoResponse> getInformationProject(Long idProject) throws SQLException;

    CompletableFuture<List<StatusProjectBoardDtoResponse>> getAllProjectBoardByStatus();

    CompletableFuture<List<StatusAllProjectDtoResponse>> getAllProjectByStatus();

    CompletableFuture<List<StatusProjectBoardDtoResponse>> changeStatusPosition( UpdateChangePositionProject dtoRequest) throws SQLException;

    CompletableFuture<Void> removeProjectById(Long IdProject) throws SQLException;
}
