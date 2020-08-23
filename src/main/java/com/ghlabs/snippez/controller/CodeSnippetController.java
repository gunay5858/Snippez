package com.ghlabs.snippez.controller;

import com.ghlabs.snippez.dto.CategoryDTO;
import com.ghlabs.snippez.dto.CodeSnippetDTO;
import com.ghlabs.snippez.entity.CodeSnippet;
import com.ghlabs.snippez.exception.CodeSnippetCreatorNotFoundException;
import com.ghlabs.snippez.response.BasicListResponse;
import com.ghlabs.snippez.response.BasicSingleResponse;
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

    public CodeSnippetController(@Autowired CodeSnippetService codeSnippetService) {
        this.codeSnippetService = codeSnippetService;
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
