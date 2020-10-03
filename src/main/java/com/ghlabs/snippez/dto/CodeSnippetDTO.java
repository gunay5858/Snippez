package com.ghlabs.snippez.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonIgnoreProperties({"creator"})
public class CodeSnippetDTO {
    private Long id;

    private String title;

    private UserDTO creator;

    private CategoryDTO category;

    private String description;

    private String code;

    private boolean isPublic = true;

    private String tags;

    private List<UserDTO> sharedUsers;

    private Date createdAt;

    private Date updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeSnippetDTO that = (CodeSnippetDTO) o;
        return id.equals(that.id);
    }

    @Override
    public String toString() {
        return "CodeSnippetDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creator=" + creator +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", isPublic=" + isPublic +
                ", tags='" + tags + '\'' +
                ", sharedUsers=" + sharedUsers +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
