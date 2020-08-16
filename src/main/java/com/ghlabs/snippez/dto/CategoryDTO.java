package com.ghlabs.snippez.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghlabs.snippez.entity.CodeSnippet;
import com.ghlabs.snippez.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;

    private String name;

    private String icon;

    @JsonIgnoreProperties("categories")
    private UserDTO creator;

    private List<CodeSnippet> snippets;

    private Date createdAt;

    private Date updatedAt;
}
