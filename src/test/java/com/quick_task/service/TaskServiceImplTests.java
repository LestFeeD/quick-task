package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateTaskDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionTask;
import com.quick_task.dto.update.UpdateTaskDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.factory.DaoFactory;
import com.quick_task.factory.TaskFactory;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTests {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private TaskFactory taskFactory;
    @Mock
    private TaskDAO taskDAO;
    @Mock
    private WebUserDAO webUserDAO;
    @Mock
    private PriorityDAO priorityDAO;
    @Mock
    private TagDAO tagDAO;
    @Mock
    private ProjectDAO projectDAO;
    @Mock
    private TaskParticipantsDAO taskParticipantsDAO;
    @Mock
    private StatusDAO statusDAO;
    @Mock
    private StatusTaskDAO statusTaskDAO;
    @Mock
    private Executor taskExecutor;

    @InjectMocks
    private TaskServiceImpl taskService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = Mockito.mockStatic(DaoFactory.class);

        when(DaoFactory.getDao(TaskDAO.class)).thenReturn(taskDAO);
        when(DaoFactory.getDao(PriorityDAO.class)).thenReturn(priorityDAO);
        when(DaoFactory.getDao(StatusDAO.class)).thenReturn(statusDAO);
        when(DaoFactory.getDao(WebUserDAO.class)).thenReturn(webUserDAO);
        when(DaoFactory.getDao(StatusTaskDAO.class)).thenReturn(statusTaskDAO);
        when(DaoFactory.getDao(TaskParticipantsDAO.class)).thenReturn(taskParticipantsDAO);
        when(DaoFactory.getDao(ProjectDAO.class)).thenReturn(projectDAO);
        when(DaoFactory.getDao(TagDAO.class)).thenReturn(tagDAO);


    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void findAllTaskUser_NameStatusNotIsNull_ReturnProject() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        String nameStatus = "Created";
        Task task = new Task();
        task.setIdTask(1L);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<Task> spyTaskList = spy(taskList);

        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        taskDtoResponse.setIdTask(1L);
        List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        taskDtoResponses.add(taskDtoResponse);
        List<TaskDtoResponse> spyTaskResponse = spy(taskDtoResponses);


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.findSortedTasksWithFilters(1L, nameStatus, null)).thenReturn(spyTaskList);

        taskService.findAllTaskUser(nameStatus);

        verify(taskDAO).findSortedTasksWithFilters(1L, nameStatus, null);
        verify(taskDAO, never()).getAllByIdUser(1L);
        assertEquals(1L, spyTaskResponse.iterator().next().getIdTask());
    }

    @Test
    void findAllTaskUser_NameStatusNotNull_ReturnProject() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        String nameStatus = null;
        Task task = new Task();
        task.setIdTask(1L);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<Task> spyTaskList = spy(taskList);

        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        taskDtoResponse.setIdTask(1L);
        List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        taskDtoResponses.add(taskDtoResponse);
        List<TaskDtoResponse> spyTaskResponse = spy(taskDtoResponses);


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.getAllByIdUser(1L)).thenReturn(spyTaskList);

        taskService.findAllTaskUser(null);

        verify(taskDAO, never()).findSortedTasksWithFilters(1L, nameStatus, null);
        verify(taskDAO).getAllByIdUser(1L);
        assertEquals(1L, spyTaskResponse.iterator().next().getIdTask());
    }

    @Test
    void getAllTaskByProject_NameStatusNotIsNull_ReturnProject() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        String nameStatus = "Created";
        Task task = new Task();
        task.setIdTask(1L);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<Task> spyTaskList = spy(taskList);

        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        taskDtoResponse.setIdTask(1L);
        List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        taskDtoResponses.add(taskDtoResponse);
        List<TaskDtoResponse> spyTaskResponse = spy(taskDtoResponses);


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.findSortedTasksWithFilters(1L, nameStatus, null)).thenReturn(spyTaskList);

        taskService.getAllTaskWithoutProject(nameStatus);

        verify(taskDAO).findSortedTasksWithFilters(1L, nameStatus, null);
        verify(taskDAO, never()).getAllByIdUser(1L);
        assertEquals(1L, spyTaskResponse.iterator().next().getIdTask());
    }

    @Test
    void getAllTaskWithoutProject_NameStatusNotNull_ReturnProject() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        String nameStatus = null;
        Task task = new Task();
        task.setIdTask(1L);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<Task> spyTaskList = spy(taskList);

        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        taskDtoResponse.setIdTask(1L);
        List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        taskDtoResponses.add(taskDtoResponse);
        List<TaskDtoResponse> spyTaskResponse = spy(taskDtoResponses);


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.getByProject(1L)).thenReturn(spyTaskList);

        taskService.getTasksByProject(1L, null);

        verify(taskDAO, never()).findSortedTasksWithFilters(1L, nameStatus, null);
        verify(taskDAO).getByProject(1L);
        assertEquals(1L, spyTaskResponse.iterator().next().getIdTask());
    }


    @Test
    void getFullInformationTask_FindInformation_ReturnTaskAndHisStatus() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Task task = Mockito.mock(Task.class);
        StatusTask statusTask = Mockito.mock(StatusTask.class);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.findById(1L)).thenReturn(task);
        when(statusTaskDAO.findByTaskId(1L)).thenReturn(statusTask);


        taskService.getFullInformationTask(1L);

        verify(taskDAO).findById(1L);
    }

    @Test
    void getAllTaskByStatus_NameStatusNotNull_ReturnProject() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        String nameStatus = "Test";
        List<String> statuses = new ArrayList<>();
        statuses.add(nameStatus);
        List<String> spyStatuses = spy(statuses);

        Task task = new Task();
        task.setIdTask(1L);
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        List<Task> spyTaskList = spy(taskList);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusTaskDAO.findByAllStatusUser(1L)).thenReturn(spyStatuses);
        when(taskDAO.findAllTasksByStatus(1L, nameStatus)).thenReturn(spyTaskList);
        taskService.getAllTaskByStatus();

        assertEquals(1L, spyTaskList.iterator().next().getIdTask());
    }


    @Test
    void changeStatusPosition_newStatusAndFirstPlace() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        StatusTask statusTask = Mockito.mock(StatusTask.class);
        Task task = Mockito.mock(Task.class);
        Status status = Mockito.mock(Status.class);

        UpdateChangePositionTask dtoRequest = new UpdateChangePositionTask();
        dtoRequest.setLeftTaskId(null);
        dtoRequest.setRightTaskId(null);
        dtoRequest.setNewStatusId(1L);
        dtoRequest.setTaskId(1L);

        UpdateChangePositionTask spyDtoRequest = spy(dtoRequest);


        List<String> statusNames  = new ArrayList<>();
        statusNames.add("Begin");

        List<Task> tasksByStatus = new ArrayList<>();
        tasksByStatus.add(task);

        TaskServiceImpl spyTaskService = Mockito.spy(taskService);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusDAO.findById(1L)).thenReturn(status);
        when(statusTaskDAO.findByTaskId(getTask().getIdTask())).thenReturn(statusTask);
        when(statusTaskDAO.update(any(StatusTask.class))).thenReturn(statusTask);
        when(statusTaskDAO.findByAllStatusUser(1L)).thenReturn(statusNames);



        taskService.changeStatusPosition(spyDtoRequest);
        verify(statusDAO, times(1)).findById(1L);


    }

    @Test
    void changeStatusPosition_AnotherPlaceTheSameStatus() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Long taskLeftId = 1L;
        Long taskRightId = 3L;

        StatusTask leftTask = Mockito.mock(StatusTask.class);
        StatusTask rightTask = Mockito.mock(StatusTask.class);
        StatusTask statusTask = new StatusTask();
        statusTask.setStatus(getStatus());


        StatusTask spyStatusTask = spy(statusTask);
        Task task = Mockito.mock(Task.class);

        UpdateChangePositionTask dtoRequest = new UpdateChangePositionTask();
        dtoRequest.setLeftTaskId(taskLeftId);
        dtoRequest.setRightTaskId(taskRightId);
        dtoRequest.setTaskId(2L);


        UpdateChangePositionTask spyDtoRequest = spy(dtoRequest);


        List<String> statusNames  = new ArrayList<>();
        statusNames.add("Begin");

        List<Task> projectsByStatus = new ArrayList<>();
        projectsByStatus.add(task);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(statusTaskDAO.findByTaskId(spyDtoRequest.getTaskId())).thenReturn(spyStatusTask);
        when(statusTaskDAO.findByAllStatusUser(1L)).thenReturn(statusNames);
        when(statusTaskDAO.update(spyStatusTask)).thenReturn(spyStatusTask);
        when(statusTaskDAO.findById(taskLeftId)).thenReturn(leftTask);
        when(statusTaskDAO.findById(taskRightId)).thenReturn(rightTask);



        taskService.changeStatusPosition(spyDtoRequest);

        verify(statusTaskDAO, times(1)).findById(taskLeftId);

    }

    @Test
    void createTask_CreateTaskInProject_ReturnCreatedTask() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        CreateTaskDtoRequest createTaskDtoRequest = Mockito.mock(CreateTaskDtoRequest.class);
        when(createTaskDtoRequest.getIdProject()).thenReturn(1L);

        WebUser mockWebUser = Mockito.mock(WebUser.class);
        when(mockWebUser.getIdWebUser()).thenReturn(1L);

        Project mockProject = Mockito.mock(Project.class);

        Task mockTask = Mockito.mock(Task.class);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(webUserDAO.findById(1L)).thenReturn(mockWebUser);
        when(statusDAO.findByDefaultName()).thenReturn(1L);
        when(projectDAO.findById(1L)).thenReturn(mockProject);
        when(taskDAO.create(any(Task.class))).thenReturn(mockTask);

        StatusTask statusTask = Mockito.mock(StatusTask.class);

        when(statusTaskDAO.findStatusTaskByRightStatusTaskIsNullAndUserId(mockWebUser.getIdWebUser())).thenReturn(Optional.of(statusTask));
        when(statusTaskDAO.create(any(StatusTask.class))).thenReturn(statusTask);

        doNothing().when(taskParticipantsDAO).create(any(TaskParticipants.class));

        TaskDtoResponse response = Mockito.mock(TaskDtoResponse.class);
        when(taskFactory.makeTaskDto(any(Task.class))).thenReturn(response);


        taskService.createTask(createTaskDtoRequest).get();

    }


    @Test
    void createTask_CreateTaskWithoutProject_ReturnCreatedTask() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        CreateTaskDtoRequest createTaskDtoRequest = Mockito.mock(CreateTaskDtoRequest.class);

        WebUser mockWebUser = Mockito.mock(WebUser.class);
        when(mockWebUser.getIdWebUser()).thenReturn(1L);


        Task mockTask = Mockito.mock(Task.class);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(webUserDAO.findById(1L)).thenReturn(mockWebUser);
        when(statusDAO.findByDefaultName()).thenReturn(1L);
        when(taskDAO.create(any(Task.class))).thenReturn(mockTask);

        StatusTask statusTask = Mockito.mock(StatusTask.class);

        when(statusTaskDAO.findStatusTaskByRightStatusTaskIsNullAndUserId(mockWebUser.getIdWebUser())).thenReturn(Optional.of(statusTask));
        when(statusTaskDAO.create(any(StatusTask.class))).thenReturn(statusTask);

        doNothing().when(taskParticipantsDAO).create(any(TaskParticipants.class));

        TaskDtoResponse response = Mockito.mock(TaskDtoResponse.class);
        when(taskFactory.makeTaskDto(any(Task.class))).thenReturn(response);


        taskService.createTask(createTaskDtoRequest).get();

    }

    @Test
    void updateTask_UpdateTaskWithReplacementOldOfTheNewTag_ReturnUpdatedTask() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        UpdateTaskDtoRequest updateTaskDtoRequest = new UpdateTaskDtoRequest();
        updateTaskDtoRequest.setDescriptionTask("newDescription");
        updateTaskDtoRequest.setIdStatus(2L);
        updateTaskDtoRequest.setIdNewTag(2L);
        updateTaskDtoRequest.setIdOldTag(1L);


        Status status = new Status();
        status.setIdStatus(1L);
        StatusTask statusTask = new StatusTask();
        statusTask.setStatus(status);

        Tag oldTag = new Tag();
        oldTag.setIdTag(1L);
        Tag spyOldTag = spy(oldTag);


        Tag newTag = new Tag();
        newTag.setIdTag(2L);
        Tag spyNewTag = spy(newTag);


        Task task = new Task();
        task.setIdTask(1L);
        task.setDescriptionTask("old");
        task.setTagSet(new HashSet<>(Collections.singleton(spyOldTag)));
        task.setStatusTasks(new HashSet<>(List.of(statusTask)));

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.findById(task.getIdTask())).thenReturn(task);
        when(tagDAO.findById(1L)).thenReturn(spyOldTag);
        when(tagDAO.findById(2L)).thenReturn(spyNewTag);
        when(statusDAO.findById(2L)).thenReturn(getNewStatus());
        when(statusTaskDAO.findByTaskId(task.getIdTask())).thenReturn(statusTask);
        when(taskDAO.update(task)).thenReturn(task);

        TaskDtoResponse response = Mockito.mock(TaskDtoResponse.class);
        when(taskFactory.makeTaskDto(any(Task.class))).thenReturn(response);

        taskService.updateTask(1L, updateTaskDtoRequest);

        verify(taskDAO).update(task);
        verify(statusTaskDAO).update(any(StatusTask.class));

        assertEquals(updateTaskDtoRequest.getDescriptionTask() , task.getDescriptionTask());
        assertEquals(updateTaskDtoRequest.getIdStatus(), task.getStatusTasks().iterator().next().getStatus().getIdStatus());

        assertFalse(task.getTagSet().contains(spyOldTag));
        assertTrue(task.getTagSet().contains(spyNewTag));
    }

    @Test
    void updateTask_UpdateTaskAddNewTag_ReturnUpdatedTask() throws SQLException, ExecutionException, InterruptedException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        UpdateTaskDtoRequest updateTaskDtoRequest = new UpdateTaskDtoRequest();
        updateTaskDtoRequest.setDescriptionTask("newDescription");
        updateTaskDtoRequest.setIdStatus(2L);
        updateTaskDtoRequest.setIdNewTag(2L);


        Status status = new Status();
        status.setIdStatus(1L);
        StatusTask statusTask = new StatusTask();
        statusTask.setStatus(status);

        Tag newTag = new Tag();
        newTag.setIdTag(2L);
        Tag spyNewTag = spy(newTag);


        Task task = new Task();
        task.setIdTask(1L);
        task.setDescriptionTask("old");
        task.setStatusTasks(new HashSet<>(List.of(statusTask)));
        task.setTagSet(new HashSet<>());

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(taskDAO.findById(task.getIdTask())).thenReturn(task);
        when(tagDAO.findById(2L)).thenReturn(spyNewTag);
        when(statusDAO.findById(2L)).thenReturn(getNewStatus());
        when(statusTaskDAO.findByTaskId(task.getIdTask())).thenReturn(statusTask);
        when(taskDAO.update(task)).thenReturn(task);

        TaskDtoResponse response = Mockito.mock(TaskDtoResponse.class);
        when(taskFactory.makeTaskDto(any(Task.class))).thenReturn(response);

        taskService.updateTask(1L, updateTaskDtoRequest);

        verify(taskDAO).update(task);
        verify(statusTaskDAO).update(any(StatusTask.class));

        assertEquals(updateTaskDtoRequest.getDescriptionTask() , task.getDescriptionTask());
        assertEquals(updateTaskDtoRequest.getIdStatus(), task.getStatusTasks().iterator().next().getStatus().getIdStatus());

    }

    @Test
    void changeProjectPosition_MoveTaskToAnotherProject() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Project project = Mockito.mock(Project.class);
        Task task = Mockito.mock(Task.class);

        when(taskDAO.findById(1L)).thenReturn(task);
        when(projectDAO.findById(1L)).thenReturn(project);
        when(taskDAO.update(task)).thenReturn(task);

        taskService.changeProjectPosition(1L, 1L);

        verify(taskDAO).findById(1L);
        verify(projectDAO).findById(1L);


    }

    @Test
    void removeProject_DeleteProject_SuccessfulDelete () throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Task task = Mockito.mock(Task.class);
        StatusTask statusTask = Mockito.mock(StatusTask.class);
        StatusTask leftStatusTask = Mockito.mock(StatusTask.class);
        StatusTask rightStatusTask = Mockito.mock(StatusTask.class);

        when(taskDAO.findById(1L)).thenReturn(task);
        when(statusTaskDAO.findByTaskId(1L)).thenReturn(statusTask);
        when(statusTask.getIdStatusTask()).thenReturn(1L);
        when(statusTask.getIdLeftTaskStatus()).thenReturn(leftStatusTask);
        when(statusTask.getIdRightTaskStatus()).thenReturn(rightStatusTask);

        when(statusTaskDAO.update(leftStatusTask)).thenReturn(leftStatusTask);
        when(statusTaskDAO.update(rightStatusTask)).thenReturn(rightStatusTask);

        doNothing().when(statusTaskDAO).delete(1L);
        doNothing().when(taskParticipantsDAO).deleteByTask(1L);
        doNothing().when(taskDAO).deleteById(1L);

        taskService.removeTaskById(1L);

        verify(taskDAO).deleteById(1L);
        verify(statusTaskDAO).delete(1L);
        verify(statusTaskDAO).update(leftStatusTask);
        verify(statusTaskDAO).update(rightStatusTask);
    }

    @Test
    void removeProject_DeleteProjectAndStatusProjectRightIdNull() throws SQLException {

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Task task = Mockito.mock(Task.class);
        StatusTask statusTask = Mockito.mock(StatusTask.class);
        StatusTask leftStatusTask = Mockito.mock(StatusTask.class);
        StatusTask rightStatusTask = Mockito.mock(StatusTask.class);

        when(taskDAO.findById(1L)).thenReturn(task);
        when(statusTaskDAO.findByTaskId(1L)).thenReturn(statusTask);
        when(statusTask.getIdStatusTask()).thenReturn(1L);
        when(statusTask.getIdLeftTaskStatus()).thenReturn(leftStatusTask);
        when(statusTask.getIdRightTaskStatus()).thenReturn(null);

        when(statusTaskDAO.update(leftStatusTask)).thenReturn(leftStatusTask);

        doNothing().when(statusTaskDAO).delete(1L);
        doNothing().when(taskParticipantsDAO).deleteByTask(1L);
        doNothing().when(taskDAO).deleteById(1L);

        taskService.removeTaskById(1L);

        verify(taskDAO).deleteById(1L);
        verify(statusTaskDAO).delete(1L);
        verify(statusTaskDAO).update(leftStatusTask);
        verify(statusTaskDAO, never()).update(rightStatusTask);
    }

    @Test
    void removeTaskFromProject_DeleteProjectInTask() throws SQLException {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        Task task = Mockito.mock(Task.class);

        when(taskDAO.findById(1L)).thenReturn(task);
        when(taskDAO.update(task)).thenReturn(task);

        taskService.removeTaskFromProject(1L);

        verify(taskDAO).findById(1L);

    }


    private Task getTask() {
        return Task.builder()
                .idTask(1L)
                .statusTasks(Collections.singleton(getStatusTask()))
                .descriptionTask("description")
                .nameTask("test")
                .build();
    }

    private Status getNewStatus() {
        return Status.builder()
                .idStatus(2L)
                .nameStatus("newTest")
                .build();
    }

    private StatusTask getStatusTask() {
        return StatusTask.builder()
                .idStatusTask(1L)
                .idLeftTaskStatus(null)
                .idRightTaskStatus(null)
                .status(getStatus())
                .build();
    }
    private Status getStatus() {
        return Status.builder()
                .idStatus(1L)
                .nameStatus("test")
                .build();
    }
}
