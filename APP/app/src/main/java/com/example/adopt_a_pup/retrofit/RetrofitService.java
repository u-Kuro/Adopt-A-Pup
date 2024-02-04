package com.example.adopt_a_pup.retrofit;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    /** @noinspection FieldCanBeLocal*/
    final private String IPv4 = "192.168.1.10";
    private Retrofit retrofit;
    public RetrofitService() {
        initRetrofit();
    }
    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://"+IPv4+":18080")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }
    public Retrofit getRetrofit() {
        return retrofit;
    }
}