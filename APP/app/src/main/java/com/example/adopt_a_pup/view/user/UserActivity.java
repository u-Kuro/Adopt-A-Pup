package com.example.adopt_a_pup.view.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adopt_a_pup.MainActivity;
import com.example.adopt_a_pup.R;
import com.example.adopt_a_pup.model.Dog;
import com.example.adopt_a_pup.model.PendingAdoption;
import com.example.adopt_a_pup.model.Person;
import com.example.adopt_a_pup.retrofit.DogApi;
import com.example.adopt_a_pup.retrofit.PendingAdoptionApi;
import com.example.adopt_a_pup.retrofit.PersonApi;
import com.example.adopt_a_pup.retrofit.RetrofitService;
import com.example.adopt_a_pup.view.DogAdapter;
import com.example.adopt_a_pup.view.admin.AdminActivity;
import com.example.adopt_a_pup.view.admin.DogTabFragment;
import com.example.adopt_a_pup.view.admin.PendingAdoptionTabFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserActivity extends AppCompatActivity {
    final Retrofit retrofit = new RetrofitService().getRetrofit();
    final DogApi dogApi = retrofit.create(DogApi.class);
    final PendingAdoptionApi pendingAdoptionApi = retrofit.create(PendingAdoptionApi.class);
    final PersonApi personApi = retrofit.create(PersonApi.class);
    ListView availableDogsListView;
    ArrayList<Dog> availableDogs;
    DogAdapter availableDogsAdapter;
    TextView userStatus;
    Person currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUser(3000);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        userStatus = findViewById(R.id.user_status);
        availableDogsListView = findViewById(R.id.available_dogs_list);

        availableDogsListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (availableDogsAdapter == null) return;
            Dog dog = availableDogsAdapter.getItem(i);
            adoptDogBottomDialog(dog);
        });
        loadAllAvailableDogs();

        ImageView profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View bottomSheet = inflater.inflate(R.layout.admin_user_drawer, null);
            dialog.setContentView(bottomSheet);

            LinearLayout userButton = dialog.findViewById(R.id.login_as_user_button);
            if (userButton!=null) {
                userButton.setVisibility(View.GONE);
            }
            LinearLayout adminButton = dialog.findViewById(R.id.login_as_admin_button);

            if (adminButton==null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                adminButton.setOnClickListener(view1 ->{
                    Intent intent = new Intent(this, AdminActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
                dialog.show();
            }
        });

        ImageView contact = findViewById(R.id.contact);
        contact.setOnClickListener(view -> {
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onResume();
    }

    Timer timer;
    public void initUser(int delay) {
        if (currentUser!=null) return;
        if (timer != null) {
            try {
                timer.cancel();
                timer = new Timer();
            } catch (Exception ignored) {}
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Response<ArrayList<Person>> responsePeople = personApi.getPeople().execute();
                        ArrayList<Person> people = responsePeople.body();
                        if (people == null || people.size() == 0) {
                            Response<Person> response = personApi.addPerson(new Person("dummy", "dummy", "dummy@gmail.com", "")).execute();
                            currentUser = response.body();
                            if (currentUser != null) {
                                new Handler(Looper.getMainLooper()).post(() -> userStatus.setVisibility(View.GONE));
                            } else {
                                initUser(3000);
                            }
                        } else {
                            currentUser = people.get(0);
                            new Handler(Looper.getMainLooper()).post(() -> userStatus.setVisibility(View.GONE));
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE, "Error Occurred", ex);
                        initUser(3000);
                    }
                }
            }, delay);
        } else {
            timer = new Timer();
            initUser(0);
        }
    }

    public void loadAllAvailableDogs() {
        dogApi.getAvailableDogs().enqueue(new Callback<ArrayList<Dog>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Dog>> call, @NonNull Response<ArrayList<Dog>> response) {
                ArrayList<Dog> loadedDogs = response.body();
                if (loadedDogs != null) {
                    availableDogs = loadedDogs;
                    if (availableDogsAdapter ==null) {
                        availableDogsAdapter = new DogAdapter(UserActivity.this, R.layout.dog_card, availableDogs);
                        availableDogsListView.setAdapter(availableDogsAdapter);
                    } else {
                        availableDogsAdapter.notifyDataSetChanged();
                    }
                    PendingAdoptionTabFragment pendingAdoptionTabFragment = PendingAdoptionTabFragment.getInstanceActivity();
                    if (pendingAdoptionTabFragment!=null) {
                        pendingAdoptionTabFragment.loadAllPendingAdoptions();
                    }
                } else {
                    String error = "Error: The response body contains a wrong datatype!";
                    showToast(Toast.makeText(UserActivity.this, error, Toast.LENGTH_SHORT));
                    Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Dog>> call, @NonNull Throwable t) {
                showToast(Toast.makeText(UserActivity.this,"Failed to get all dogs!", Toast.LENGTH_SHORT));
                Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",t);
            }
        });
    }


    public void adoptDogBottomDialog(Dog dog) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View bottomSheet = inflater.inflate(R.layout.adopt_dog_drawer, null);
        dialog.setContentView(bottomSheet);

        LinearLayout adoptDogButton = dialog.findViewById(R.id.adopt_dog_button);

        if (adoptDogButton==null || dog==null) {
            showToast(Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT));
            return;
        }

        adoptDogButton.setOnClickListener(view -> {
            if (currentUser==null) {
                showToast(Toast.makeText(this, "User not found, Please wait...", Toast.LENGTH_SHORT));
//                initUser(3000);
                return;
            }
            long adoptingDogId = dog.getId();
            long userId = currentUser.getId();
            PendingAdoption pendingAdoption = new PendingAdoption(userId, adoptingDogId);
            pendingAdoptionApi.addPendingAdoption(pendingAdoption).enqueue(new Callback<PendingAdoption>() {
                @Override
                public void onResponse(@NonNull Call<PendingAdoption> call, @NonNull Response<PendingAdoption> response) {
                    PendingAdoption addedAdoption = response.body();
                    if (addedAdoption!=null
                        && addedAdoption.getUserId()==userId
                        && addedAdoption.getDogId()==adoptingDogId
                        && addedAdoption.getDogId()==adoptingDogId
                    ) {
                        int foundIndex = -1;
                        for (int i = 0; i < availableDogs.size(); i++) {
                            if (availableDogs.get(i).getId() == adoptingDogId) {
                                foundIndex = i;
                                break;
                            }
                        }
                        if (foundIndex>=0) {
                            availableDogs.remove(foundIndex);
                            if (availableDogsAdapter==null) {
                                availableDogsAdapter = new DogAdapter(UserActivity.this, R.layout.dog_card, availableDogs);
                                availableDogsListView.setAdapter(availableDogsAdapter);
                            } else {
                                availableDogsAdapter.notifyDataSetChanged();
                            }
                        }
                        dialog.dismiss();
                        showToast(Toast.makeText(UserActivity.this, "Successfully submitted a pending adoption", Toast.LENGTH_SHORT));
                    } else {
                        showToast(Toast.makeText(UserActivity.this, "Failed to submit adoption", Toast.LENGTH_SHORT));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PendingAdoption> call, @NonNull Throwable t) {
                    showToast(Toast.makeText(UserActivity.this, "Failed to submit adoption", Toast.LENGTH_SHORT));
                    Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE, "Error Occurred", t);
                }
            });
        });


        dialog.show();
    }

    Toast shownToast;
    public void showToast(Toast toast) {
        if (shownToast!=null) {
            shownToast.cancel();
        }
        shownToast = toast;
        shownToast.show();
    }
}
