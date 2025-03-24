package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateCommentProjectDtoRequest;
import com.quick_task.dto.create.CreateCommentTaskDtoRequest;
import com.quick_task.dto.response.AllCommentDtoResponse;
import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.dto.update.UpdateCommentProjectDtoRequest;
import com.quick_task.dto.update.UpdateCommentTaskDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.factory.CommentProjectFactory;
import com.quick_task.factory.DaoFactory;
import com.quick_task.utils.DBService;
import jakarta.validation.Valid;
import org.hibernate.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Validated
@Service
public class CommentProjectServiceImpl implements CommentProjectService{
    private final CommentProjectFactory commentProjectFactory;
    private final AuthenticationService authenticationService;
    private final Executor taskExecutor;

    public CommentProjectServiceImpl(CommentProjectFactory commentProjectFactory, AuthenticationService authenticationService, Executor taskExecutor) {
        this.commentProjectFactory = commentProjectFactory;
        this.authenticationService = authenticationService;
        this.taskExecutor = taskExecutor;
    }

    @Async
    @Override
    public CompletableFuture<List<AllCommentDtoResponse>> findCommentByProject(Long idProject) throws SQLException {
       return CompletableFuture.supplyAsync(() -> {
        Transaction transaction = DBService.getTransaction();
        try{
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            CommentProjectDAO commentProjectDAO = DaoFactory.getDao(CommentProjectDAO.class);
            assert commentProjectDAO != null;
            List<CommentProject> commentProjectList = commentProjectDAO.findByIdProject(idProject);
            transaction.commit();

            List<AllCommentDtoResponse> commentProjects = new ArrayList<>();
            for (CommentProject commentProject: commentProjectList) {
                AllCommentDtoResponse dtoResponse = commentProjectFactory.makeCommentProjectDto(commentProject);
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
    public CompletableFuture<Void> createCommentProject(Long idProject, CreateCommentProjectDtoRequest dtoRequest) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try{
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            CommentProjectDAO commentProjectDAO = DaoFactory.getDao(CommentProjectDAO.class);
            assert commentProjectDAO != null;
            Project project = projectDAO.findById(idProject);
            Long userId = authenticationService.getCurrentUserId();
            WebUser webUser = webUserDAO.findById(userId);

            CommentProject commentProject = CommentProject.builder()
                    .commentText(dtoRequest.getCommentText())
                    .project(project)
                    .webUser(webUser)
                    .build();
            commentProjectDAO.create(commentProject);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Async
    @Override
    public CompletableFuture<Void>  updateCommentProject(Long idCommentProject, UpdateCommentProjectDtoRequest dtoRequest) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();

        try{

            CommentProjectDAO commentProjectDAO = DaoFactory.getDao(CommentProjectDAO.class);
            assert commentProjectDAO != null;
            CommentProject commentProject = commentProjectDAO.findById(idCommentProject);
            if(dtoRequest.getCommentText() != null ) {
                commentProject.setCommentText(dtoRequest.getCommentText());
                commentProjectDAO.update(commentProject);

            } 
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Async
    @Override
    public CompletableFuture<Void> deleteCommentProjectById(Long idCommentProject) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try{
            CommentProjectDAO commentProjectDAO = DaoFactory.getDao(CommentProjectDAO.class);
            assert commentProjectDAO != null;
            commentProjectDAO.deleteById(idCommentProject);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }
}
