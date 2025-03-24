package com.quick_task.controller;

import com.quick_task.dto.LoginDtoRequest;
import com.quick_task.dto.create.CreateWebUserDtoRequest;
import com.quick_task.dto.response.WebUserDtoResponse;
import com.quick_task.dto.update.UpdateWebUserDtoRequest;
import com.quick_task.exception.EntityNotFoundException;
import com.quick_task.service.WebUserServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@RestController
@Tag(name = "User")
public class UserController {
    private final WebUserServiceImpl userService;
    public static final String REGISTER_USER = "/registration";
    public static final String LOGIN_USER = "/login";
    public static final String CONFIRM_USER = "/registration/confirm";
    public static final String UPDATE_USER = "/general-user";
    public static final String FIND_USER = "/user";
    public static final String DELETE_USER = "/user";




    public UserController(WebUserServiceImpl userService) {
        this.userService = userService;
    }
    @SecurityRequirements
    @PostMapping(REGISTER_USER)
    public ResponseEntity<CompletableFuture<String>> createUser(@RequestBody CreateWebUserDtoRequest dtoRequest)  {
        return  new ResponseEntity<>(userService.registerUser(dtoRequest), HttpStatus.CREATED);
    }

    @SecurityRequirements
    @PostMapping(LOGIN_USER)
    public CompletableFuture<String> loginUserController(@Valid @RequestBody LoginDtoRequest dtoRequest) throws SQLException {
        return  userService.loginUser(dtoRequest);
    }

    @SecurityRequirements
    @Operation(
            summary = "User's email confirmation",
            description = "The user receives a token that will last up to 15 minutes, and which must be confirmed by mail in order for the account to be saved."
    )
    @GetMapping(CONFIRM_USER)
    public CompletableFuture<String> confirm(@RequestParam("token") String token)  {
        return  userService.confirmToken(token);
    }
    @Operation(
            summary = "Updating user parameters.",
            description = "The user can update their data."
    )
    @PatchMapping(UPDATE_USER)
    public CompletableFuture<Void> updateUser( @Valid @RequestBody UpdateWebUserDtoRequest dtoRequest) {
        return  userService.updateUser( dtoRequest);
    }
    @Operation(
            summary = "Find a user by email.",
            description = "The method is used for integration testing."
    )
    @GetMapping(FIND_USER)
    public  CompletableFuture<ResponseEntity<WebUserDtoResponse>>  findUser(@RequestParam("email") String email) throws EntityNotFoundException {
        return  userService.findUser( email);
    }
    @DeleteMapping(DELETE_USER)
    public  CompletableFuture<Void> deleteUser() throws SQLException {
        return  userService.deleteUser();
    }
}
