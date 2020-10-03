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
        property = "id", scope = Category.class)
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_seq_category")
    @SequenceGenerator(name = "_seq_category", sequenceName = "_seq_category", initialValue = 1, allocationSize=1)
    private Long id;

    @NotEmpty(message = "Please provide a name")
    private String name;

    private String icon;

    @Transient
    private int snippetCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", referencedColumnName = "id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "category",
            fetch = FetchType.LAZY,
            targetEntity = CodeSnippet.class,
            cascade = CascadeType.MERGE)
    private List<CodeSnippet> snippets;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @PreRemove
    void preRemove() {
        for (CodeSnippet s : this.getSnippets()) {
            s.setCategory(null);
        }

        this.setCreator(null);
    }
}
