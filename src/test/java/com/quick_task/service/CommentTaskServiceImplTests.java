package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateCommentTaskDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.factory.DaoFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentTaskServiceImplTests {

    @InjectMocks
    private CommentTaskServiceImpl commentTaskService;
    @Mock
    private TaskDAO taskDAO;
    @Mock
    private WebUserDAO webUserDAO;
    @Mock
    private CommentTaskDAO commentTaskDAO;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private Executor executor;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = Mockito.mockStatic(DaoFactory.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Testing create a commentTest with valid data")
    public void create() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        when(DaoFactory.getDao(TaskDAO.class)).thenReturn(taskDAO);
        when(DaoFactory.getDao(WebUserDAO.class)).thenReturn(webUserDAO);
        when(DaoFactory.getDao(CommentTaskDAO.class)).thenReturn(commentTaskDAO);

        when(taskDAO.findById(1L)).thenReturn(getTask());
        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(webUserDAO.findById(1L)).thenReturn(getWebUser());
        when(commentTaskDAO.create(any(CommentTask.class))).thenReturn(getCommentTask());

        CreateCommentTaskDtoRequest comment = mock(CreateCommentTaskDtoRequest.class);
        when(comment.getCommentText()).thenReturn("testNew");

        commentTaskService.createCommentTask(1L, comment);

        verify(commentTaskDAO).create(any(CommentTask.class));


    }

    @Test
    @DisplayName("Testing update a commentTest with valid data")
    public void update() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));
        when(DaoFactory.getDao(TaskDAO.class)).thenReturn(taskDAO);
        when(DaoFactory.getDao(WebUserDAO.class)).thenReturn(webUserDAO);
        when(DaoFactory.getDao(CommentTaskDAO.class)).thenReturn(commentTaskDAO);

        when(taskDAO.findById(1L)).thenReturn(getTask());
        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(webUserDAO.findById(1L)).thenReturn(getWebUser());
        when(commentTaskDAO.create(any(CommentTask.class))).thenReturn(getCommentTask());

        CreateCommentTaskDtoRequest comment = mock(CreateCommentTaskDtoRequest.class);
        when(comment.getCommentText()).thenReturn("test");

        commentTaskService.createCommentTask(1L, comment);

        verify(commentTaskDAO).create(any(CommentTask.class));
    }

    @Test @DisplayName("Testing delete a commentTest with valid data")
    public void delete() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));
        when(DaoFactory.getDao(CommentTaskDAO.class)).thenReturn(commentTaskDAO);

        commentTaskService.deleteCommentTaskById(1L);

        verify(commentTaskDAO).deleteById(1L);
    }

    private CommentTask getCommentTask() {
        return CommentTask.builder()
                .addCommentDate(LocalDateTime.now())
                .commentText("test")
                .task(getTask())
                .webUser(getWebUser())
                .build();
    }

    private WebUser getWebUser() {
        return WebUser.builder()
                .idWebUser(1L)
                .mailUser("test")
                .passwordUser("123")
                .build();
    }
    private Task getTask() {
        return Task.builder()
                .idTask(1L)
                .nameTask("test")
                .build();
    }
}
