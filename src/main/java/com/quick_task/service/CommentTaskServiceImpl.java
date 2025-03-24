package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateCommentTaskDtoRequest;
import com.quick_task.dto.response.AllCommentDtoResponse;
import com.quick_task.dto.response.AllTaskCommentDtoResponse;
import com.quick_task.dto.update.UpdateCommentTaskDtoRequest;
import com.quick_task.entity.CommentProject;
import com.quick_task.entity.CommentTask;
import com.quick_task.entity.Task;
import com.quick_task.entity.WebUser;
import com.quick_task.factory.CommentTaskFactory;
import com.quick_task.factory.DaoFactory;
import com.quick_task.utils.DBService;
import jakarta.validation.Valid;
import org.hibernate.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class CommentTaskServiceImpl implements CommentTaskService{
    private final AuthenticationService authenticationService;
    private final Executor taskExecutor;
    private final CommentTaskFactory commentTaskFactory;

    public CommentTaskServiceImpl(AuthenticationService authenticationService, Executor taskExecutor, CommentTaskFactory commentTaskFactory) {
        this.authenticationService = authenticationService;
        this.taskExecutor = taskExecutor;
        this.commentTaskFactory = commentTaskFactory;
    }

    @Async
    @Override
    public CompletableFuture<List<AllTaskCommentDtoResponse>> findCommentByTask(Long idTask) {
        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = DBService.getTransaction();
            try{
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;
                CommentTaskDAO commentTaskDAO = DaoFactory.getDao(CommentTaskDAO.class);
                assert commentTaskDAO != null;
                List<CommentTask> commentTaskList = commentTaskDAO.findByIdTask(idTask);
                transaction.commit();

                List<AllTaskCommentDtoResponse> commentProjects = new ArrayList<>();
                for (CommentTask commentTask: commentTaskList) {
                    AllTaskCommentDtoResponse dtoResponse = commentTaskFactory.makeCommentTaskDto(commentTask);
                    commentProjects.add(dtoResponse);
                }
                return commentProjects;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }


    @Async
    @Override
    public CompletableFuture<Void> createCommentTask(Long idTask, CreateCommentTaskDtoRequest dtoRequest) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try{
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            Long userId = authenticationService.getCurrentUserId();
            WebUser webUser = webUserDAO.findById(userId);
            CommentTaskDAO commentTaskDAO = DaoFactory.getDao(CommentTaskDAO.class);
            assert commentTaskDAO != null;
            Task task = taskDao.findById(idTask);
            CommentTask commentTask = CommentTask.builder()
                    .commentText(dtoRequest.getCommentText())
                    .task(task)
                    .webUser(webUser)
                    .build();
            commentTaskDAO.create(commentTask);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Async
    @Override
    public CompletableFuture<Void> updateCommentTask(Long idCommentTask, UpdateCommentTaskDtoRequest dtoRequest) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();

        try{

            CommentTaskDAO commentTaskDAO = DaoFactory.getDao(CommentTaskDAO.class);
            assert commentTaskDAO != null;
            CommentTask commentTask = commentTaskDAO.findById(idCommentTask);
            if(dtoRequest.getCommentText() != null && dtoRequest.getCommentText().isEmpty()) {
                commentTask.setCommentText(dtoRequest.getCommentText());
                commentTaskDAO.update(commentTask);

            }
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Async
    @Override
    public CompletableFuture<Void> deleteCommentTaskById(Long idCommentTask) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try{
            CommentTaskDAO commentTaskDAO = DaoFactory.getDao(CommentTaskDAO.class);
            assert commentTaskDAO != null;
            commentTaskDAO.deleteById(idCommentTask);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }
}
