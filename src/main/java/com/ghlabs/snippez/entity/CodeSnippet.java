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
public class CodeSnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="creator")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private String description;

    private String code;

    private boolean isPublic = true;

    private String tags;

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
}
