package com.ghlabs.snippez.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = CodeSnippet.class)
public class CodeSnippet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_seq_codeSnippet")
    @SequenceGenerator(name = "_seq_codeSnippet", sequenceName = "_seq_codeSnippet", initialValue = 1, allocationSize=1)
    private Long id;

    @NotEmpty(message = "Please provide a title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Category.class)
    @JoinColumn(name = "category", referencedColumnName = "id")
    private Category category;

    private String description;

    @NotEmpty(message = "Please provide some code")
    private String code;

    private boolean isPublic = true;

    private String tags;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = CascadeType.MERGE)
    @JoinColumn(name = "creator", referencedColumnName = "id", nullable = false)
    private User creator;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "accessUser_codeSnippet",
            joinColumns = @JoinColumn(name = "snippet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> sharedUsers;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    private void createdAt() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    private void updatedAt() {
        this.updatedAt = new Date();
    }

    @Override
    public String toString() {
        return "CodeSnippet{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", isPublic=" + isPublic +
                ", tags='" + tags + '\'' +
                ", creator=" + creator +
                ", sharedUsers=" + sharedUsers +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeSnippet that = (CodeSnippet) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
