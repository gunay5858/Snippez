package com.ghlabs.snippez.ControllerAdvice;

import com.ghlabs.snippez.controller.CategoryController;
import com.ghlabs.snippez.exception.CategoryCreatorNotFoundException;
import com.ghlabs.snippez.response.ApiError;
import com.ghlabs.snippez.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = CategoryController.class)
public class CategoryServiceExceptionHandler {

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, "Please provide category data"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CategoryCreatorNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleCategoryCreatorNotFoundException(CategoryCreatorNotFoundException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
