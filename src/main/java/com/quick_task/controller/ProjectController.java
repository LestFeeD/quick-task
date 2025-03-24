package com.quick_task.controller;

import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionProject;
import com.quick_task.dto.update.UpdateProjectDtoRequest;
import com.quick_task.service.ProjectServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Transactional
@RestController
@Tag(name = "Project")
public class ProjectController {
    @Autowired
    private final ProjectServiceImpl projectService;
    Logger logger = org.slf4j.LoggerFactory.getLogger(ProjectController .class);

    public static final String All_PROJECTS  = "/all-projects";
    public static final String PROJECTS  = "/projects/{id_status}";
    public static final String ALL_PROJECTS_STATUS = "/projects";
    public static final String FETCH_PROJECTS = "/fetch-projects";
    public static final String INFORMATION_PROJECT = "/information-project/{id_project}";
    public static final String All_PROJECTS_BOARD = "/projects-board";
    public static final String CREATE_PROJECT = "/projects";
    public static final String UPDATE_OR_DELETE_PROJECT = "/projects/{id_project}";
    public static final String CHANGE_TASK_BOARD = "/projects-board";

    public ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    @GetMapping(All_PROJECTS)
    public CompletableFuture<List<ProjectDtoResponse>> allProject()   {
        return  projectService.findAllProject();
    }
    @Operation(
            summary = "Outputs all projects",
            description = "Displays all projects, if you click on the status, the list reverses."
    )
    @GetMapping(PROJECTS)
    public CompletableFuture<List<ProjectDtoResponse>> projectsByStatus(@PathVariable(value = "id_status") Long idStatus, @RequestParam(value = "enable", required = false) Boolean enable)  {
        return  projectService.findAllProjectByStatus(idStatus, enable);
    }

    @GetMapping(FETCH_PROJECTS)
    public CompletableFuture<List<ProjectDtoResponse>> getByNameProjects(@RequestParam(value = "nameProject") String nameProject)  {
        return  projectService.getByName(nameProject);
    }
    @GetMapping(INFORMATION_PROJECT)
    public CompletableFuture<InformationProjectDtoResponse> informationProject(@PathVariable(value = "id_project") Long idProject) throws SQLException {
        return  projectService.getInformationProject(idProject);
    }
    @Operation(
            summary = "Displays all projects from the board",
            description = "Displays all projects from the board along with their status."
    )
    @GetMapping(All_PROJECTS_BOARD)
    public CompletableFuture<List<StatusProjectBoardDtoResponse>> allProjectBoard()  {
        return  projectService.getAllProjectBoardByStatus();
    }

    @Operation(
            summary = "Changes projects on the board",
            description = "It changes the projects on the board depending on the location, and it can also change the status."
    )
    @PatchMapping(CHANGE_TASK_BOARD)
    public CompletableFuture<List<StatusProjectBoardDtoResponse>> changeTask( @RequestBody UpdateChangePositionProject dtoRequest) throws SQLException {
        return  projectService.changeStatusPosition( dtoRequest);
    }

    @Operation(
            summary = "Displays a list of projects by their status.",
            description = "Displays a list of projects by their status on the main page."
    )
    @GetMapping(ALL_PROJECTS_STATUS)
    public CompletableFuture<List<StatusAllProjectDtoResponse>> allProjectUser()  {
        return  projectService.getAllProjectByStatus();
    }

    @PostMapping(CREATE_PROJECT)
    public ResponseEntity<CompletableFuture<ProjectDtoResponse>> createProject(@Valid  @RequestBody CreateProjectDtoRequest createProjectDtoRequest) {
        return new ResponseEntity<>(projectService.createProject(createProjectDtoRequest), HttpStatus.CREATED);

    }
    @PatchMapping(UPDATE_OR_DELETE_PROJECT)
    public CompletableFuture<ProjectDtoResponse> allProjectsUser(@PathVariable(value = "id_project") Long id,  @Valid  @RequestBody UpdateProjectDtoRequest dtoRequest)  {
        return  projectService.updateProject(id, dtoRequest);
    }
    @DeleteMapping(UPDATE_OR_DELETE_PROJECT)
    public CompletableFuture<Void> deleteProject(@PathVariable(value = "id_project") Long id) throws SQLException {
        return projectService.removeProjectById(id);
    }
}
