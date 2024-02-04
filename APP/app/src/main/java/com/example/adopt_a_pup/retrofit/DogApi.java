package com.example.adopt_a_pup.retrofit;

import com.example.adopt_a_pup.model.Dog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DogApi {
    /** @noinspection unused*/
    @GET("/api/is-dog-available/{id}")
    Call<Boolean> isDogAvailable(@Path("id") long id);
    @GET("/api/available-dogs")
    Call<ArrayList<Dog>> getAvailableDogs();
    @GET("/api/dogs")
    Call<ArrayList<Dog>> getAllDogs();
    @GET("/api/dog/{id}")
    Call<Dog> getDog(@Path("id") long id);
    @POST("/api/add-dog")
    Call<Dog> addDog(@Body Dog dog);
    @PUT("/api/update-dog/{id}")
    Call<Dog> updateDog(@Path(value = "id") long id, @Body Dog dog);
    @DELETE("/api/delete-dog/{id}")
    Call<Dog> deleteDog(@Path("id") long id);
}
