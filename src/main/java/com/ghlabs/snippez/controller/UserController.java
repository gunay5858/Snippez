package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.AuthRequest;
import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.exception.UserAlreadyExistsException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.CategoryService;
import com.ghlabs.snippez.service.UserService;
import com.ghlabs.snippez.util.JwtUtil;
import javassist.NotFoundException;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserController(@Autowired UserService userService, CategoryService categoryService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<BasicListResponse> getAllUsers() {
        List<UserDTO> userList = userService.findAllUsers();
        return ResponseEntity.ok(new BasicListResponse(true, userList, Response.SC_OK));
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<BasicSingleResponse> getUserById(@PathVariable("userId") @NotBlank Long userId) throws NotFoundException, MethodArgumentTypeMismatchException {
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, foundUser, Response.SC_OK));
    }

    @GetMapping("/id/{userId}/category")
    public ResponseEntity<BasicListResponse> getCategoriesOfUser(@PathVariable("userId") @NotBlank Long userId) throws NotFoundException, MethodArgumentTypeMismatchException {
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        return ResponseEntity.ok(new BasicListResponse(true, categoryService.findCategoriesOfUser(userId), Response.SC_OK));
    }


    @PostMapping("/create")
    public ResponseEntity<BasicSingleResponse> createUser(@Valid @RequestBody @NotBlank User user) throws UserAlreadyExistsException, HttpMessageNotReadableException {
        User u = userService.addUser(user);
        if (u == null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, u, Response.SC_OK));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<BasicSingleResponse> generateToken(@RequestBody AuthRequest authRequest) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("wrong credentials.");
        }

        return  ResponseEntity.ok(new BasicSingleResponse(true,jwtUtil.generateToken(authRequest.getUsername()), Response.SC_OK));
    }

    //TODO: only execute if user is authenticated and is given user
    @PutMapping("/update/{userId}")
    public ResponseEntity<BasicSingleResponse> updateUser(@PathVariable("userId") @NotBlank Long userId, @RequestBody @NotBlank User user) throws NotFoundException {
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, userService.updateUser(userId, user), Response.SC_OK));
    }

    @DeleteMapping(value = "/delete/{userId}")
    public ResponseEntity<BasicSingleResponse> deleteUser(@PathVariable("userId") @NotBlank long userId) throws NotFoundException {
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        userService.deleteUserById(userId);
        return ResponseEntity.ok(new BasicSingleResponse(true, null, Response.SC_OK));
    }
}
