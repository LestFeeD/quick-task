package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.response.AllProjectDtoResponse;
import com.quick_task.dto.response.InformationProjectDtoResponse;
import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.dto.response.StatusProjectBoardDtoResponse;
import com.quick_task.dto.update.UpdateChangePositionProject;
import com.quick_task.dto.update.UpdateProjectDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.factory.DaoFactory;
import com.quick_task.factory.ProjectFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTests {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private  ProjectFactory projectFactory;
    @Mock
    private TaskDAO taskDAO;
    @Mock
    private ProjectDAO projectDAO;
    @Mock
    private WebUserDAO webUserDAO;
    @Mock
    private PriorityDAO priorityDAO;
    @Mock
    private ProjectParticipantsDAO projectParticipantsDAO;
    @Mock
    private StatusDAO statusDAO;
    @Mock
    private StatusProjectDAO statusProjectDAO;
    @Mock
    private  Executor taskExecutor;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = Mockito.mockStatic(DaoFactory.class);
        when(DaoFactory.getDao(TaskDAO.class)).thenReturn(taskDAO);
        when(DaoFactory.getDao(ProjectDAO.class)).thenReturn(projectDAO);
        when(DaoFactory.getDao(PriorityDAO.class)).thenReturn(priorityDAO);
        when(DaoFactory.getDao(StatusDAO.class)).thenReturn(statusDAO);
        when(DaoFactory.getDao(WebUserDAO.class)).thenReturn(webUserDAO);
        when(DaoFactory.getDao(StatusProjectDAO.class)).thenReturn(statusProjectDAO);
        when(DaoFactory.getDao(ProjectParticipantsDAO.class)).thenReturn(projectParticipantsDAO);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void findAllProjectByStatus_FindAllProjectWithOffSort_ReturnProject() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Status status = new Status();
        status.setNameStatus("test");
        Project project = new Project();
        project.setIdProject(1L);
        List<Project> projectList = new ArrayList<>();
        projectList.add(project);
        List<Project> spyProjectList = spy(projectList);

        ProjectDtoResponse projectDtoResponse = new ProjectDtoResponse();
        projectDtoResponse.setIdProject(1L);
        List<ProjectDtoResponse> projectDtoResponses = new ArrayList<>();
        projectDtoResponses.add(projectDtoResponse);
        List<ProjectDtoResponse> spyProjectDtoResponses = spy(projectDtoResponses);


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(projectDAO.findProjectGroupedByStatus(1L, status.getNameStatus())).thenReturn(spyProjectList);
        when(statusDAO.findById(1L)).thenReturn(status);
        projectService.findAllProjectByStatus(1L, false);

        verify(projectDAO).findProjectGroupedByStatus(1L, status.getNameStatus());
        assertEquals(1L, spyProjectDtoResponses.iterator().next().getIdProject());
    }
    @Test
    void findAllProjectByStatus_FindAllProjectWithOnSort_ReturnProject() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Status status = new Status();
        status.setNameStatus("test");
        Project project = new Project();
        project.setIdProject(1L);
        List<Project> projectList = new ArrayList<>();
        projectList.add(project);
        List<Project> spyProjectList = spy(projectList);

        ProjectDtoResponse projectDtoResponse = new ProjectDtoResponse();
        projectDtoResponse.setIdProject(1L);
        List<ProjectDtoResponse> projectDtoResponses = new ArrayList<>();
        projectDtoResponses.add(projectDtoResponse);
        List<ProjectDtoResponse> spyProjectDtoResponses = spy(projectDtoResponses);


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(projectDAO.findProjectGroupedByStatus(1L, status.getNameStatus())).thenReturn(spyProjectList);
        when(statusDAO.findById(1L)).thenReturn(status);
        projectService.findAllProjectByStatus(1L, true);

        verify(projectDAO).findProjectGroupedByStatus(1L, status.getNameStatus());
        assertEquals(1L, spyProjectDtoResponses.iterator().next().getIdProject());
    }
    @Test
    void findAllProjectByStatus_FindAllProject_ReturnEmptyList() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Status status = new Status();
        status.setNameStatus("test");
        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusDAO.findById(1L)).thenReturn(status);

        when(projectDAO.findProjectGroupedByStatus(1L, status.getNameStatus())).thenReturn( new ArrayList<>());

        projectService.findAllProjectByStatus(1L, false);
        verify(projectDAO).findProjectGroupedByStatus(1L, status.getNameStatus());
    }
    @Test
    void getProjectName() throws SQLException {

        when(projectDAO.findById(1L)).thenReturn( getProject());

        projectService.getProjectName(1L);

        verify(projectDAO).findById(1L);
    }
    @Test
    void getByName_FindProjectByName_ReturnProject() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        List<Project> projectList = new ArrayList<>();
        Project project = Mockito.mock(Project.class);
        projectList.add(project);

        when(projectDAO.getByName("parameter")).thenReturn(projectList);

        ProjectDtoResponse response = Mockito.mock(ProjectDtoResponse.class);
        when(projectFactory.makeProjectDto(any(Project.class))).thenReturn(response);

        projectService.getByName("parameter");

        verify(projectDAO).getByName("parameter");

    }
    @Test
    void getByName_ReturnEmptyList() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        List<Project> projectList = new ArrayList<>();

        when(projectDAO.getByName("parameter")).thenReturn(projectList);

        projectService.getByName("parameter");

        verify(projectDAO).getByName("parameter");


    }
    @Test
    void createProject_CreateProjectForUser_ReturnCreatedProject() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        CreateProjectDtoRequest createProjectDtoRequest = Mockito.mock(CreateProjectDtoRequest.class);
        when(createProjectDtoRequest.getIdPriority()).thenReturn(1L);

        Status status = new Status();
        status.setNameStatus("Begin");
        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusDAO.findById(1L)).thenReturn(new Status());
        when(statusDAO.findByDefaultName()).thenReturn(1L);
        when(priorityDAO.findById(1L)).thenReturn(getPriority());
        when(webUserDAO.findById(1L)).thenReturn(getWebUser());
        when(projectDAO.create(any(Project.class))).thenReturn(getProject());
        StatusProject statusProject = Mockito.mock(StatusProject.class);

        when(statusProjectDAO.findStatusProjectByRightStatusTaskIsNullAndUserId(getWebUser().getIdWebUser())).thenReturn(Optional.of(statusProject));
        when(statusProjectDAO.create(any(StatusProject.class))).thenReturn(statusProject);

        doNothing().when(projectParticipantsDAO).create(any(ProjectParticipants.class));

        ProjectDtoResponse response = Mockito.mock(ProjectDtoResponse.class);
        when(projectFactory.makeProjectDto(any(Project.class))).thenReturn(response);


        projectService.createProject(createProjectDtoRequest).get();
        verify(projectDAO).create(any(Project.class));

    }

    @Test
    void updateProject_UpdateProjectForUser_ReturnUpdatedProject() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        UpdateProjectDtoRequest updateProjectDtoRequest = new UpdateProjectDtoRequest();
        updateProjectDtoRequest.setDescriptionProject("newDescription");
        updateProjectDtoRequest.setIdStatus(2L);
        updateProjectDtoRequest.setIdPriority(2L);

        Status status = new Status();
        status.setIdStatus(1L);
        StatusProject statusProject = new StatusProject();
        statusProject.setStatus(status);

        Priority priority = new Priority();
        priority.setIdPriority(2L);

        Project project = new Project();
        project.setDescriptionProject("old");
        project.setPriority(getPriority());
        project.setStatusProjectSet(new HashSet<>(Arrays.asList(statusProject)));

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(projectDAO.findById(getProject().getIdProject())).thenReturn(project);
        when(priorityDAO.findById(2L)).thenReturn(priority);
        when(statusDAO.findById(2L)).thenReturn(getNewStatus());
        when(statusProjectDAO.findByProjectId(getProject().getIdProject())).thenReturn(statusProject);
        when(projectDAO.update(any(Project.class))).thenReturn(project);

        ProjectDtoResponse response = Mockito.mock(ProjectDtoResponse.class);
        when(projectFactory.makeProjectDto(any(Project.class))).thenReturn(response);

        projectService.updateProject(1L, updateProjectDtoRequest).get();

        verify(projectDAO).update(any(Project.class));
        verify(statusProjectDAO).update(any(StatusProject.class));

        assertEquals(updateProjectDtoRequest.getDescriptionProject() , project.getDescriptionProject());
        assertEquals(updateProjectDtoRequest.getIdPriority(), project.getPriority().getIdPriority());
        assertEquals(updateProjectDtoRequest.getIdStatus(), project.getStatusProjectSet().iterator().next().getStatus().getIdStatus());
    }

    @Test
    void getInformationProject_FindInformationAboutProject_ReturnInformation() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Project project = Mockito.mock(Project.class);
        StatusProject statusProject = Mockito.mock(StatusProject.class);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusProjectDAO.findByProjectId(1L)).thenReturn(statusProject);
        when(projectDAO.findById(getProject().getIdProject())).thenReturn(project);

        InformationProjectDtoResponse response = Mockito.mock(InformationProjectDtoResponse.class);
        when(projectFactory.makeInformationProjectDto(project, statusProject)).thenReturn(response);

        projectService.getInformationProject(1L);

        verify(projectDAO).findById(1L);
        verify(statusProjectDAO).findByProjectId(1L);

    }

    @Test
    void getAllProjectBoardByStatus_FindAllProjectForBoard_ReturnProject() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Project project = Mockito.mock(Project.class);
        Status status = new Status();
        status.setIdStatus(1L);
        status.setNameStatus("Begin");
        List<Long> idStatuses = new ArrayList<>();
        idStatuses.add(1L);

        List<Project> projectsByStatus = new ArrayList<>();
        projectsByStatus.add(project);

        AllProjectDtoResponse projectDtoResponse = Mockito.mock(AllProjectDtoResponse.class);
        List<StatusProjectBoardDtoResponse> statusProjectDtoResponses = new ArrayList<>();

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusProjectDAO.findByAllStatusUser(1L)).thenReturn(idStatuses);
        when(statusDAO.findById(1L)).thenReturn(status);
        when(projectDAO.findAllProjectsByStatus(1L, status.getNameStatus())).thenReturn(projectsByStatus);
        when(projectFactory.makeAllProjectDto(project,  status.getNameStatus())).thenReturn(projectDtoResponse);

        projectService.getAllProjectBoardByStatus().get();

        verify(statusProjectDAO).findByAllStatusUser(1L);
        verify(projectDAO).findAllProjectsByStatus(1L, status.getNameStatus());
        verify(projectFactory).makeAllProjectDto(project, status.getNameStatus());

    }

    @Test
    void changeStatusPosition_newStatusAndFirstPlace() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        StatusProject statusProject = Mockito.mock(StatusProject.class);
        Project project = Mockito.mock(Project.class);
        Status status = Mockito.mock(Status.class);

        UpdateChangePositionProject dtoRequest = new UpdateChangePositionProject();
        dtoRequest.setLeftProjectId(null);
        dtoRequest.setRightProjectId(null);
        dtoRequest.setNewStatusId(1L);
        dtoRequest.setProjectId(1L);

        UpdateChangePositionProject spyDtoRequest = spy(dtoRequest);


        List<Long> idStatuses = new ArrayList<>();
        idStatuses.add(1L);

        List<Project> projectsByStatus = new ArrayList<>();
        projectsByStatus.add(project);

        ProjectServiceImpl spyProjectService = Mockito.spy(projectService);
        doNothing().when(spyProjectService).replaceOldTaskPosition(any(StatusProject.class));

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusDAO.findById(1L)).thenReturn(status);
        when(statusProjectDAO.findByProjectId(getProject().getIdProject())).thenReturn(statusProject);
        when(statusProjectDAO.update(any(StatusProject.class))).thenReturn(statusProject);
        when(statusProjectDAO.findByAllStatusUser(1L)).thenReturn(idStatuses);


        spyProjectService.changeStatusPosition(spyDtoRequest);


        projectService.changeStatusPosition(spyDtoRequest);
        verify(statusDAO, times(6)).findById(1L);
        verify(statusProjectDAO, times(2)).update(statusProject);


    }

    @Test
    void changeStatusPosition_AnotherPlaceTheSameStatus() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Long projectLeftId = 1L;
        Long projectRightId = 3L;

        StatusProject leftProject = Mockito.mock(StatusProject.class);
        StatusProject rightProject = Mockito.mock(StatusProject.class);
        StatusProject statusProject = Mockito.mock(StatusProject.class);
        Project project = Mockito.mock(Project.class);

        UpdateChangePositionProject dtoRequest = new UpdateChangePositionProject();
        dtoRequest.setLeftProjectId(1L);
        dtoRequest.setRightProjectId(3L);
        dtoRequest.setProjectId(1L);

        UpdateChangePositionProject spyDtoRequest = spy(dtoRequest);


        List<Long> idStatuses = new ArrayList<>();
        idStatuses.add(1L);

        List<Project> projectsByStatus = new ArrayList<>();
        projectsByStatus.add(project);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusProjectDAO.findByProjectId(spyDtoRequest.getProjectId())).thenReturn(statusProject);
        when(statusProjectDAO.findByAllStatusUser(1L)).thenReturn(idStatuses);
        when(statusProjectDAO.update(statusProject)).thenReturn(statusProject);
        when(statusProjectDAO.findById(projectLeftId)).thenReturn(leftProject);
        when(statusProjectDAO.findById(projectRightId)).thenReturn(rightProject);

        projectService.changeStatusPosition(spyDtoRequest);

        verify(statusProjectDAO, times(1)).update(leftProject);
        verify(statusProjectDAO, times(1)).update(rightProject);

        verify(statusProjectDAO, times(1)).update(statusProject);
    }
    @Test
    void removeProject_DeleteProject_SuccessfulDelete () throws SQLException, ExecutionException, InterruptedException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        List<Task> allTaskByProjectId = new ArrayList<>();
        allTaskByProjectId.add(new Task());

        Project project = Mockito.mock(Project.class);
        StatusProject statusProject = Mockito.mock(StatusProject.class);
        StatusProject leftStatusProject = Mockito.mock(StatusProject.class);
        StatusProject rightStatusProject = Mockito.mock(StatusProject.class);

        when(projectDAO.findById(1L)).thenReturn(project);
        when(taskDAO.getByProject(1L)).thenReturn(allTaskByProjectId);
        when(statusProjectDAO.findByProjectId(1L)).thenReturn(statusProject);
        when(statusProject.getIdStatusProject()).thenReturn(1L);
        when(statusProject.getIdLeftProjectStatus()).thenReturn(leftStatusProject);
        when(statusProject.getIdRightProjectStatus()).thenReturn(rightStatusProject);

        when(statusProjectDAO.update(leftStatusProject)).thenReturn(leftStatusProject);
        when(statusProjectDAO.update(rightStatusProject)).thenReturn(rightStatusProject);

        doNothing().when(statusProjectDAO).delete(1L);
        doNothing().when(projectParticipantsDAO).deleteByProject(1L);
        doNothing().when(projectDAO).removeById(1L);

        projectService.removeProjectById(1L).get();

        verify(projectDAO).removeById(1L);
        verify(statusProjectDAO).delete(1L);
        verify(statusProjectDAO).update(leftStatusProject);
        verify(statusProjectDAO).update(rightStatusProject);
    }


    @Test
    void removeProject_DeleteProjectAndStatusProjectRightIdNull() throws SQLException, ExecutionException, InterruptedException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Project project = Mockito.mock(Project.class);
        StatusProject statusProject = Mockito.mock(StatusProject.class);
        StatusProject leftStatusProject = Mockito.mock(StatusProject.class);
        StatusProject rightStatusProject = Mockito.mock(StatusProject.class);

        List<Task> allTaskByProjectId = new ArrayList<>();
        allTaskByProjectId.add(new Task());

        when(projectDAO.findById(1L)).thenReturn(project);
        when(taskDAO.getByProject(1L)).thenReturn(allTaskByProjectId);
        when(statusProjectDAO.findByProjectId(1L)).thenReturn(statusProject);
        when(statusProject.getIdStatusProject()).thenReturn(1L);
        when(statusProject.getIdLeftProjectStatus()).thenReturn(leftStatusProject);
        when(statusProject.getIdRightProjectStatus()).thenReturn(null);

        when(statusProjectDAO.update(leftStatusProject)).thenReturn(leftStatusProject);

        doNothing().when(statusProjectDAO).delete(1L);
        doNothing().when(projectParticipantsDAO).deleteByProject(1L);
        doNothing().when(projectDAO).removeById(1L);

        projectService.removeProjectById(1L).get();

        verify(projectDAO).removeById(1L);
        verify(statusProjectDAO).delete(1L);
        verify(statusProjectDAO).update(leftStatusProject);
        verify(statusProjectDAO, never()).update(rightStatusProject);
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
                .priority(getPriority())
                .statusProjectSet(Collections.singleton(getStatusProject()))
                .descriptionProject("description")
                .nameProject("test")
                .build();
    }
    private Priority getPriority() {
        return Priority.builder()
                .idPriority(1L)
                .namePriority("test")
                .build();
    }

    private Status getStatus() {
        return Status.builder()
                .idStatus(1L)
                .nameStatus("test")
                .build();
    }
    private Status getNewStatus() {
        return Status.builder()
                .idStatus(2L)
                .nameStatus("newTest")
                .build();
    }

    private StatusProject getStatusProject() {
        return StatusProject.builder()
                .idStatusProject(1L)
                .idLeftProjectStatus(null)
                .idRightProjectStatus(null)
                .status(getStatus())
                .build();
    }
    private ProjectParticipants getProjectParticipants() {
        return ProjectParticipants.builder()
                .webUser(getWebUser())
                .project(getProject())
                .ownerProject(1)
                .build();
    }
}
