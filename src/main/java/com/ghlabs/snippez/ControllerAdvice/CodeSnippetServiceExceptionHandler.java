package com.ghlabs.snippez.ControllerAdvice;

import com.ghlabs.snippez.controller.CategoryController;
import com.ghlabs.snippez.controller.CodeSnippetController;
import com.ghlabs.snippez.exception.CategoryCreatorNotFoundException;
import com.ghlabs.snippez.exception.CodeSnippetCreatorNotFoundException;
import com.ghlabs.snippez.response.ApiError;
import com.ghlabs.snippez.response.ApiErrorResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = CodeSnippetController.class)
public class CodeSnippetServiceExceptionHandler {
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CodeSnippetCreatorNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleCodeSnippetCreatorNotFoundException(CodeSnippetCreatorNotFoundException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, "Please provide code snippet data"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, e.getBindingResult().getFieldError().getDefaultMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, "Please provide a valid Id"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
