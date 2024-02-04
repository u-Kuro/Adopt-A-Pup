package com.example.adopt_a_pup.model;

/** @noinspection CanBeFinal, FieldMayBeFinal, unused, FieldCanBeLocal */
public class Person {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public Person(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    public String getEmail() {
        return email;
    }
}
