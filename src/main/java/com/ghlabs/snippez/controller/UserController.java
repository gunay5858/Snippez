package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.exception.UserAlreadyExistsException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.UserService;
import javassist.NotFoundException;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<BasicListResponse> getAllUsers() {
        ArrayList<Object> userList = userService.findAllUsers();
        return ResponseEntity.ok(new BasicListResponse(true, userList, Response.SC_OK));
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<BasicSingleResponse> getUserById(@PathVariable("userId") @NotBlank Long userId) throws NotFoundException, MethodArgumentTypeMismatchException {
        User foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, foundUser, Response.SC_OK));
    }


    @PostMapping("/create")
    public ResponseEntity<BasicSingleResponse> createUser(@RequestBody @NotBlank User user) throws UserAlreadyExistsException, HttpMessageNotReadableException {
        User u = userService.addUser(user);
        if (u == null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, u, Response.SC_OK));
    }
}
