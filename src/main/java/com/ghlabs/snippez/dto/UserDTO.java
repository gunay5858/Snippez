package com.ghlabs.snippez.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghlabs.snippez.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    private String username;

    private String email;

    private String avatar;

    @JsonIgnoreProperties("creator")
    private List<CategoryDTO> categories = new ArrayList<>();

    private Date createdAt;

    private Date updatedAt;
}
