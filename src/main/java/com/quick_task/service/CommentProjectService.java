package com.quick_task.service;

import com.quick_task.dto.create.CreateCommentProjectDtoRequest;
import com.quick_task.dto.create.CreateCommentTaskDtoRequest;
import com.quick_task.dto.response.AllCommentDtoResponse;
import com.quick_task.dto.update.UpdateCommentProjectDtoRequest;
import com.quick_task.dto.update.UpdateCommentTaskDtoRequest;
import com.quick_task.entity.CommentProject;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommentProjectService {
    CompletableFuture<List<AllCommentDtoResponse>> findCommentByProject(Long idProject) throws SQLException;
    CompletableFuture<Void> createCommentProject(Long idProject, CreateCommentProjectDtoRequest dtoRequest) throws SQLException;
    CompletableFuture<Void> updateCommentProject(Long idCommentProject, UpdateCommentProjectDtoRequest dtoRequest) throws SQLException;
    CompletableFuture<Void> deleteCommentProjectById (Long idCommentProject) throws SQLException;

}
