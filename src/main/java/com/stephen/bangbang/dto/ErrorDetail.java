package com.stephen.bangbang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDetail {
    String title;
    @JsonProperty("error_class_name")
    String errorClassName;
    String description;

    public ErrorDetail(String title, Class clazz, String description) {
        this.title = title;
        this.errorClassName = clazz.getName();
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getErrorClassName() {
        return errorClassName;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setErrorClassName(String errorClassName) {
        this.errorClassName = errorClassName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
