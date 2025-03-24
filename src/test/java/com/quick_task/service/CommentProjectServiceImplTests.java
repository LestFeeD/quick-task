package com.quick_task.service;

import com.quick_task.dao.CommentProjectDAO;
import com.quick_task.dao.ProjectDAO;
import com.quick_task.dao.WebUserDAO;
import com.quick_task.dto.create.CreateCommentProjectDtoRequest;
import com.quick_task.dto.update.UpdateCommentProjectDtoRequest;
import com.quick_task.entity.CommentProject;
import com.quick_task.entity.Project;
import com.quick_task.entity.WebUser;
import com.quick_task.factory.DaoFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentProjectServiceImplTests  {


    @InjectMocks
    private CommentProjectServiceImpl commentProjectService;

    @Mock
    private ProjectDAO projectDAO;
    @Mock
    private WebUserDAO webUserDAO;
    @Mock
    private CommentProjectDAO commentProjectDAO;
    @Mock
    private  AuthenticationService authenticationService;
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

    @Test @DisplayName("Testing create a commentProject with valid data")
    public void create() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        when(DaoFactory.getDao(ProjectDAO.class)).thenReturn(projectDAO);
        when(DaoFactory.getDao(WebUserDAO.class)).thenReturn(webUserDAO);
        when(DaoFactory.getDao(CommentProjectDAO.class)).thenReturn(commentProjectDAO);

        when(projectDAO.findById(1L)).thenReturn(getProject());
        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(webUserDAO.findById(1L)).thenReturn(getWebUser());
        when(commentProjectDAO.create(any(CommentProject.class))).thenReturn(getCommentProject());

        CreateCommentProjectDtoRequest comment = mock(CreateCommentProjectDtoRequest.class);
        when(comment.getCommentText()).thenReturn("test");

        commentProjectService.createCommentProject(1L, comment).get();

        verify(commentProjectDAO).create(any(CommentProject.class));
    }


    @Test @DisplayName("Testing update a commentProject with valid data")
    public void update() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        when(DaoFactory.getDao(CommentProjectDAO.class)).thenReturn(commentProjectDAO);

        CommentProject commentProject = getCommentProject();
        when(commentProjectDAO.findById(1L)).thenReturn(commentProject);

        when(commentProjectDAO.update(any(CommentProject.class))).thenReturn(commentProject);

        UpdateCommentProjectDtoRequest request = mock(UpdateCommentProjectDtoRequest.class);
        when(request.getCommentText()).thenReturn("testNew");

        commentProjectService.updateCommentProject(1L, request);

        verify(commentProjectDAO).update(any(CommentProject.class));

        assertEquals("testNew", commentProject.getCommentText());
    }

    @Test @DisplayName("Testing delete a commentProject with valid data")
    public void delete() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        when(DaoFactory.getDao(CommentProjectDAO.class)).thenReturn(commentProjectDAO);


        commentProjectService.deleteCommentProjectById(1L);

        verify(commentProjectDAO).deleteById(1L);
    }

    private CommentProject getCommentProject() {
        return CommentProject.builder()
                .addCommentDate(LocalDateTime.now())
                .commentText("test")
                .project(getProject())
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
    private Project getProject() {
        return Project.builder()
                .idProject(1L)
                .nameProject("test")
                .build();
    }
}
