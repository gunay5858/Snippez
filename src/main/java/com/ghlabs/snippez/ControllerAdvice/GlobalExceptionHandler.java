package com.ghlabs.snippez.ControllerAdvice;


import com.ghlabs.snippez.response.ApiError;
import com.ghlabs.snippez.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
