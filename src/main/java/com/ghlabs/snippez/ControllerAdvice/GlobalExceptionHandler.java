package com.ghlabs.snippez.ControllerAdvice;


import com.ghlabs.snippez.exception.FileTypeNotAllowedException;
import com.ghlabs.snippez.exception.UserIsBlockedException;
import com.ghlabs.snippez.exception.WrongUserException;
import com.ghlabs.snippez.response.ApiError;
import com.ghlabs.snippez.response.ApiErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    String maxFileSizeForUpload;

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<ApiErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.UNAUTHORIZED, "You don't have permission for this."));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserIsBlockedException.class})
    public ResponseEntity<ApiErrorResponse> handleUserIsBlockedException(UserIsBlockedException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({WrongUserException.class})
    public ResponseEntity<ApiErrorResponse> handleWrongUserException(WrongUserException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({FileTypeNotAllowedException.class})
    public ResponseEntity<ApiErrorResponse> handleFileTypeNotAllowedException(FileTypeNotAllowedException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ApiErrorResponse error = new ApiErrorResponse(new ApiError(HttpStatus.BAD_REQUEST, "Maximum filesize (" + maxFileSizeForUpload + ") exceeded!"));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
