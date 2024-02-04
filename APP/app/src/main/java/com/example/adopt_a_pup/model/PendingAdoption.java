package com.example.adopt_a_pup.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.adopt_a_pup.retrofit.DogApi;
import com.example.adopt_a_pup.retrofit.PersonApi;
import com.example.adopt_a_pup.retrofit.RetrofitService;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

/** @noinspection CanBeFinal, unused, unused, FieldMayBeFinal */
public class PendingAdoption {
    private long id;
    private long userId;
    private long dogId;
    private long pendingAdoptionDateTimestamp;
    public Person person;
    public Dog dog;

    public PendingAdoption(long userId, long dogId) {
        this.userId = userId;
        this.dogId = dogId;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public Person getUser() {
        try {
            final Retrofit retrofit = new RetrofitService().getRetrofit();
            final PersonApi personApi = retrofit.create(PersonApi.class);
            Response<Person> response = personApi.getPerson(this.userId).execute();
            Person person = response.body();
            if (person!=null) {
                this.person = person;
            }
        } catch (IOException ignored) {}
        return this.person;
    }
    public long getDogId() {
        return dogId;
    }

    public Dog getDog() {
        try {
            final Retrofit retrofit = new RetrofitService().getRetrofit();
            final DogApi dogApi = retrofit.create(DogApi.class);
            Response<Dog> response = dogApi.getDog(this.dogId).execute();
            Dog dog = response.body();
            if (dog!=null) {
                this.dog = dog;
            }
        } catch (IOException ignored) {}
        return this.dog;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getPendingAdoptionDateTimestamp() {
        return this.pendingAdoptionDateTimestamp;
    }

}
