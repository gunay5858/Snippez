package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.CodeSnippetDTO;
import com.ghlabs.snippez.entity.CodeSnippet;
import com.ghlabs.snippez.exception.CodeSnippetCreatorNotFoundException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
import com.ghlabs.snippez.service.CategoryService;
import com.ghlabs.snippez.service.CodeSnippetService;
import javassist.NotFoundException;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/snippet")
public class CodeSnippetController {
    private final CodeSnippetService codeSnippetService;
    private final CategoryService categoryService;

    public CodeSnippetController(@Autowired CodeSnippetService codeSnippetService, @Autowired CategoryService categoryService) {
        this.codeSnippetService = codeSnippetService;
        this.categoryService = categoryService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<BasicListResponse> findAllCodeSnippets() {
        return ResponseEntity.ok(new BasicListResponse(true, codeSnippetService.findAllCodeSnippets(), Response.SC_OK));
    }



    @GetMapping(value = "/id/{snippetId}")
    public ResponseEntity<BasicSingleResponse> findCodeSnippetById(@PathVariable("snippetId") @NotBlank long snippetId) throws NotFoundException {
        CodeSnippetDTO foundSnippet = codeSnippetService.findById(snippetId);
        if (foundSnippet == null) {
            throw new NotFoundException("code snippet not found.");
        }

        return ResponseEntity.ok(new BasicSingleResponse(true, codeSnippetService.findById(snippetId), Response.SC_OK));
    }

    @PostMapping("/create")
    public ResponseEntity<BasicSingleResponse> createCodeSnippet(@Valid @RequestBody @NotBlank CodeSnippet codeSnippet) throws HttpMessageNotReadableException, NotFoundException, CodeSnippetCreatorNotFoundException {
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

        return ResponseEntity.ok(new BasicSingleResponse(true, c, Response.SC_OK));
    }

    @PutMapping("/update/{snippetId}")
    public ResponseEntity<BasicSingleResponse> updateCodeSnippet(@PathVariable("snippetId") @NotBlank Long snippetId, @RequestBody @NotBlank CodeSnippet codeSnippet) throws NotFoundException {
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

        return ResponseEntity.ok(new BasicSingleResponse(true, codeSnippetService.updateCodeSnippet(snippetId, codeSnippet), Response.SC_OK));
    }

    @DeleteMapping(value = "/delete/{snippetId}")
    public ResponseEntity<BasicSingleResponse> deleteCodeSnippet(@PathVariable("snippetId") @NotBlank long snippetId) throws NotFoundException {
        CodeSnippetDTO foundSnippet = codeSnippetService.findById(snippetId);
        if (foundSnippet == null) {
            throw new NotFoundException("code snippet not found.");
        }

        codeSnippetService.deleteCodeSnippetById(snippetId);
        return ResponseEntity.ok(new BasicSingleResponse(true, null, Response.SC_OK));
    }
}
