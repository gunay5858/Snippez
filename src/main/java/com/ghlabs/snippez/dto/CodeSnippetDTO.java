package com.ghlabs.snippez.dto;

import com.ghlabs.snippez.entity.Category;
import com.ghlabs.snippez.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeSnippetDTO {
    private Long id;

    private String title;

    private User createdBy;

    private Category category;

    private String description;

    private String code;

    private boolean isPublic = true;

    private String tags;

    private User creator;

    private List<User> sharedUsers;

    private Date createdAt;

    private Date updatedAt;
}
