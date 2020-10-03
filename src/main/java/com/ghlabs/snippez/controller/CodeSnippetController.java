package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.CodeSnippetDTO;
import com.ghlabs.snippez.dto.UserDTO;
import com.ghlabs.snippez.entity.CodeSnippet;
import com.ghlabs.snippez.exception.CodeSnippetCreatorNotFoundException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/snippet")
public class CodeSnippetController {
    private final CodeSnippetService codeSnippetService;
    private final CategoryService categoryService;
    private final UserService userService;

    public CodeSnippetController(@Autowired CodeSnippetService codeSnippetService, @Autowired CategoryService categoryService, @Autowired UserService userService) {
        this.codeSnippetService = codeSnippetService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/category/{categoryId}")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicListResponse> findAllCodeSnippetsOfCategory(@PathVariable("categoryId") @NotBlank Long categoryId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<CodeSnippetDTO> snippets = codeSnippetService.findAllCodeSnippetsOfCategory(categoryId);

        UserDTO user = userService.findUserByUsername(auth.getName());
        snippets = snippets.stream().filter(s -> s.getSharedUsers().contains(user) && s.isPublic() || s.getCreator().getId().equals(user.getId())).collect(Collectors.toList());

        return ResponseEntity.ok(new BasicListResponse(true, snippets, Response.SC_OK));
    }

    @GetMapping(value = "/id/{snippetId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> findCodeSnippetById(@PathVariable("snippetId") @NotBlank long snippetId) throws NotFoundException, WrongUserException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CodeSnippetDTO foundSnippet = codeSnippetService.findById(snippetId);
        if (foundSnippet == null) {
            throw new NotFoundException("code snippet not found.");
        }

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!auth.getName().equals(foundSnippet.getCreator().getUsername())) {
                throw new WrongUserException("this snippet does not belong to you.");
            }
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, codeSnippetService.findById(snippetId), Response.SC_OK));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> createCodeSnippet(@Valid @RequestBody @NotBlank CodeSnippet codeSnippet) throws HttpMessageNotReadableException, NotFoundException, CodeSnippetCreatorNotFoundException, WrongUserException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (codeSnippet == null) {
            throw new HttpMessageNotReadableException(null);
        }

        if (codeSnippet.getCreator() == null) {
            throw new CodeSnippetCreatorNotFoundException("Cannot create code snippet without creator.");
        }

        try {
            if (codeSnippet.getCategory().getId() == null) {
                throw new NotFoundException("category not found.");
            }
        } catch (NullPointerException e) {
            codeSnippet.setCategory(null);
        }

        CodeSnippetDTO c = codeSnippetService.addCodeSnippet(codeSnippet);

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin")) && c.getCategory() != null) {
            if (!c.getCategory().getCreator().getUsername().equals(auth.getName())) {
                throw new WrongUserException("this category is not yours.");
            }
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, c, Response.SC_OK));
    }

    @PutMapping("/update/{snippetId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> updateCodeSnippet(@PathVariable("snippetId") @NotBlank Long snippetId, @RequestBody @NotBlank CodeSnippet codeSnippet) throws NotFoundException, WrongUserException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CodeSnippetDTO foundCodeSnippet = codeSnippetService.findById(snippetId);
        if (foundCodeSnippet == null) {
            throw new NotFoundException("code snippet not found.");
        }

        try {
            CategoryDTO foundCategory = categoryService.findCategoryById(codeSnippet.getCategory().getId());
            if (foundCategory == null) {
                throw new NotFoundException("category not found.");
            }
        } catch (NullPointerException e) {
            codeSnippet.setCategory(null);
        }


        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!foundCodeSnippet.getCategory().getCreator().getUsername().equals(auth.getName())) {
                throw new WrongUserException("this category is not yours.");
            }

            if (!foundCodeSnippet.getCreator().getUsername().equals(auth.getName())) {
                throw new WrongUserException("this codeSnippet is not yours.");
            }
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, codeSnippetService.updateCodeSnippet(snippetId, codeSnippet), Response.SC_OK));
    }

    @DeleteMapping(value = "/delete/{snippetId}")
    @PreAuthorize("hasAnyAuthority({'member', 'admin'})")
    public ResponseEntity<BasicSingleResponse> deleteCodeSnippet(@PathVariable("snippetId") @NotBlank long snippetId) throws NotFoundException, WrongUserException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CodeSnippetDTO foundSnippet = codeSnippetService.findById(snippetId);
        if (foundSnippet == null) {
            throw new NotFoundException("code snippet not found.");
        }

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            if (!foundSnippet.getCreator().getUsername().equals(auth.getName())) {
                throw new WrongUserException("this codeSnippet is not yours.");
            }
        }


        codeSnippetService.deleteCodeSnippetById(snippetId);
        return ResponseEntity.ok(new BasicSingleResponse(true, null, Response.SC_OK));
    }
}
