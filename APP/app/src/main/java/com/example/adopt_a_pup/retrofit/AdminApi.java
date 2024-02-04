package com.example.adopt_a_pup.retrofit;

import com.example.adopt_a_pup.model.Admin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminApi {
    /** @noinspection unused*/
    @GET("/api/is-admin/{userid}")
    Boolean isAdmin(@Path("userid") long userid);
    /** @noinspection unused*/
    @POST("/api/add-admin")
    Call<Admin> addAdmin(@Body Admin admin);
    /** @noinspection unused*/
    @DELETE("/api/delete-admin/{userid}")
    Call<Admin> deleteAdmin(@Path("userid") long userid);
}
