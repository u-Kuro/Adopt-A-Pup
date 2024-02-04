package com.example.adopt_a_pup.retrofit;

import com.example.adopt_a_pup.model.PendingAdoption;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PendingAdoptionApi {
    @GET("/api/pending-adoptions")
    Call<ArrayList<PendingAdoption>> getAllPendingAdoptions();
    /** @noinspection unused*/
    @GET("/api/user-pending-adoptions/{userid}")
    Call<ArrayList<PendingAdoption>> getUserPendingAdoptions(@Path("userid") long userid);
    /** @noinspection unused*/
    @GET("/api/pending-adoption/{id}")
    Call<PendingAdoption> showPendingAdoption(@Path("id") long id);
    @POST("/api/add-pending-adoption")
    Call<PendingAdoption> addPendingAdoption(@Body PendingAdoption pendingAdoption);
    @DELETE("/api/delete-pending-adoption/{id}")
    Call<PendingAdoption> deletePendingAdoption(@Path("id") long id);
}
