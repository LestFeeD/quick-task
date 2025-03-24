package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionProject;
import com.quick_task.dto.update.UpdateProjectDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.exception.EntityNotFoundException;
import com.quick_task.factory.DaoFactory;
import com.quick_task.factory.ProjectFactory;
import com.quick_task.utils.DBService;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ProjectServiceImpl implements ProjectService {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final AuthenticationService authenticationService;

    private final ProjectFactory projectFactory;
    private final Executor taskExecutor;



    @Autowired
    public ProjectServiceImpl(AuthenticationService authenticationService, ProjectFactory projectFactory, Executor taskExecutor) {
        this.authenticationService = authenticationService;
        this.projectFactory = projectFactory;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public CompletableFuture<List<ProjectDtoResponse>> findAllProject() {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
            try {
                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                assert projectDAO != null;
                Long userId = authenticationService.getCurrentUserId();
                List<Project> projects =  projectDAO.getAllByIdUser(userId);
                transaction.commit();
                List<ProjectDtoResponse> projectDtoResponses = new ArrayList<>();

                for(Project project: projects) {
                    ProjectDtoResponse dtoResponse = projectFactory.makeProjectDto(project);
                    projectDtoResponses.add(dtoResponse);
                }
                return projectDtoResponses;
            } catch (SQLException e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }


    @Async
    @Override
    public CompletableFuture<List<ProjectDtoResponse>> findAllProjectByStatus(Long idStatus, Boolean enable) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            Status status = statusDAO.findById(idStatus);
            Long userId = authenticationService.getCurrentUserId();
            List<Project> projects;
            if(enable == null) {
                projects =  projectDAO.findProjectGroupedByStatus(userId, status.getNameStatus());
            } else if (enable) {
                projects =  projectDAO.findProjectGroupedByStatus(userId, status.getNameStatus());
                Collections.reverse(projects);
            } else {
                projects = projectDAO.findProjectGroupedByStatus(userId, status.getNameStatus());
            }

            transaction.commit();

            List<ProjectDtoResponse> projectDtoResponses = new ArrayList<>();
            for (Project project : projects) {
                ProjectDtoResponse dtoResponse = projectFactory.makeProjectDto(project);
                projectDtoResponses.add(dtoResponse);
            }
            return projectDtoResponses;

        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }, taskExecutor);


}

    @Override
    public String getProjectName(Long idProject) throws SQLException {

            Transaction transaction = DBService.getTransaction();
        try {
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            Project project = projectDAO.findById(idProject);
            transaction.commit();
            return project.getNameProject();
    } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
    }

    }

    @Async
    @Override
    public CompletableFuture<List<ProjectDtoResponse>> getByName(String parameter) {
            return CompletableFuture.supplyAsync(() -> {

                Transaction transaction = DBService.getTransaction();
        try{
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            List<Project> projects = projectDAO.getByName(parameter);
            transaction.commit();
                List<ProjectDtoResponse> projectDtoResponses = new ArrayList<>();
            if(projects != null) {
                for (Project project : projects) {
                    ProjectDtoResponse dtoResponse = projectFactory.makeProjectDto(project);
                    logger.info("Get a project with ID: {}", dtoResponse.getIdProject());
                    projectDtoResponses.add(dtoResponse);
                }
            }
            return projectDtoResponses;

        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Override
    @Async
    public CompletableFuture<ProjectDtoResponse> createProject(CreateProjectDtoRequest createProjectDtoRequest) {


        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
            try {

                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                assert projectDAO != null;
                PriorityDAO priorityDAO = DaoFactory.getDao(PriorityDAO.class);
                assert priorityDAO != null;
                ProjectParticipantsDAO projectParticipantsDAO = DaoFactory.getDao(ProjectParticipantsDAO.class);
                assert projectParticipantsDAO != null;
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;
                Long userId = authenticationService.getCurrentUserId();

                StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
                assert statusDAO != null;
                StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
                assert statusProjectDAO != null;

                Long statusId = statusDAO.findByDefaultName();
                Status status = statusDAO.findById(statusId);
                logger.info("Found status: {}", status.getNameStatus());
                Priority priority = priorityDAO.findById(createProjectDtoRequest.getIdPriority());
                logger.info("Found priority: {}", priority.getNamePriority());

                WebUser webUser = webUserDAO.findById(userId);
                logger.info("Found user: {}", webUser.getNameUser());

                Optional<StatusProject> optionalStatusProject = statusProjectDAO.findStatusProjectByRightStatusTaskIsNullAndUserId(webUser.getIdWebUser());

                Project project = Project.builder()
                        .nameProject(createProjectDtoRequest.getNameProject())
                        .startDate(createProjectDtoRequest.getStartDate())
                        .endDate(createProjectDtoRequest.getEndDate())
                        .descriptionProject(createProjectDtoRequest.getDescriptionProject())
                        .priority(priority)
                        .build();
                projectDAO.create(project);
                logger.info("Project created: {}", project.getIdProject());

                ProjectParticipants projectParticipants = ProjectParticipants.builder()
                        .webUser(webUser)
                        .project(project)
                        .ownerProject(1)
                        .build();
                IdProjectParticipants idProjectParticipants = new IdProjectParticipants();
                idProjectParticipants.setIdProject(project.getIdProject());
                idProjectParticipants.setIdWebUser(webUser.getIdWebUser());
                projectParticipants.setIdProjectParticipants(idProjectParticipants);

                projectParticipantsDAO.create(projectParticipants);

                StatusProject statusProject = StatusProject.builder()
                        .status(status)
                        .project(project)
                        .build();

                statusProjectDAO.create(statusProject);


                if (optionalStatusProject.isEmpty()) {
                    // This is the first task status in the project, creating a bundle with null
                    statusProject.setIdLeftProjectStatus(null);
                    statusProject.setIdRightProjectStatus(null);

                } else {
                    logger.info("Found leftStatus: {}", optionalStatusProject.get().getIdLeftProjectStatus());

                    // If the previous status is found, we associate the new status with it.
                    StatusProject previousStatusProject = optionalStatusProject.get();
                    statusProject.setIdLeftProjectStatus(previousStatusProject);
                    previousStatusProject.setIdRightProjectStatus(statusProject);

                    try {
                        statusProjectDAO.update(previousStatusProject);
                        statusProjectDAO.update(statusProject);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                transaction.commit();

                ProjectDtoResponse response = projectFactory.makeProjectDto(project);
                response.setNamePriority(project.getPriority().getNamePriority());

                return response;

            } catch (SQLException e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }

    @Override
    @Async
    public CompletableFuture<ProjectDtoResponse> updateProject(Long idProject,  UpdateProjectDtoRequest dtoRequest) {
        return CompletableFuture.supplyAsync(() -> {
                    Transaction transaction = DBService.getTransaction();
        try{
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
            assert statusProjectDAO != null;
            PriorityDAO priorityDAO = DaoFactory.getDao(PriorityDAO.class);
            assert priorityDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            Long userId = authenticationService.getCurrentUserId();
            WebUser webUser = webUserDAO.findById(userId);
            Project project = projectDAO.findById(idProject);
            if (project == null) {
                throw new com.quick_task.exception.EntityNotFoundException(String.format("Project with ID \"%s\" not found.", idProject));
            }
            if (dtoRequest.getNameProject() != null) {
                project.setNameProject(dtoRequest.getNameProject());
            }
            if (dtoRequest.getStartDate() != null) {
                project.setStartDate(dtoRequest.getStartDate());
            }
            if (dtoRequest.getEndDate() != null) {
                project.setEndDate(dtoRequest.getEndDate());
            }
            if (dtoRequest.getDescriptionProject() != null) {

                project.setDescriptionProject(dtoRequest.getDescriptionProject());
            }
            if (dtoRequest.getIdStatus() != null) {
                Status status = statusDAO.findById(dtoRequest.getIdStatus());
                logger.info("found status: ID = {}, name = {}", status.getIdStatus(), status.getNameStatus());
                StatusProject statusProject = statusProjectDAO.findByProjectId(idProject);
                statusProject.setStatus(status);
                statusProjectDAO.update(statusProject);
            }
            if (dtoRequest.getIdPriority() != null) {
                Priority newPriority = priorityDAO.findById(dtoRequest.getIdPriority());
                logger.info("found priority: ID = {}, name = {}", newPriority.getIdPriority(), newPriority.getNamePriority());
                project.setPriority(newPriority);
            }

            projectDAO.update(project);
            transaction.commit();
            return projectFactory.makeProjectDto(project);
        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }

    @Override
    @Async
    public CompletableFuture<InformationProjectDtoResponse> getInformationProject(Long idProject) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = DBService.getTransaction();
            try {
                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
                assert statusProjectDAO != null;
                assert projectDAO != null;
                PriorityDAO priorityDAO = DaoFactory.getDao(PriorityDAO.class);
                assert priorityDAO != null;
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;
                Long userId = authenticationService.getCurrentUserId();

                Project project = projectDAO.findById(idProject);
                if(project == null) {
                    throw new EntityNotFoundException("Not found the Project");
                }
                StatusProject statusProject = statusProjectDAO.findByProjectId(idProject);
                transaction.commit();
                return projectFactory.makeInformationProjectDto(project, statusProject);
            } catch (SQLException e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }

    @Override
    @Async
    public CompletableFuture<List<StatusProjectBoardDtoResponse>> getAllProjectBoardByStatus() {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
            try {
                StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
                assert statusProjectDAO != null;
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;
                Long userId = authenticationService.getCurrentUserId();

                List<Long> idStatuses = statusProjectDAO.findByAllStatusUser(userId);
                List<StatusProjectBoardDtoResponse> statusProjectDtoResponses = new ArrayList<>();


                for (Long idStatus : idStatuses) {
                    assert projectDAO != null;
                    Status status = statusDAO.findById(idStatus);
                    List<Project> projectsByStatus = projectDAO.findAllProjectsByStatus(userId, status.getNameStatus());
                    if (projectsByStatus == null) {
                        projectsByStatus = new ArrayList<>();
                    }

                    List<AllProjectDtoResponse> projectsDTOs = projectsByStatus.stream()
                            .map(project -> projectFactory.makeAllProjectDto(project, status.getNameStatus()))
                            .toList();

                    StatusProjectBoardDtoResponse statusProjectDtoResponse = new StatusProjectBoardDtoResponse(
                            status.getIdStatus(),
                            status.getNameStatus(),
                            projectsDTOs
                    );
                    statusProjectDtoResponses.add(statusProjectDtoResponse);
                }

                transaction.commit();
                return statusProjectDtoResponses;
            } catch (SQLException e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        },taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<List<StatusAllProjectDtoResponse>> getAllProjectByStatus() {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            StatusDAO statusDao = DaoFactory.getDao(StatusDAO.class);
            StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
            assert statusProjectDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            Long userId = authenticationService.getCurrentUserId();

            List<Long> idStatuses = statusProjectDAO.findByAllStatusUser(userId);
            List<StatusAllProjectDtoResponse> statusProjectDtoResponses  = new ArrayList<>();

            for (Long idStatus : idStatuses) {
                Status status = statusDao.findById(idStatus);
                logger.info("Project method getAllProjectByStatus, nameStatus: {}", status.getNameStatus());
                List<Project> projectsByStatus = projectDAO.findProjectGroupedByStatus(userId, status.getNameStatus());
                logger.info("Projects for status {}: {}", status.getNameStatus(), projectsByStatus);

                if(!projectsByStatus.isEmpty()) {

                    List<ProjectDtoResponse> projectsDTOs = projectsByStatus.stream()
                            .map(projectFactory::makeProjectDto)
                            .toList();

                    StatusAllProjectDtoResponse statusProjectDtoResponse = new StatusAllProjectDtoResponse(
                            idStatus,
                            status.getNameStatus(),
                            projectsDTOs
                    );
                    statusProjectDtoResponses.add(statusProjectDtoResponse);
                }
            }

            transaction.commit();
            return statusProjectDtoResponses;
        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<List<StatusProjectBoardDtoResponse>> changeStatusPosition( UpdateChangePositionProject dtoRequest) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();

        try {
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
            assert statusProjectDAO != null;
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            Long userId = authenticationService.getCurrentUserId();
            StatusProject statusProject = statusProjectDAO.findByProjectId(dtoRequest.getProjectId());
            List<Long> idStatuses = statusProjectDAO.findByAllStatusUser(userId);
            List<StatusProjectBoardDtoResponse> statusProjectDtoResponses = new ArrayList<>();
            Status status = new Status();
            if (dtoRequest.getNewStatusId() != null) {
                status = statusDAO.findById(dtoRequest.getNewStatusId());
                status.setIdStatus(dtoRequest.getNewStatusId());
            }
            Optional<StatusProject> optionalNewLeftProject = Optional.ofNullable(
                    dtoRequest.getLeftProjectId() != null ? statusProjectDAO.findById(dtoRequest.getLeftProjectId()) : null);
            Optional<StatusProject> optionalNewRightProject = Optional.ofNullable(
                    dtoRequest.getRightProjectId() != null ? statusProjectDAO.findById(dtoRequest.getRightProjectId()) : null);

            if (dtoRequest.getNewStatusId() != null) {
                statusProject.setStatus(statusDAO.findById(dtoRequest.getNewStatusId()));
            }

            // Replacing the old issue position
            replaceOldTaskPosition(statusProject);
            // Setting a new left task
            if (optionalNewLeftProject.isPresent()) {
                logger.info("leftStatus ID: {}", optionalNewLeftProject.get().getIdStatusProject());
                StatusProject newLeftProject = optionalNewLeftProject.get();
                newLeftProject.setIdRightProjectStatus(statusProject);
                statusProject.setIdLeftProjectStatus(newLeftProject);

            } else {
                statusProject.setIdLeftProjectStatus(null);

            }
            // Setting a new right-hand task
            if (optionalNewRightProject.isPresent()) {
                logger.info("rightStatus ID: {}", optionalNewRightProject.get().getIdStatusProject());

                StatusProject newRightRight = optionalNewRightProject.get();
                newRightRight.setIdLeftProjectStatus(statusProject);
                statusProject.setIdRightProjectStatus(newRightRight);

            } else {
                statusProject.setIdRightProjectStatus(null);

            }
            statusProjectDAO.update(statusProject);

            if (optionalNewLeftProject.isPresent()) {
                statusProjectDAO.update(optionalNewLeftProject.get());
            }

            if (optionalNewRightProject.isPresent()) {
                statusProjectDAO.update(optionalNewRightProject.get());
            }

            transaction.commit();

            if (status.getIdStatus().equals(statusDAO.findIdByProjectId(dtoRequest.getProjectId()))) {

                for (Long idStatus : idStatuses) {
                    Status statusUseUser = statusDAO.findById(idStatus);
                    assert projectDAO != null;
                    logger.info("Project method changeStatusPosition, nameStatus: {}", statusUseUser.getNameStatus());

                    List<Project> projectsByStatus = projectDAO.findAllProjectsByStatus(userId, statusUseUser.getNameStatus());

                    List<AllProjectDtoResponse> projectsDtos = projectsByStatus.stream()
                            .map(project -> projectFactory.makeAllProjectDto(project, statusUseUser.getNameStatus()))
                            .toList();

                    StatusProjectBoardDtoResponse statusProjectDtoResponse = new StatusProjectBoardDtoResponse(
                            status.getIdStatus(),
                            statusUseUser.getNameStatus(),
                            projectsDtos
                    );
                    statusProjectDtoResponses.add(statusProjectDtoResponse);
                }
            }

            return statusProjectDtoResponses;

        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }, taskExecutor);
    }

    public void deletePriority(Long idProject) {
        Transaction transaction = DBService.getTransaction();
        try {
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            Project project = projectDAO.findById(idProject);
            project.setPriority(null);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public  CompletableFuture<Void> removeProjectById(Long idProduct) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
    try {
        ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
        assert projectDAO != null;
        StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
        assert statusProjectDAO != null;
        TaskDAO taskDAO = DaoFactory.getDao(TaskDAO.class);
        Project project = projectDAO.findById(idProduct);

        assert taskDAO != null;
        List<Task> allTaskByProjectId = taskDAO.getByProject(idProduct);
        if(!allTaskByProjectId.isEmpty()) {
            for (Task task : allTaskByProjectId) {
                task.setProject(null);
            }
        }
        if (project == null) {
            throw new com.quick_task.exception.EntityNotFoundException(String.format("Project with ID \"%s\" not found.", idProduct));
        }
        StatusProject statusProject = statusProjectDAO.findByProjectId(idProduct);
        logger.info("StatusProject id: {}", statusProject.getIdStatusProject());
        ProjectParticipantsDAO projectParticipantsDAO = DaoFactory.getDao(ProjectParticipantsDAO.class);
        assert projectParticipantsDAO != null;

        if (statusProject != null) {
            StatusProject leftStatus = statusProject.getIdLeftProjectStatus();

            StatusProject rightStatus = statusProject.getIdRightProjectStatus();

            if (leftStatus != null) {
                logger.info("leftStatus id: {}", leftStatus.getIdStatusProject());

                if(rightStatus != null) {
                    leftStatus.setIdRightProjectStatus(rightStatus);
                } else {
                    leftStatus.setIdRightProjectStatus(null);
                }
                statusProjectDAO.update(leftStatus);
            }

            if (rightStatus != null) {
                logger.info("rightStatus id: {}", rightStatus.getIdStatusProject());

                if(leftStatus != null) {
                    rightStatus.setIdLeftProjectStatus(leftStatus);

                } else {
                rightStatus.setIdLeftProjectStatus(null);
                }
                statusProjectDAO.update(rightStatus);
            }

            statusProjectDAO.delete(statusProject.getIdStatusProject());
        }

        projectParticipantsDAO.deleteByProject(idProduct);

        projectDAO.removeById(idProduct);
        transaction.commit();
    } catch (SQLException e) {
        transaction.rollback();
        throw new RuntimeException(e);
    }
        }, taskExecutor);

    }

    @Async
    public void replaceOldTaskPosition(StatusProject changeProject) {


             StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
             assert statusProjectDAO != null;


             Optional<StatusProject> optionalOldLeftProject = Optional.ofNullable(changeProject.getIdLeftProjectStatus());
             Optional<StatusProject> optionalOldRightProject = Optional.ofNullable(changeProject.getIdRightProjectStatus());

             // Обновляем старую левую задачу
             optionalOldLeftProject.ifPresent(oldLeftTask -> {
                 oldLeftTask.setIdRightProjectStatus(optionalOldRightProject.orElse(null));
                 try {
                     statusProjectDAO.update(oldLeftTask);
                 } catch (SQLException e) {
                     throw new RuntimeException(e);
                 }
             });

             // Обновляем старую правую задачу
             optionalOldRightProject.ifPresent(oldRightTask -> {
                 oldRightTask.setIdLeftProjectStatus(optionalOldLeftProject.orElse(null));
                 try {
                     statusProjectDAO.update(oldRightTask);
                 } catch (SQLException e) {

                     throw new RuntimeException(e);
                 }
             });

        }

    }

