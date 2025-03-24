package com.quick_task.controller;

import com.quick_task.dto.create.CreateTaskDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionTask;
import com.quick_task.dto.update.UpdateTaskDtoRequest;
import com.quick_task.service.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@Tag(name = "Task")
public class TaskController {
    private final TaskServiceImpl taskService;

    public static final String All_TASK_USER = "/tasks-user";
    public static final String All_TASK_PROJECTS = "/tasks";
    public static final String All_TASKS_WITHOUT_PROJECT = "/all-tasks-without-project";
    public static final String INFORMATION_TASK = "/information-task/{id_task}";
    public static final String All_TASK_BOARD = "/tasks-board";
    public static final String TASK_PROJECT = "/tasks/{id_project}";
    public static final String CHANGE_TASK_BOARD = "/tasks-board/update-task";
    public static final String CREATE_TASK = "/task";
    public static final String CHANGE_PROJECT = "/project/{id_project}/task/{id_task}";
    public static final String UPDATE_OR_DELETE_TASK = "/task/{id_task}";
    public static final String REMOVE_TASK_PROJECT = "/project/task/{id_task}";



    public TaskController(TaskServiceImpl service) {
        this.taskService = service;
    }

    @GetMapping(All_TASK_USER)
    public  CompletableFuture<List<TaskDtoResponse>> allTasks( @RequestParam(value = "name_status", required = false) String nameStatus)  {
        return  taskService.findAllTaskUser( nameStatus);
    }
    @GetMapping(INFORMATION_TASK)
    public CompletableFuture<FullInformationTaskDtoResponse>  informationTask(@PathVariable(value = "id_task") Long idTask) throws SQLException {
        return  taskService.getFullInformationTask(idTask);
    }
    @GetMapping(All_TASKS_WITHOUT_PROJECT)
    public CompletableFuture<List<TaskDtoResponse>> allTasksWithoutProject( @RequestParam(value = "name_status", required = false) String nameStatus)  {
        return  taskService.getAllTaskWithoutProject( nameStatus);
    }
    @Operation(
            summary = "Displays all tasks from the board",
            description = "Displays all tasks from the board along with their status."
    )
    @GetMapping(All_TASK_BOARD)
    public CompletableFuture<List<StatusTaskDtoResponse>> allTaskBoard()  {
        return  taskService.getAllTaskByStatus();
    }

@Operation(
        summary = "Sorts by issue status",
        description = "It sorts by increasing or decreasing depending on the status."
)
    @GetMapping(TASK_PROJECT)
    public CompletableFuture<AllTaskByProjectDtoResponse> allTaskProject(@PathVariable(value = "id_project") Long idProject, @RequestParam(value = "enable", required = false) Boolean enable)  {
        return  taskService.getTasksByProject(idProject, enable);
    }
    @Operation(
            summary = "All tasks for all projects",
            description = "Displays all tasks according to their user projects."
    )
    @GetMapping(All_TASK_PROJECTS)
    public CompletableFuture<List<AllTaskByProjectDtoResponse>> allTaskProject()  {
        return  taskService.getAllTasksByProject();
    }
    @Operation(
            summary = "Changes tasks on the board",
            description = "It changes the tasks on the board depending on the location, and it can also change the status."
    )
    @PatchMapping(CHANGE_TASK_BOARD)
    public CompletableFuture<List<StatusTaskDtoResponse>>  changeTask( @RequestBody UpdateChangePositionTask dtoRequest) throws SQLException {
        return  taskService.changeStatusPosition( dtoRequest);
    }
    @PostMapping(CREATE_TASK)
    public ResponseEntity<CompletableFuture<TaskDtoResponse>> createTask(@Valid @RequestBody CreateTaskDtoRequest dtoRequest)  {
        return new ResponseEntity<>(taskService.createTask(dtoRequest), HttpStatus.CREATED);
    }
    @PatchMapping(CHANGE_PROJECT)
    public CompletableFuture<Void> changeProject(@PathVariable(value = "id_task") Long idTask, @PathVariable(value = "id_project") Long idProject) throws SQLException {
         return taskService.changeProjectPosition(idTask, idProject);
    }
    @PatchMapping(UPDATE_OR_DELETE_TASK)
    public CompletableFuture<TaskDtoResponse> updateTask(@PathVariable(value = "id_task") Long idTask, @Valid  @RequestBody UpdateTaskDtoRequest dtoRequest)  {
        return  taskService.updateTask(idTask, dtoRequest);
    }
    @DeleteMapping(REMOVE_TASK_PROJECT)
    public CompletableFuture<Void> removeTaskFromProject(@PathVariable(value = "id_task") Long idTask) throws SQLException {
       return taskService.removeTaskFromProject(idTask);
    }
    @DeleteMapping(UPDATE_OR_DELETE_TASK)
    public CompletableFuture<Void>  deleteTask(@PathVariable(value = "id_task") Long idTask) throws SQLException {
         return taskService.removeTaskById(idTask);
    }
}
