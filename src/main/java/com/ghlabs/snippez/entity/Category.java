package com.ghlabs.snippez.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String icon;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="creator_id")
    private User createdBy;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
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
}
