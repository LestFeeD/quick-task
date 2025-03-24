package com.quick_task.dao;

import com.quick_task.entity.Project;
import com.quick_task.entity.WebUser;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public interface WebUserDAO extends Dao{

    WebUser create(WebUser webUser) throws SQLException;
    WebUser update(WebUser webUser) throws SQLException;
    Integer findByEmail(String email) throws SQLException;
    WebUser findUserByEmail(String email) throws SQLException;
    WebUser findById(Long idWebUser) throws SQLException;
    WebUser findOwnerTaskByIdTask(Long idTask) throws  SQLException;
    Long findByIdToken(Long idToken) throws SQLException;
    void deleteById(Long idUser) throws SQLException;

}
