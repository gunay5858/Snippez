package com.ghlabs.snippez.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghlabs.snippez.entity.CodeSnippet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"creator", "snippets"})
public class CategoryDTO {
    private Long id;

    private String name;

    private int snippetCount;

    private String icon;

    private UserDTO creator;

    private List<CodeSnippetDTO> snippets;

    private Date createdAt;

    private Date updatedAt;

    public CategoryDTO(String name, int snippetCount, String icon, List<CodeSnippetDTO> snippets) {
        this.name = name;
        this.snippetCount = snippetCount;
        this.icon = icon;
        this.snippets = snippets;
    }

    public CategoryDTO(Long id, String name, int snippetCount, String icon, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.snippetCount = snippetCount;
        this.icon = icon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDTO that = (CategoryDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
