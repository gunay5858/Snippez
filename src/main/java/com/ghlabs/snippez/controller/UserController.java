package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.AuthRequest;
import com.ghlabs.snippez.dto.UploadFileDTO;
import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.exception.*;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.CategoryService;
import com.ghlabs.snippez.service.UserAvatarService;
import com.ghlabs.snippez.service.UserService;
import com.ghlabs.snippez.util.JwtUtil;
import javassist.NotFoundException;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    private final UserService userService;
    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserAvatarService userAvatarService;

    public UserController(@Autowired UserService userService, @Autowired CategoryService categoryService, @Autowired JwtUtil jwtUtil, @Autowired AuthenticationManager authenticationManager, @Autowired UserAvatarService userAvatarService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userAvatarService = userAvatarService;
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAnyAuthority({'admin'})")
    public ResponseEntity<BasicListResponse> getAllUsers() {
        List<UserDTO> userList = userService.findAllUsers();
        return ResponseEntity.ok(new BasicListResponse(true, userList, Response.SC_OK));
    }

    @GetMapping("/id/{userId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> getUserById(@PathVariable("userId") @NotBlank Long userId) throws NotFoundException, MethodArgumentTypeMismatchException {
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, foundUser, Response.SC_OK));
    }

    @GetMapping("/id/{userId}/category")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicListResponse> getCategoriesOfUser(@PathVariable("userId") @NotBlank Long userId) throws NotFoundException, MethodArgumentTypeMismatchException, WrongUserException, UserIsBlockedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!foundUser.getUsername().equals(auth.getName())) {
                throw new WrongUserException("this is not you.");
            }

            if (!foundUser.isEnabled()) {
                throw new UserIsBlockedException("you are blocked");
            }
        }

        return ResponseEntity.ok(new BasicListResponse(true, categoryService.findCategoriesOfUser(userId), Response.SC_OK));
    }


    @PostMapping("/create")
    public ResponseEntity<BasicSingleResponse> createUser(@Valid @RequestBody @NotBlank User user) throws UserAlreadyExistsException, HttpMessageNotReadableException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin")) || user.getRole() == null) {
            user.setRole("member");
        }

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

        String userRole = userService.findUserByUsername(authRequest.getUsername()).getRole();

        return ResponseEntity.ok(new BasicSingleResponse(true, jwtUtil.generateToken(authRequest.getUsername(), userRole), Response.SC_OK));
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> updateUser(@PathVariable("userId") @NotBlank Long userId, @RequestBody @NotBlank User user) throws NotFoundException, WrongUserException, UserIsBlockedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!foundUser.getUsername().equals(auth.getName())) {
                throw new WrongUserException("this is not you.");
            }

            if (!foundUser.isEnabled()) {
                throw new UserIsBlockedException("you are blocked");
            }
        } else {
            if (user.isEnabled() != foundUser.isEnabled()) {
                foundUser.setEnabled(user.isEnabled());
            }
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, userService.updateUser(userId, user), Response.SC_OK));
    }

    @DeleteMapping(value = "/delete/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BasicSingleResponse> deleteUser(@PathVariable("userId") @NotBlank long userId) throws NotFoundException {
        UserDTO foundUser = userService.findUserById(userId);
        if (foundUser == null) {
            throw new NotFoundException("user not found.");
        }
        userService.deleteUserById(userId);
        return ResponseEntity.ok(new BasicSingleResponse(true, null, Response.SC_OK));
    }

    // TODO: check only images are uploaded
    @PostMapping("/avatar/upload")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> uploadFile(@RequestParam("file") MultipartFile file) throws FileStorageException, FileTypeNotAllowedException {

        // upload file
        String fileName = userAvatarService.uploadFile(file);

        // create resource and return
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/avatar/")
                .path(fileName)
                .toUriString();

        return ResponseEntity.ok(new BasicSingleResponse(true, new UploadFileDTO(fileName, fileDownloadUri,
                file.getContentType(), file.getSize()), Response.SC_OK));
    }

    @GetMapping("/avatar/{fileName:.+}")
    @PermitAll
    public ResponseEntity<Resource> downloadFile(@PathVariable @NotBlank String fileName, HttpServletRequest request) throws FileNotFoundException {
        // Load file as Resource
        Resource resource = userAvatarService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = MediaType.APPLICATION_OCTET_STREAM.getType();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
