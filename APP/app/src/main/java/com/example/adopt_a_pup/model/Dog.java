package com.example.adopt_a_pup.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Base64;

/** @noinspection unused*/
public class Dog {
    private long id;
    private String name;
    private String breed;
    private long birthTimestamp;
    private String image;

    public Dog(String name, String breed, long birthTimestamp, byte[] imageBytes) {
        this.name = name;
        this.breed = breed;
        this.birthTimestamp = birthTimestamp;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.image = Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    public long getId() {
        return id;
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

    public byte[] getImage() {
        byte[] imageByte = null;
        if (this.image!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String base64Data = this.image;
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            imageByte = Base64.getDecoder().decode(base64Data);
        }
        return imageByte;
    }

    public void setImage(byte[] imageBytes) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.image = Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge() {
        Instant instant = Instant.ofEpochMilli(birthTimestamp);
        LocalDate birthdate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }
}
