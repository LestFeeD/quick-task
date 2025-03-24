package com.quick_task.controller;

import com.quick_task.dto.create.CreateCommentTaskDtoRequest;
import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.response.AllTaskCommentDtoResponse;
import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.dto.update.UpdateCommentTaskDtoRequest;
import com.quick_task.dto.update.UpdateProjectDtoRequest;
import com.quick_task.service.CommentTaskServiceImpl;
import com.quick_task.service.ProjectServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@Tag(name = "CommentTask")
public class CommentTaskController {
    private final CommentTaskServiceImpl commentTaskService;

    public static final String FIND_OR_CREATE_COMMENT_TASK = "/tasks/{id_task}/comments";
    public static final String UPDATE_OR_DELETE_COMMENT_TASK = "/comments/{id_comment_task}";

    public CommentTaskController(CommentTaskServiceImpl commentTaskService) {
        this.commentTaskService = commentTaskService;
    }

    @GetMapping(FIND_OR_CREATE_COMMENT_TASK)
    public CompletableFuture<List<AllTaskCommentDtoResponse>> allCommentsTask(@PathVariable(value = "id_task") Long idTask) throws SQLException {
      return   commentTaskService.findCommentByTask(idTask);
    }
    @PostMapping(FIND_OR_CREATE_COMMENT_TASK)
    public ResponseEntity<Void> createComment(@PathVariable(value = "id_task") Long idTask, @Valid @RequestBody CreateCommentTaskDtoRequest dtoRequest) throws SQLException {
          commentTaskService.createCommentTask(idTask, dtoRequest);
        return  new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PatchMapping(UPDATE_OR_DELETE_COMMENT_TASK)
    public CompletableFuture<Void> updateComment(@PathVariable(value = "id_comment_task") Long idCommentTask,  @Valid @RequestBody UpdateCommentTaskDtoRequest dtoRequest) throws SQLException {
        return commentTaskService.updateCommentTask(idCommentTask, dtoRequest);
    }
    @DeleteMapping(UPDATE_OR_DELETE_COMMENT_TASK)
    public CompletableFuture<Void> deleteComment(@PathVariable(value = "id_project") Long idCommentTask) throws SQLException {
        return  commentTaskService.deleteCommentTaskById(idCommentTask);
    }
}
