package com.quick_task.service;

import com.quick_task.dto.create.CreateCommentTaskDtoRequest;
import com.quick_task.dto.response.AllTaskCommentDtoResponse;
import com.quick_task.dto.update.UpdateCommentTaskDtoRequest;
import com.quick_task.entity.CommentTask;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommentTaskService {

    CompletableFuture<List<AllTaskCommentDtoResponse>> findCommentByTask(Long idTask);
    CompletableFuture<Void> createCommentTask(Long idTask, CreateCommentTaskDtoRequest dtoRequest) throws SQLException;
    CompletableFuture<Void> updateCommentTask(Long idCommentTask, UpdateCommentTaskDtoRequest dtoRequest) throws SQLException;
    CompletableFuture<Void> deleteCommentTaskById (Long idCommentTask) throws SQLException;
}
