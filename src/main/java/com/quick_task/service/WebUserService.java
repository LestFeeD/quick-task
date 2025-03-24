package com.quick_task.service;

import com.quick_task.dto.LoginDtoRequest;
import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.create.CreateWebUserDtoRequest;
import com.quick_task.dto.response.ProjectDtoResponse;
import com.quick_task.dto.response.WebUserDtoResponse;
import com.quick_task.dto.update.UpdateProjectDtoRequest;
import com.quick_task.dto.update.UpdateWebUserDtoRequest;
import com.quick_task.exception.BadRequestException;
import com.quick_task.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface WebUserService {

    CompletableFuture<String> registerUser(CreateWebUserDtoRequest dtoRequest);

    CompletableFuture<String> loginUser(LoginDtoRequest loginDtoRequest) throws SQLException;

    CompletableFuture<ResponseEntity<WebUserDtoResponse>> findUser(String email) throws EntityNotFoundException;

    CompletableFuture<Void> updateUser( UpdateWebUserDtoRequest dtoRequest);

    CompletableFuture<Void> deleteUser();



}
