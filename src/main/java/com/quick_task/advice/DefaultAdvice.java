package com.quick_task.advice;

import com.quick_task.exception.BadRequestException;
import com.quick_task.exception.EntityNotFoundException;
import com.quick_task.exception.ResponseException;
import com.quick_task.validation.ValidationErrorResponse;
import com.quick_task.validation.Violation;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Hidden
@ControllerAdvice
public class DefaultAdvice  {


    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<ResponseException> handleExceptionBadRequest(BadRequestException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ResponseException response = new ResponseException(
                exception.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response,badRequest);
    }

    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationErrorResponse(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseException> onEntityNotFoundException (EntityNotFoundException e) {
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ResponseException response = new ResponseException(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(response,badRequest);
    }


}
