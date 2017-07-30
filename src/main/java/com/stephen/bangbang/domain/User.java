package com.stephen.bangbang.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "USERS",
        indexes = @Index(name = "username",
        columnList = "USERNAME",
        unique = true))
public class User {

    protected User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id
    @GeneratedValue(generator = Constants.PERFECT_SEQUENCE)
    protected Long id;

    @Column(unique = true, nullable = false, length = 20)
    @NotNull
    @Size(min = 5, max = 16)
    protected String username;

    @Column(nullable = false)
    @NotNull
    @JsonIgnore
    protected String password;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @org.hibernate.annotations.OrderBy(clause = "id desc")
    protected Set<HelpingTask> tasks = new LinkedHashSet<>();

    // 先设置这两个

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<HelpingTask> getTasks() {
        return tasks;
    }
}
