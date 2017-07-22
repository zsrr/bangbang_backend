package com.stephen.bangbang.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    protected String password;

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
}
