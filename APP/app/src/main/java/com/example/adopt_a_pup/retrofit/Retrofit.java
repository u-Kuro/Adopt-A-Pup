package com.example.adopt_a_pup.retrofit;

/** @noinspection unused, unused, unused, unused */
public class Retrofit {
    private static Retrofit instance;

    private Retrofit() {}

    public static synchronized Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit();
        }
        return instance;
    }

    private String variable;
}
