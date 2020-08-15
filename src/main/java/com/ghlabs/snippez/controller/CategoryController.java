package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.User;
import com.ghlabs.snippez.exception.UserAlreadyExistsException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.CategoryService;
import com.ghlabs.snippez.service.UserService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(@Autowired CategoryService categoryService, @Autowired UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<BasicListResponse> findAllCategories() {
        return ResponseEntity.ok(new BasicListResponse(true, categoryService.findAllCategories(), Response.SC_OK));
    }

    @PostMapping("/create")
    public ResponseEntity<BasicSingleResponse> createCategory(@RequestBody @NotBlank Category category) {
        User refCreator = userService.findUserById(category.getCreatedBy().getId());
        category.setCreatedBy(refCreator);

        return ResponseEntity.ok(new BasicSingleResponse(true, categoryService.addCategory(category), Response.SC_OK));
    }
}
