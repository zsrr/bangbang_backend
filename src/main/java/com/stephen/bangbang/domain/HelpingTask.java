package com.stephen.bangbang.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "TASKS")
public class HelpingTask {
    @Id
    @GeneratedValue(generator = Constants.PERFECT_SEQUENCE)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    protected User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PERSON_IN_CHARGE")
    protected User responsiblePerson;

    @Column(nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    protected String title;

    @Column(length = 200)
    @Size(max = 30)
    protected String details;

    @Column(length = 40, nullable = false)
    @NotNull
    @Size(max = 40)
    protected String location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    @JsonProperty("start_time")
    protected Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    @JsonProperty("end_time")
    protected Date endTime;

    @Column(nullable = false)
    @NotNull
    @JsonProperty("min_price")
    protected Integer minPrice;

    @JsonProperty("max_price")
    protected Integer maxPrice;

    @Column(nullable = false, length = 15)
    @NotNull
    @Size(max = 11)
    protected String phone;

    @Column(length = 200)
    @Size(max = 200)
    protected String note;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    protected Date createTime;

    public HelpingTask() {

    }

    public HelpingTask(User user, String title, String location, Date startTime, Date endTime, Integer minPrice, String phone) {
        this.user = user;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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

    public Long getId() {
        return id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public User getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(User responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }
}
