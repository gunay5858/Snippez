package com.ghlabs.snippez.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String username) {
        super("user already exists: " + username);
    }
}
