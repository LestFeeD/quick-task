package com.quick_task.controller;

import com.quick_task.dto.create.CreateCommentProjectDtoRequest;
import com.quick_task.dto.response.AllCommentDtoResponse;
import com.quick_task.dto.update.UpdateCommentProjectDtoRequest;
import com.quick_task.service.CommentProjectServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@Tag(name = "CommentProject")
public class CommentProjectController {
    private final CommentProjectServiceImpl commentProjectService;

    public static final String FIND_OR_CREATE_COMMENT_PROJECT = "/projects/{id_project}/comments";
    public static final String UPDATE_OR_DELETE_COMMENT_PROJECT = "/comments/{id_comment_project}";

    public CommentProjectController(CommentProjectServiceImpl commentTaskService) {
        this.commentProjectService = commentTaskService;
    }

    @GetMapping(FIND_OR_CREATE_COMMENT_PROJECT)
    public CompletableFuture<List<AllCommentDtoResponse>> allCommentsTask(@PathVariable(value = "id_task") Long idTask) throws SQLException {
       return commentProjectService.findCommentByProject(idTask);
    }

    @PostMapping(FIND_OR_CREATE_COMMENT_PROJECT)
    public ResponseEntity<Void> allProjectsUser(@PathVariable(value = "id_project") Long idTask, @Valid @RequestBody CreateCommentProjectDtoRequest dtoRequest) throws SQLException {
        commentProjectService.createCommentProject(idTask, dtoRequest);
        return  new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping(UPDATE_OR_DELETE_COMMENT_PROJECT)
    public CompletableFuture<Void> updateComment(@PathVariable(value = "id_comment_project") Long idCommentTask,  @Valid @RequestBody UpdateCommentProjectDtoRequest dtoRequest) throws SQLException {
       return commentProjectService.updateCommentProject(idCommentTask, dtoRequest);
    }
    @DeleteMapping(UPDATE_OR_DELETE_COMMENT_PROJECT)
    public CompletableFuture<Void> deleteComment(@PathVariable(value = "id_project") Long idCommentTask) throws SQLException {
        return commentProjectService.deleteCommentProjectById(idCommentTask);
    }
}
