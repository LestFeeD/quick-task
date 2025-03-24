package com.quick_task.service;

import com.quick_task.dao.*;
import com.quick_task.dto.create.CreateTaskDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateChangePositionTask;
import com.quick_task.dto.update.UpdateTaskDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.exception.EntityNotFoundException;
import com.quick_task.factory.DaoFactory;
import com.quick_task.factory.TaskFactory;
import com.quick_task.utils.DBService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class TaskServiceImpl implements TaskService
{
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private  final TaskFactory taskFactory;
    private final AuthenticationService authenticationService;
    private final Executor taskExecutor;

    @Autowired
    public TaskServiceImpl(TaskFactory taskFactory, AuthenticationService authenticationService, Executor taskExecutor) {
        this.taskFactory = taskFactory;
        this.authenticationService = authenticationService;
        this.taskExecutor = taskExecutor;
    }

    @Async
    @Override
    public CompletableFuture<List<TaskDtoResponse>> findAllTaskUser( String nameStatus) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            List<Task> tasks;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            Long userId = authenticationService.getCurrentUserId();
            assert webUserDAO != null;
            if(nameStatus != null) {
                tasks =  taskDao.findSortedTasksWithFilters(userId, nameStatus, null);
            } else {
                tasks = taskDao.getAllByIdUser(userId);
            }

            transaction.commit();
            List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
            for (Task task : tasks) {
                TaskDtoResponse dtoResponse = taskFactory.makeTaskDto(task);
                taskDtoResponses.add(dtoResponse);
            }
            return taskDtoResponses;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }
    @Async
    @Override
    public CompletableFuture<AllTaskByProjectDtoResponse> getTasksByProject(Long idProject, Boolean enable) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
            try {
                TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
                assert taskDao != null;
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                Project project = projectDAO.findById(idProject);

                Long userId = authenticationService.getCurrentUserId();
                assert webUserDAO != null;
                List<Task> tasks;
                if(enable == null) {
                    tasks = taskDao.getByProject(idProject);
                } else if (enable) {
                    tasks = taskDao.getByProject(idProject);
                    Collections.reverse(tasks);

                } else
                {
                    tasks = taskDao.getByProject(idProject);

                }
                transaction.commit();
                    List<TaskDtoResponse> dtoResponse = tasks.stream()
                            .map(taskFactory::makeTaskDto)
                            .toList();

                return new AllTaskByProjectDtoResponse(idProject, project.getNameProject(), dtoResponse);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<List<AllTaskByProjectDtoResponse>> getAllTasksByProject() {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
            try {
                TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
                assert taskDao != null;
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                Long userId = authenticationService.getCurrentUserId();
                assert projectDAO != null;
                assert webUserDAO != null;
                List<AllTaskByProjectDtoResponse> allTaskProjectDtoResponseList = new ArrayList<>();
                List<Project> projects = projectDAO.getAllByIdUser(userId);
                for (Project project: projects) {
                    List<Task> tasks = taskDao.getByProject(project.getIdProject());

                    if(!tasks.isEmpty()) {
                        List<TaskDtoResponse> dtoResponse = tasks.stream()
                                .map(taskFactory::makeTaskDto)
                                .toList();
                    AllTaskByProjectDtoResponse allTask = new AllTaskByProjectDtoResponse(project.getIdProject(), project.getNameProject(), dtoResponse);
                    allTaskProjectDtoResponseList.add(allTask);
                    }
                }
                transaction.commit();
                return  allTaskProjectDtoResponseList;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<List<TaskDtoResponse>> getAllTaskWithoutProject( String nameStatus) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            List<Task> tasks;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            Long userId = authenticationService.getCurrentUserId();
            assert webUserDAO != null;
            if(nameStatus != null) {
                tasks = taskDao.findSortedTasksWithFilters(userId, nameStatus, null);
            } else {
                tasks =    taskDao.getAllTaskWithoutProjectByIdUser(userId);

            }
            transaction.commit();
            List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
            for (Task task : tasks) {
                TaskDtoResponse dtoResponse = taskFactory.makeTaskDto(task);
                taskDtoResponses.add(dtoResponse);
            }
            return taskDtoResponses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);
        }
    @Async
    @Override
    public CompletableFuture<FullInformationTaskDtoResponse> getFullInformationTask(Long idTask) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
            assert statusTaskDAO != null;
            assert taskDao != null;
            Long userId = authenticationService.getCurrentUserId();

            Task task = taskDao.findById(idTask);
            if(task == null) {
                throw new EntityNotFoundException("Not found the task");
            }
            StatusTask statusTask = statusTaskDAO.findByTaskId(idTask);

            transaction.commit();
            return taskFactory.makeInformationTaskDto(task,statusTask);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<List<StatusTaskDtoResponse>> getAllTaskByStatus() {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
            assert statusTaskDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            Long userId = authenticationService.getCurrentUserId();
            assert webUserDAO != null;
            List<String> statusNames = statusTaskDAO.findByAllStatusUser(userId);
            List<StatusTaskDtoResponse> statusTaskDtoResponses  = new ArrayList<>();

            for (String statusName : statusNames) {
                assert taskDao != null;

                List<Task> tasksByStatus = taskDao.findAllTasksByStatus(userId, statusName);

                List<TaskBoardDtoResponse> taskDtos = tasksByStatus.stream()
                        .map(taskFactory::makeTaskBoardDto)
                        .toList();
                StatusTaskDtoResponse statusTaskDtoResponse = new StatusTaskDtoResponse(
                        statusName,
                        taskDtos
                );
                statusTaskDtoResponses.add(statusTaskDtoResponse);
            }

            transaction.commit();
            return statusTaskDtoResponses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);
}


    @Async
    @Override
    public CompletableFuture<List<StatusTaskDtoResponse>> changeStatusPosition( UpdateChangePositionTask dtoRequest) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();

        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
            assert statusTaskDAO != null;
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            StatusTask changeTask = statusTaskDAO.findByTaskId(dtoRequest.getTaskId());
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            Long userId = authenticationService.getCurrentUserId();
            assert webUserDAO != null;
            List<String> statusNames = statusTaskDAO.findByAllStatusUser(userId);
            List<StatusTaskDtoResponse> statusTaskDtoResponses = new ArrayList<>();

            Optional<StatusTask> optionalNewLeftTask = Optional.ofNullable(
                    dtoRequest.getLeftTaskId() != null ? statusTaskDAO.findById(dtoRequest.getLeftTaskId()) : null);
            Optional<StatusTask> optionalNewRightTask = Optional.ofNullable(
                    dtoRequest.getRightTaskId() != null ? statusTaskDAO.findById(dtoRequest.getRightTaskId()) : null);

            if (dtoRequest.getNewStatusId() != null) {
                changeTask.setStatus(statusDAO.findById(dtoRequest.getNewStatusId()));
            }

            // Replacing the old issue position
            replaceOldTaskPosition(changeTask);


            // Setting a new left task
            if (optionalNewLeftTask.isPresent()) {
                logger.info("leftStatus ID: {}", optionalNewLeftTask.get().getIdStatusTask());
                StatusTask newLeftTask = optionalNewLeftTask.get();
                newLeftTask.setIdRightTaskStatus(changeTask);
                changeTask.setIdLeftTaskStatus(newLeftTask);

            } else {
                changeTask.setIdLeftTaskStatus(null); // The task will be the first one

            }

            // Setting a new right-hand task
            if (optionalNewRightTask.isPresent()) {
                logger.info("rightStatus ID: {}", optionalNewRightTask.get().getIdStatusTask());
                StatusTask newRightTask = optionalNewRightTask.get();
                newRightTask.setIdLeftTaskStatus(changeTask);
                changeTask.setIdRightTaskStatus(newRightTask);

            } else {
                changeTask.setIdRightTaskStatus(null);

            }

            statusTaskDAO.update(changeTask);

            if (optionalNewLeftTask.isPresent()) {
                statusTaskDAO.update(optionalNewLeftTask.get());
            }

            if (optionalNewRightTask.isPresent()) {
                statusTaskDAO.update(optionalNewRightTask.get());
            }


            if (changeTask.getStatus().getIdStatus() != null) {

                for (String nameStatus : statusNames) {
                    assert taskDao != null;
                    logger.info("Task method changeStatusPosition , nameStatus: {}", nameStatus);

                    List<Task> tasksByStatus = taskDao.findAllTasksByStatus(userId, nameStatus);

                    List<TaskBoardDtoResponse> taskDtos = tasksByStatus.stream()
                            .map(taskFactory::makeTaskBoardDto)
                            .toList();

                    StatusTaskDtoResponse statusTaskDtoResponse = new StatusTaskDtoResponse(
                            nameStatus,
                            taskDtos
                    );
                    statusTaskDtoResponses.add(statusTaskDtoResponse);
                }
            }
            transaction.commit();

            return statusTaskDtoResponses;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }
    @Async
    @Override
    public  CompletableFuture<Void> changeProjectPosition(Long idTask, Long idProject) throws SQLException {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = DBService.getTransaction();
        try{
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            Task task = taskDao.findById(idTask);
            assert projectDAO != null;
            Long userId = authenticationService.getCurrentUserId();

            Project project = projectDAO.findById(idProject);
            task.setProject(project);
            taskDao.update(task);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }, taskExecutor);

}

    @Override
    public List<TaskDtoResponse> getByName(String parameter) {
        Transaction transaction = DBService.getTransaction();
        try{
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            List<Task> tasks = taskDao.getByName(parameter);
            transaction.commit();
            List<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
            for (Task task : tasks) {
                TaskDtoResponse dtoResponse = taskFactory.makeTaskDto(task);
                taskDtoResponses.add(dtoResponse);
            }
            return taskDtoResponses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Async
    @Override
    public CompletableFuture<TaskDtoResponse> createTask(CreateTaskDtoRequest createTaskDtoRequest) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
            assert projectDAO != null;
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
            assert statusTaskDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            TaskParticipantsDAO taskParticipantsDAO = DaoFactory.getDao(TaskParticipantsDAO.class);
            assert taskParticipantsDAO != null;
            Long statusId = statusDAO.findByDefaultName();
            Status status = new Status();
            status.setIdStatus(statusId);
            Long userId = authenticationService.getCurrentUserId();
            WebUser webUser = webUserDAO.findById(userId);
            Project project = null;
            if (createTaskDtoRequest.getIdProject() != null) {
                project = projectDAO.findById(createTaskDtoRequest.getIdProject());
            }
            Optional<StatusTask> optionalStatusTask = statusTaskDAO.findStatusTaskByRightStatusTaskIsNullAndUserId(webUser.getIdWebUser());

            Task task = Task.builder()
                    .nameTask(createTaskDtoRequest.getNameTask())
                    .project(project)
                    .build();
            taskDao.create(task);
            logger.info("Task created: {}", task.getIdTask());

            TaskParticipants taskParticipants = TaskParticipants.builder()
                    .task(task)
                    .webUser(webUser)
                    .ownerTask(1)
                    .build();
            IdTaskParticipants idTaskParticipants = new IdTaskParticipants();
            idTaskParticipants.setIdTask(task.getIdTask());
            idTaskParticipants.setIdWebUser(webUser.getIdWebUser());
            taskParticipants.setIdTaskParticipants(idTaskParticipants);

            taskParticipantsDAO.create(taskParticipants);


            StatusTask statusTask = StatusTask.builder()
                    .status(status)
                    .task(task)
                    .build();

            task.setStatusTasks(new HashSet<>(Collections.singletonList(statusTask)));
            statusTaskDAO.create(statusTask);


            if (optionalStatusTask.isEmpty()) {
                statusTask.setIdLeftTaskStatus(null);
                statusTask.setIdRightTaskStatus(null);

            } else {
                StatusTask previousStatusTask = optionalStatusTask.get();
                statusTask.setIdLeftTaskStatus(previousStatusTask);
                previousStatusTask.setIdRightTaskStatus(statusTask);

                try {
                    statusTaskDAO.update(previousStatusTask);
                    statusTaskDAO.update(statusTask);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            transaction.commit();
            return taskFactory.makeTaskDto(task);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<TaskDtoResponse> updateTask(Long idTask, UpdateTaskDtoRequest dtoRequest) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
            try {
                TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
                assert taskDao != null;
                TagDAO tagDAO = DaoFactory.getDao(TagDAO.class);
                assert tagDAO != null;
                StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
                assert statusDAO != null;
                Task task = taskDao.findById(idTask);
                StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
                assert statusTaskDAO != null;

                Long userId = authenticationService.getCurrentUserId();

                if (dtoRequest.getNameTask() != null) {
                    task.setNameTask(dtoRequest.getNameTask());
                }
                    if (dtoRequest.getStartDate() != null) {
                        if (task.getEndDate() != null) {

                            if(!dtoRequest.getEndDate().before(dtoRequest.getStartDate()) || !task.getEndDate().before(dtoRequest.getStartDate())) {

                            task.setStartDate(dtoRequest.getStartDate());
                        }
                    }  else {
                        task.setStartDate(dtoRequest.getStartDate());
                    }
                }


                    if (dtoRequest.getEndDate() != null) {
                        if (task.getStartDate() != null) {
                            if (!dtoRequest.getEndDate().before(dtoRequest.getStartDate()) || !task.getEndDate().before(dtoRequest.getStartDate())) {
                                task.setEndDate(dtoRequest.getEndDate());
                            }
                            } else {
                            task.setEndDate(dtoRequest.getEndDate());
                        }
                    }

                if (dtoRequest.getDescriptionTask() != null) {
                    task.setDescriptionTask(dtoRequest.getDescriptionTask());
                }
                if (dtoRequest.getIdStatus() != null) {
                    Status status = statusDAO.findById(dtoRequest.getIdStatus());
                    logger.info("found status: ID = {}, name = {}", status.getIdStatus(), status.getNameStatus());
                    StatusTask statusTask = statusTaskDAO.findByTaskId(idTask);
                    statusTask.setStatus(status);
                    statusTaskDAO.update(statusTask);
                }
                if (dtoRequest.getIdNewTag() != null) {
                    Tag newTag = tagDAO.findById(dtoRequest.getIdNewTag());
                    logger.info("New Tag ID: {}, name: {}", newTag.getIdTag(), newTag.getNameTag());
                    if (dtoRequest.getIdOldTag() != null) {
                        Tag oldTag = tagDAO.findById(dtoRequest.getIdOldTag());
                        task.getTagSet().remove(oldTag);
                        task.getTagSet().add(newTag);
                    } else {
                        task.getTagSet().add(newTag);

                    }
                }

                taskDao.update(task);
                transaction.commit();
                return taskFactory.makeTaskDto(task);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }
    @Async
    @Override
    public CompletableFuture<Void> removeTaskById(Long idTask) throws SQLException {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {

            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
        assert taskDao != null;
        StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
        assert statusTaskDAO != null;
        StatusTask statusTask = statusTaskDAO.findByTaskId(idTask);
        logger.info("StatusTask id: {}", statusTask.getIdRightTaskStatus());

        TaskParticipantsDAO taskParticipantsDAO = DaoFactory.getDao(TaskParticipantsDAO.class);
        assert taskParticipantsDAO != null;
        Task task = taskDao.findById(idTask);

        if (statusTask != null) {
            StatusTask leftStatus = statusTask.getIdLeftTaskStatus();
            StatusTask rightStatus = statusTask.getIdRightTaskStatus();

            // If the one on the left that is being deleted on the right is not the last one, then we put it to him.
            if (leftStatus != null) {
                if(rightStatus != null) {
                    leftStatus.setIdRightTaskStatus(rightStatus);
                } else {
                    leftStatus.setIdRightTaskStatus(null);
                }
                statusTaskDAO.update(leftStatus);
            }
            // If the one on the right is not the last one to be deleted, then we put it to him.

            if (rightStatus != null) {
                if(leftStatus != null) {
                    rightStatus.setIdLeftTaskStatus(leftStatus);

                } else {
                    rightStatus.setIdRightTaskStatus(null);
                }
                statusTaskDAO.update(rightStatus);
            }

            statusTaskDAO.delete(statusTask.getIdStatusTask());
        }

        taskParticipantsDAO.deleteByTask(idTask);


        if (task != null) {
            task.getStatusTasks().clear();
            task.getTaskParticipantsSet().clear();
            task.getCommentTaskSet().clear();
            taskDao.deleteById(idTask);
        }
        transaction.commit();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
        }, taskExecutor);

    }

    @Async
    @Override
    public CompletableFuture<Void> removeTaskFromProject(Long idTask) throws SQLException {
        return CompletableFuture.runAsync(() -> {

        Transaction transaction = DBService.getTransaction();
        try {
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            Task task = taskDao.findById(idTask);
            if (task == null) {
                throw new EntityNotFoundException("Task not found with id: " + idTask);
            }
            task.setProject(null);
            taskDao.update(task);
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }
    void replaceOldTaskPosition(StatusTask changeTask) {
        StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);
        assert statusTaskDAO != null;


        Optional<StatusTask> optionalOldLeftTask = Optional.ofNullable(changeTask.getIdLeftTaskStatus());
        Optional<StatusTask> optionalOldRightTask = Optional.ofNullable(changeTask.getIdRightTaskStatus());

        // Updating the old left issue
        optionalOldLeftTask.ifPresent(oldLeftTask -> {
            oldLeftTask.setIdRightTaskStatus(optionalOldRightTask.orElse(null));
            try {
                statusTaskDAO.update(oldLeftTask);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Updating the old right-hand issue
        optionalOldRightTask.ifPresent(oldRightTask -> {
            oldRightTask.setIdLeftTaskStatus(optionalOldLeftTask.orElse(null));
            try {
                statusTaskDAO.update(oldRightTask);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
