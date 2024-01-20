package com.ktoda.app.common.exception;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public record ExceptionResponse(String msg, HttpStatus status, Timestamp timestamp) {
}
