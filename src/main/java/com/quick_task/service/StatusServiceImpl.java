package com.quick_task.service;

import com.quick_task.dao.StatusDAO;
import com.quick_task.dao.StatusRoleDAO;
import com.quick_task.dao.TaskDAO;
import com.quick_task.dao.WebUserDAO;
import com.quick_task.dto.create.CreateStatusDtoRequest;
import com.quick_task.dto.response.AllStatusDtoResponse;
import com.quick_task.dto.response.AllTaskDtoResponse;
import com.quick_task.dto.update.UpdateCustomStatusDtoRequest;
import com.quick_task.entity.Status;
import com.quick_task.entity.StatusRole;
import com.quick_task.entity.WebUser;
import com.quick_task.factory.DaoFactory;
import com.quick_task.factory.StatusFactory;
import com.quick_task.utils.DBService;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {
    private final StatusFactory statusFactory;
    private final AuthenticationService authenticationService;


    public StatusServiceImpl(StatusFactory statusFactory, AuthenticationService authenticationService) {
        this.statusFactory = statusFactory;
        this.authenticationService = authenticationService;
    }
    //TODO: make it possible to change the status of the custom
    //TODO: make a status output for a project/task
    @Override
    public void createCustomStatusService(CreateStatusDtoRequest dtoRequest) {
        Transaction transaction = DBService.getTransaction();
        try {
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            StatusRoleDAO statusRoleDAO = DaoFactory.getDao(StatusRoleDAO.class);
            assert statusRoleDAO != null;
            StatusRole statusRole = statusRoleDAO.findById(dtoRequest.getIdStatusRole());
            Long userId = authenticationService.getCurrentUserId();
            WebUser webUser = webUserDAO.findById(userId);

            Status status = Status.builder()
                    .nameStatus(dtoRequest.getNameStatus())
                    .isDefault(0)
                    .webUser(webUser)
                    .statusRole(statusRole)
                    .build();
            statusDAO.createStatus(status);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCustomStatusService(UpdateCustomStatusDtoRequest dtoRequest) {
        Transaction transaction = DBService.getTransaction();
        try {
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            StatusRoleDAO statusRoleDAO = DaoFactory.getDao(StatusRoleDAO.class);
            assert statusRoleDAO != null;
            Status status = statusDAO.findById(dtoRequest.getIdStatusRole());
            if(dtoRequest.getNameStatus() != null) {
                status.setNameStatus(dtoRequest.getNameStatus());
            }
            if(dtoRequest.getNameStatus() != null) {
                StatusRole statusRole = statusRoleDAO.findById(dtoRequest.getIdStatusRole());
                status.setStatusRole(statusRole);
                statusRoleDAO.update(statusRole);
            }
            statusDAO.update(status);
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AllStatusDtoResponse> getAllStatusForTask(Long idTask) {
        Transaction transaction = DBService.getTransaction();
        try {
            StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
            assert statusDAO != null;
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            TaskDAO taskDao = DaoFactory.getDao(TaskDAO.class);
            assert taskDao != null;
            StatusRoleDAO statusRoleDAO = DaoFactory.getDao(StatusRoleDAO.class);
            WebUser webUser = webUserDAO.findOwnerTaskByIdTask(idTask);
            List<Status> statusList = statusDAO.findAllStatusUser(webUser.getIdWebUser());
            transaction.commit();

            return  statusList.stream()
                        .map(statusFactory::makeStatus)
                        .toList();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
