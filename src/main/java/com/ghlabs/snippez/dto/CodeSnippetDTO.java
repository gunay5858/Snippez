package com.ghlabs.snippez.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class CodeSnippetDTO {
    private Long id;

    private String title;

    @JsonIgnoreProperties("creator")
    private UserDTO creator;

    @JsonIgnoreProperties("categories")
    private CategoryDTO category;

    private String description;

    private String code;

    private boolean isPublic = true;

    private String tags;

    private List<UserDTO> sharedUsers;

    private Date createdAt;

    private Date updatedAt;
}
