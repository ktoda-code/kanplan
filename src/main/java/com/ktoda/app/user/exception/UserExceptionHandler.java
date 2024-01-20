package com.ktoda.app.user.exception;

import com.ktoda.app.common.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Timestamp;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleNotFound(UserNotFoundException unfe) {
        ExceptionResponse er = new ExceptionResponse(
                unfe.getMessage(),
                HttpStatus.NOT_FOUND,
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(er);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionResponse> handleAlreadyExists(UserAlreadyExistsException uaee) {
        ExceptionResponse er = new ExceptionResponse(
                uaee.getMessage(),
                HttpStatus.CONFLICT,
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(er);
    }
}
