package com.example.adopt_a_pup.retrofit;

import com.example.adopt_a_pup.model.Person;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PersonApi {
    @GET("/api/people")
    Call<ArrayList<Person>> getPeople();
    @GET("/api/person/{id}")
    Call<Person> getPerson(@Path("id") long id);
    @POST("/api/add-person")
    Call<Person> addPerson(@Body Person person);
    /** @noinspection unused*/
    @PUT("/api/update-person/{id}")
    Call<Person> updatePerson(@Path("id") long id, @Body Person person);
    /** @noinspection unused*/
    @DELETE("/api/delete-person/{id}")
    Call<Person> deletePerson(@Path("id") long id);
}
