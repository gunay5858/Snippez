package com.ghlabs.snippez.ControllerAdvice;

import com.ghlabs.snippez.exception.UserAlreadyExistsException;
import com.ghlabs.snippez.response.ApiError;
import com.ghlabs.snippez.response.ApiErrorResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class UserServiceExceptionHandler {
    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, "Please provide a valid Id"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, "Please provide user data"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
