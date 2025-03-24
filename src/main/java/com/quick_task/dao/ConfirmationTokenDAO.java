package com.quick_task.dao;

import com.quick_task.entity.ConfirmationToken;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

public interface ConfirmationTokenDAO  extends Dao{
    ConfirmationToken findByToken(String token) throws SQLException;
    ConfirmationToken findById(Long id) throws SQLException;
    void createConfirmationToken(ConfirmationToken confirmationToken) throws SQLException;
    Set<Long> findAllExpiredToken(Timestamp timestamp) throws SQLException;
    void deleteById(Long id) throws SQLException;
    void deleteByUser(Long userId) throws SQLException;

}
