package com.ghlabs.snippez.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id", scope = User.class)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_seq_user")
    @SequenceGenerator(name = "_seq_user", sequenceName = "_seq_user", initialValue = 1, allocationSize=1)
    private Long id;

    @NotEmpty(message = "Please provide a username")
    private String username;

    @NotEmpty(message = "Please provide a password")
    private String password;

    @NotEmpty(message = "Please provide an email")
    private String email;

    private String avatar;

    @ManyToMany(mappedBy = "sharedUsers")
    private List<CodeSnippet> sharedSnippets;

    @OneToMany(mappedBy = "creator",
            targetEntity = Category.class,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "creator",
            targetEntity = CodeSnippet.class,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<CodeSnippet> codeSnippets = new ArrayList<>();

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
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", categories=" + categories +
                '}';
    }
}
