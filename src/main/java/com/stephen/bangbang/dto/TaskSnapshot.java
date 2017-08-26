package com.stephen.bangbang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stephen.bangbang.domain.HelpingTask;

import java.io.Serializable;

public class TaskSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("task_id")
    Long taskId;

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("finished_time")
    Long finishedTime;

    @JsonProperty("person_in_charge")
    Long personInCharge;

    String title;

    String details;

    String location;

    @JsonProperty("start_time")
    Long startTime;

    @JsonProperty("end_time")
    Long endTime;

    @JsonProperty("min_price")
    Integer minPrice;

    @JsonProperty("max_price")
    Integer maxPrice;

    String phone;

    String note;

    public TaskSnapshot(HelpingTask ht) {
        taskId = ht.getId();
        userId = ht.getUser().getId();
        title = ht.getTitle();
        details = ht.getDetails();
        location = ht.getLocation();
        startTime = ht.getStartTime().getTime();
        endTime = ht.getEndTime().getTime();
        minPrice = ht.getMinPrice();
        maxPrice = ht.getMaxPrice();
        phone = ht.getPhone();
        note = ht.getNote();
        personInCharge = ht.getResponsiblePerson() == null ? null : ht.getResponsiblePerson().getId();
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(Long personInCharge) {
        this.personInCharge = personInCharge;
    }

    public Long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
    }
}
