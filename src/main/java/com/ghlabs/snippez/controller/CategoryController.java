package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.CodeSnippetDTO;
import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.exception.CategoryCreatorNotFoundException;
import com.ghlabs.snippez.exception.UserIsBlockedException;
import com.ghlabs.snippez.exception.WrongUserException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.CategoryService;
import com.ghlabs.snippez.service.CodeSnippetService;
import com.ghlabs.snippez.service.UserService;
import javassist.NotFoundException;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final CodeSnippetService codeSnippetService;

    public CategoryController(@Autowired CategoryService categoryService, @Autowired UserService userService, CodeSnippetService codeSnippetService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.codeSnippetService = codeSnippetService;
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicListResponse> findAllCategories() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.findUserByUsername(auth.getName()).getId();

        List<CategoryDTO> categories = categoryService.findCategoriesOfUser(userId);
        List<CodeSnippetDTO> uncategorizedSnippets = codeSnippetService.findUncategorizedSnippetsOfUser(userId);
        categories.add(new CategoryDTO("Uncategorized", uncategorizedSnippets.size(), uncategorizedSnippets));


        return ResponseEntity.ok(new BasicListResponse(true, categories, Response.SC_OK));
    }

    @GetMapping("/id/{catId}")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> findCategoryById(@PathVariable("catId") @NotBlank Long catId) throws NotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CategoryDTO foundCategory = categoryService.findCategoryById(catId);
        if (foundCategory == null || !foundCategory.getCreator().getUsername().equals(auth.getName())) {
            throw new NotFoundException("category not found.");
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, foundCategory, Response.SC_OK));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> createCategory(@Valid @RequestBody @NotBlank Category category) throws HttpMessageNotReadableException, CategoryCreatorNotFoundException, WrongUserException, UserIsBlockedException {
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!auth.getName().equals(c.getCreator().getUsername())) {
                throw new WrongUserException("this is not you.");
            }

            if (!userService.checkUserIsEnabled(auth.getName())) {
                throw new UserIsBlockedException("you are blocked.");
            }
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, c, Response.SC_OK));
    }

    @PutMapping("/update/{catId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> updateCategory(@PathVariable("catId") @NotBlank Long catId, @RequestBody @NotBlank Category category) throws NotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CategoryDTO foundCategory = categoryService.findCategoryById(catId);
        if (foundCategory == null) {
            throw new NotFoundException("category not found.");
        }

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!foundCategory.getCreator().getUsername().equals(auth.getName())) {
                throw new AccessDeniedException("you are not allowed to do this");
            }
        }
        return ResponseEntity.ok(new BasicSingleResponse(true, categoryService.updateCategory(catId, category), Response.SC_OK));
    }

    @DeleteMapping(value = "/delete/{catId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> deleteCategory(@PathVariable("catId") @NotBlank long catId) throws NotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CategoryDTO foundCategory = categoryService.findCategoryById(catId);
        if (foundCategory == null) {
            throw new NotFoundException("category not found.");
        }

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!foundCategory.getCreator().getUsername().equals(auth.getName())) {
                throw new AccessDeniedException("you are not allowed to do this");
            }
        }

        categoryService.deleteCategoryById(catId);
        return ResponseEntity.ok(new BasicSingleResponse(true, null, Response.SC_OK));
    }
}
