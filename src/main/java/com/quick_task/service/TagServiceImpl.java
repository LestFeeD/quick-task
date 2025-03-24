package com.quick_task.service;

import com.quick_task.dao.TagDAO;
import com.quick_task.dao.TaskDAO;
import com.quick_task.dto.response.AllTagsDtoResponse;
import com.quick_task.entity.Tag;
import com.quick_task.factory.DaoFactory;
import com.quick_task.utils.DBService;
import org.hibernate.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class TagServiceImpl implements TagService{
    private final AuthenticationService authenticationService;
    private final Executor taskExecutor;

    public TagServiceImpl(AuthenticationService authenticationService, Executor taskExecutor) {
        this.authenticationService = authenticationService;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void deleteTagFromTask(Long idTask, Long idTag) {
        Transaction transaction = DBService.getTransaction();
        try {
            TagDAO tagDAO = DaoFactory.getDao(TagDAO.class);
            assert tagDAO != null;
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            tagDAO.deleteFromTask(idTask, idTag);
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public CompletableFuture<List<AllTagsDtoResponse>> findTagsByTask(Long idTask) {
        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = DBService.getTransaction();
            try {
                TagDAO tagDAO = DaoFactory.getDao(TagDAO.class);
                assert tagDAO != null;
                List<Tag> tagsList = tagDAO.findByIdTask(idTask);
                transaction.commit();
                List<AllTagsDtoResponse> allTagsDtoResponses = new ArrayList<>();
                for(Tag tag: tagsList) {
                    AllTagsDtoResponse dtoResponse = new AllTagsDtoResponse();
                    dtoResponse.setIdTags(tag.getIdTag());
                    dtoResponse.setNameTag(tag.getNameTag());
                    allTagsDtoResponses.add(dtoResponse);
                }
                return allTagsDtoResponses;
            } catch (SQLException e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }}, taskExecutor);
        }
}

