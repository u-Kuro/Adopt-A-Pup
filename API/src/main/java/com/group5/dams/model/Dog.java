package com.group5.dams.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ext.SqlBlobSerializer;

import javax.persistence.*;
import java.sql.Blob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import static javax.persistence.FetchType.LAZY;


@Entity
@Table(name="dog")
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String breed;
    private long birthTimestamp;
    @Column(name = "image", columnDefinition="LONGBLOB")
    @Lob @Basic(fetch=LAZY)
    private byte[] image;

    public Dog() {}

    public Dog(long id, String name, String breed, long birthTimestamp, byte[] image) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.birthTimestamp = birthTimestamp;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public long getBirthTimestamp() {
        return birthTimestamp;
    }

    public void setBirthTimestamp(long birthTimestamp) {
        this.birthTimestamp = birthTimestamp;
    }

    public int getAge() {
        Instant instant = Instant.ofEpochMilli(birthTimestamp);
        LocalDate birthdate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
