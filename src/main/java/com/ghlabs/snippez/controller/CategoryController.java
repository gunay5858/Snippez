package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.exception.CategoryCreatorNotFoundException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.CategoryService;
import com.ghlabs.snippez.service.UserService;
import javassist.NotFoundException;
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

    @GetMapping("/id/{catId}")
    @ResponseBody
    public ResponseEntity<BasicSingleResponse> findCategoryById(@PathVariable("catId") @NotBlank Long catId) throws NotFoundException {
        CategoryDTO foundCategory = categoryService.findCategoryById(catId);
        if (foundCategory == null) {
            throw new NotFoundException("category not found.");
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, foundCategory, Response.SC_OK));
    }

    @PostMapping("/create")
    public ResponseEntity<BasicSingleResponse> createCategory(@RequestBody @NotBlank Category category) throws HttpMessageNotReadableException, CategoryCreatorNotFoundException {
        if (category == null) {
            throw new HttpMessageNotReadableException(null);
        }

        if (category.getCreator() == null) {
            throw new CategoryCreatorNotFoundException("Cannot create category without creator.");
        }

        CategoryDTO c = categoryService.addCategory(category);

        if (c == null) {
            throw new CategoryCreatorNotFoundException("The provided creator does not exist.");
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, c, Response.SC_OK));
    }

    @DeleteMapping(value = "/delete/{catId}")
    public ResponseEntity<BasicSingleResponse> deleteCategory(@PathVariable("catId") @NotBlank long catId) throws NotFoundException {
        CategoryDTO foundCategory = categoryService.findCategoryById(catId);
        if (foundCategory == null) {
            throw new NotFoundException("category not found.");
        }

        categoryService.deleteCategoryId(catId);
        return ResponseEntity.ok(new BasicSingleResponse(true, null, Response.SC_OK));
    }
}
