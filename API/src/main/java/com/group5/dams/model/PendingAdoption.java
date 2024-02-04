package com.group5.dams.model;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name="pendingAdoption")
public class PendingAdoption {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long userId;
    @Column(unique=true)
    private long dogId;
    private LocalDateTime dateTime;

    public PendingAdoption() { }

    public PendingAdoption(long id, long userId, long dogId) {
        this.id = id;
        this.userId = userId;
        this.dogId = dogId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDogId() {
        return dogId;
    }

    public void setDogId(long dogId) {
        this.dogId = dogId;
    }

    public long getPendingAdoptionDateTimestamp() {
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    public void setPendingAdoptionDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
