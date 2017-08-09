package com.stephen.bangbang.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "USERS",
        indexes = @Index(name = "username",
        columnList = "USERNAME",
        unique = true))
public class User {

    public enum Gender {
        MALE,
        FEMALE
    }

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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String password;

    @Column(nullable = false, length = 30)
    @NotNull
    @Size(min = 6, max = 30)
    protected String nickname;

    @Enumerated(EnumType.STRING)
    @NotNull
    protected Gender gender = Gender.MALE;

    @Temporal(TemporalType.DATE)
    protected Date birthday;

    @AttributeOverrides({
            @AttributeOverride(name = "province", column = @Column(name = "LOCATION_PROVINCE", length = 8)),
            @AttributeOverride(name = "city", column = @Column(name = "LOCATION_CITY", length = 12))
    }
    )
    protected Address location;

    @AttributeOverrides({
            @AttributeOverride(name = "province", column = @Column(name = "BIRTHPLACE_PROVINCE", length = 8)),
            @AttributeOverride(name = "city", column = @Column(name = "BIRTHPLACE_CITY", length = 12))
    }
    )
    protected Address birthplace;

    @Column(length = 25)
    @Pattern(regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
    protected String email;

    @Column(length = 300)
    @Size(max = 300)
    protected String note;

    protected String avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @org.hibernate.annotations.OrderBy(clause = "id desc")
    protected Set<HelpingTask> tasks = new LinkedHashSet<>();

    // 先设置这两个

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public Address getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(Address birthplace) {
        this.birthplace = birthplace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<HelpingTask> getTasks() {
        return tasks;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
