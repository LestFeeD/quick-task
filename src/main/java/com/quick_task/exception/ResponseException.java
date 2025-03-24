package com.quick_task.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;


public record ResponseException(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {
}
