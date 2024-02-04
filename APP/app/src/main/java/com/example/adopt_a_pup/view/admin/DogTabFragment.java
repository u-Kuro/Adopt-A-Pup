package com.example.adopt_a_pup.view.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adopt_a_pup.R;
import com.example.adopt_a_pup.model.Dog;
import com.example.adopt_a_pup.retrofit.DogApi;
import com.example.adopt_a_pup.retrofit.RetrofitService;
import com.example.adopt_a_pup.view.DogAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DogTabFragment extends Fragment {
    public static WeakReference<DogTabFragment> weakActivity;
    private Context context;
    final Retrofit retrofit = new RetrofitService().getRetrofit();
    final DogApi dogApi = retrofit.create(DogApi.class);
    ArrayList<Dog> dogs;
    DogAdapter dogAdapter;
    ListView dogListView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        weakActivity = new WeakReference<>(DogTabFragment.this);
        View dogView = inflater.inflate(R.layout.dog_tab_fragment, container, false);
        context = requireContext();
        dogListView = dogView.findViewById(R.id.dogList);
        FloatingActionButton addDog = dogView.findViewById(R.id.saveDog);

        addDog.setOnClickListener((v) -> saveDogBottomDialog(null));

        dogListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (dogAdapter == null) return;
            Dog dog = dogAdapter.getItem(i);
            saveDogBottomDialog(dog);
        });
        loadAllDogs();
        return dogView;
    }

    public static DogTabFragment getInstanceActivity() {
        if (weakActivity != null) {
            return weakActivity.get();
        } else {
            return null;
        }
    }

    public void loadAllDogs() {
        dogApi.getAllDogs().enqueue(new Callback<ArrayList<Dog>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Dog>> call, @NonNull Response<ArrayList<Dog>> response) {
                ArrayList<Dog> loadedDogs = response.body();
                if (loadedDogs != null) {
                    dogs = loadedDogs;
                    if (dogAdapter==null) {
                        dogAdapter = new DogAdapter(context, R.layout.dog_card, dogs);
                        dogListView.setAdapter(dogAdapter);
                    } else {
                        dogAdapter.notifyDataSetChanged();
                    }
                    PendingAdoptionTabFragment pendingAdoptionTabFragment = PendingAdoptionTabFragment.getInstanceActivity();
                    if (pendingAdoptionTabFragment!=null) {
                        pendingAdoptionTabFragment.loadAllPendingAdoptions();
                    }
                } else {
                    String error = "Error: The response body contains a wrong datatype!";
                    showToast(Toast.makeText(context, error, Toast.LENGTH_SHORT));
                    Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Dog>> call, @NonNull Throwable t) {
                showToast(Toast.makeText(context,"Failed to get all dogs!", Toast.LENGTH_SHORT));
                Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE,"Error Occurred",t);
            }
        });
    }


    ImageView editDogImage;
    ImageView editImage;
    String dogName;
    String dogBreed;
    Calendar dogCalendar;
    byte[] savedDogImage;
    final ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri==null) return;
                InputStream inputStream;
                try {
                    inputStream = context.getContentResolver().openInputStream(uri);
                    if (inputStream!=null) {
                        savedDogImage = getBytes(inputStream);
                        if (editDogImage!=null) {
                            Bitmap addedDogBitmap = BitmapFactory.decodeByteArray(savedDogImage, 0, savedDogImage.length);
                            editDogImage.setImageBitmap(addedDogBitmap);
                            if (editImage!=null) {
                                editImage.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    showToast(Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT));
                }
            });
    boolean dogHasBeenAdded;
    private void saveDogBottomDialog(Dog updateDog) {
        dogHasBeenAdded = false;

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View bottomSheet = inflater.inflate(R.layout.manage_dog_drawer, null);
        dialog.setContentView(bottomSheet);

        EditText editDogName = dialog.findViewById(R.id.dogName);
        EditText editDogBreed = dialog.findViewById(R.id.dogBreed);
        EditText editDogDateOfBirth = dialog.findViewById(R.id.dogDateOfBirth);
        editDogImage = dialog.findViewById(R.id.dogImage);
        editImage = dialog.findViewById(R.id.imageEditIcon);
        RelativeLayout imageContainer = dialog.findViewById(R.id.imageContainer);
        ImageView saveDogIcon = dialog.findViewById(R.id.saveDog);
        ImageView deleteDogIcon = dialog.findViewById(R.id.deleteDog);

        if (editDogDateOfBirth==null
                || editDogName==null
                || editDogBreed==null
                || editDogImage==null
                || editImage==null
                || imageContainer==null
                || saveDogIcon==null
                || deleteDogIcon==null
        ) {
            showToast(Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT));
            return;
        }

        if (updateDog!=null) {
            dialog.setOnDismissListener(dialogInterface -> {
                dogName = "";
                dogBreed = "";
                dogCalendar = null;
                savedDogImage = null;
            });
            saveDogIcon.setImageResource(R.drawable.check_white);
            String updateDogName = updateDog.getName();
            if (updateDogName != null) {
                editDogName.setText(updateDogName);
            }

            String updateDogBreed = updateDog.getBreed();
            if (updateDogBreed != null) {
                editDogBreed.setText(updateDogBreed);
            }

            dogCalendar = Calendar.getInstance();
            try {
                long updateDogBirthTimestamp = updateDog.getBirthTimestamp();
                dogCalendar.setTimeInMillis(updateDogBirthTimestamp);
                String myFormat = "MMMM dd, yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String dateString = dateFormat.format(dogCalendar.getTime());
                editDogDateOfBirth.setText(dateString);
            } catch (Exception ignored) {}

            byte[] updateDogImage = updateDog.getImage();
            if (updateDogImage != null) {
                savedDogImage = updateDogImage;
                Bitmap updateDogBitmap = BitmapFactory.decodeByteArray(savedDogImage, 0, savedDogImage.length);
                editDogImage.setImageBitmap(updateDogBitmap);
                editImage.setVisibility(View.GONE);
            } else {
                editImage.setVisibility(View.VISIBLE);
            }

            deleteDogIcon.setVisibility(View.VISIBLE);
            deleteDogIcon.setOnClickListener(view -> new AlertDialog.Builder(context)
                    .setTitle("Delete record")
                    .setMessage("Are you sure you want to delete this record?")
                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> dogApi.deleteDog(updateDog.getId()).enqueue(new Callback<Dog>() {
                        @Override
                        public void onResponse(@NonNull Call<Dog> call, @NonNull Response<Dog> response) {
                            Dog deletedDog = response.body();
                            if (deletedDog!=null) {
                                int foundIndex = -1;
                                long deletedDogId = deletedDog.getId();
                                for (int i = 0; i < dogs.size(); i++) {
                                    if (dogs.get(i).getId() == deletedDogId) {
                                        foundIndex = i;
                                        break;
                                    }
                                }
                                if (foundIndex>=0) {
                                    dogs.remove(foundIndex);
                                    if (dogAdapter==null) {
                                        dogAdapter = new DogAdapter(context, R.layout.dog_card, dogs);
                                        dogListView.setAdapter(dogAdapter);
                                    } else {
                                        dogAdapter.notifyDataSetChanged();
                                    }
                                    PendingAdoptionTabFragment pendingAdoptionTabFragment = PendingAdoptionTabFragment.getInstanceActivity();
                                    if (pendingAdoptionTabFragment!=null) {
                                        pendingAdoptionTabFragment.dogDeleted(deletedDogId);
                                    }
                                }
                                dialog.dismiss();
                                showToast(Toast.makeText(context, "Record has been deleted", Toast.LENGTH_SHORT));
                            } else {
                                showToast(Toast.makeText(context, "Failed to delete the dog's record", Toast.LENGTH_SHORT));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Dog> call, @NonNull Throwable t) {
                            showToast(Toast.makeText(context, "Failed to delete the dog's record", Toast.LENGTH_SHORT));
                            Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE, "Error Occurred", t);
                        }
                    }))
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show());
        } else {
            dialog.setOnDismissListener(dialogInterface -> {
                if (dogHasBeenAdded) {
                    dogHasBeenAdded = false;
                    dogName = "";
                    dogBreed = "";
                    dogCalendar = null;
                    savedDogImage = null;
                } else {
                    dogName = editDogName.getText().toString();
                    dogBreed = editDogBreed.getText().toString();
                    boolean hasNoDateOfBirth = editDogDateOfBirth.getText().toString().equals("");
                    if (hasNoDateOfBirth) {
                        dogCalendar = null;
                    }
                }
            });

            deleteDogIcon.setVisibility(View.GONE);
            saveDogIcon.setImageResource(R.drawable.add_white);
            if (dogName != null) {
                editDogName.setText(dogName);
            }

            if (dogBreed != null) {
                editDogBreed.setText(dogBreed);
            }

            if (dogCalendar != null) {
                String myFormat = "MMMM dd, yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String dateString = dateFormat.format(dogCalendar.getTime());
                editDogDateOfBirth.setText(dateString);
            } else {
                dogCalendar = Calendar.getInstance();
            }

            if (savedDogImage != null) {
                Bitmap addedDogBitmap = BitmapFactory.decodeByteArray(savedDogImage, 0, savedDogImage.length);
                editDogImage.setImageBitmap(addedDogBitmap);
                editImage.setVisibility(View.GONE);
            } else {
                editImage.setVisibility(View.VISIBLE);
            }
        }

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            dogCalendar.set(Calendar.YEAR, year);
            dogCalendar.set(Calendar.MONTH,month);
            dogCalendar.set(Calendar.DAY_OF_MONTH,day);
            String myFormat = "MMMM dd, yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            String dateString = dateFormat.format(dogCalendar.getTime());
            editDogDateOfBirth.setText(dateString);
        };

        editDogDateOfBirth.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, date,
                    dogCalendar.get(Calendar.YEAR),
                    dogCalendar.get(Calendar.MONTH),
                    dogCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        imageContainer.setOnClickListener(view -> getImage.launch("image/*"));

        saveDogIcon.setOnClickListener(View-> {
            dogName = editDogName.getText().toString();
            if (dogName.equals("")) {
                showToast(Toast.makeText(context,"Please add the dog's name", Toast.LENGTH_SHORT));
                return;
            }
            dogBreed = editDogBreed.getText().toString();
            if (dogBreed.equals("")) {
                showToast(Toast.makeText(context,"Please add the dog's breed", Toast.LENGTH_SHORT));
                return;
            }

            boolean hasNoDateOfBirth = editDogDateOfBirth.getText().toString().equals("");
            if (dogCalendar==null || hasNoDateOfBirth) {
                showToast(Toast.makeText(context,"Please add the dog's birthday", Toast.LENGTH_SHORT));
                return;
            }

            if (savedDogImage ==null) {
                showToast(Toast.makeText(context,"Please add an image for the dog", Toast.LENGTH_SHORT));
                return;
            }

            long dogDateOfBirthTimestamp = dogCalendar.getTimeInMillis();

            showToast(Toast.makeText(context,"Please wait", Toast.LENGTH_SHORT));
            if (updateDog!=null) {
                updateDog.setName(dogName);
                updateDog.setBreed(dogBreed);
                updateDog.setBirthTimestamp(dogDateOfBirthTimestamp);
                updateDog.setImage(savedDogImage);

                dogApi.updateDog(updateDog.getId(), updateDog).enqueue(new Callback<Dog>() {
                    @Override
                    public void onResponse(@NonNull Call<Dog> call, @NonNull Response<Dog> response) {
                        Dog dog = response.body();
                        if (dog!=null) {
                            int foundIndex = -1;
                            for (int i = 0; i < dogs.size(); i++) {
                                if (dogs.get(i).getId() == dog.getId()) {
                                    foundIndex = i;
                                    break;
                                }
                            }
                            if (foundIndex>=0) {
                                dogs.set(foundIndex, dog);
                            } else {
                                dogs.add(dog);
                            }
                            if (dogAdapter==null) {
                                dogAdapter = new DogAdapter(context, R.layout.dog_card, dogs);
                                dogListView.setAdapter(dogAdapter);
                            } else {
                                dogAdapter.notifyDataSetChanged();
                            }
                            PendingAdoptionTabFragment pendingAdoptionTabFragment = PendingAdoptionTabFragment.getInstanceActivity();
                            if (pendingAdoptionTabFragment!=null) {
                                pendingAdoptionTabFragment.updateDogs();
                            }
                            dialog.dismiss();
                            showToast(Toast.makeText(context, "Record has been updated", Toast.LENGTH_SHORT));
                        } else {
                            showToast(Toast.makeText(context, "Failed to update the dog's record", Toast.LENGTH_SHORT));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Dog> call, @NonNull Throwable t) {
                        showToast(Toast.makeText(context, "Failed to update the dog's record", Toast.LENGTH_SHORT));
                        Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE, "Error Occurred", t);
                    }
                });
            } else {
                Dog addedDog = new Dog(dogName, dogBreed, dogDateOfBirthTimestamp, savedDogImage);

                dogApi.addDog(addedDog).enqueue(new Callback<Dog>() {
                    @Override
                    public void onResponse(@NonNull Call<Dog> call, @NonNull Response<Dog> response) {
                        Dog dog = response.body();
                        if (dog!=null) {
                            dogs.add(dog);
                            if (dogAdapter==null) {
                                dogAdapter = new DogAdapter(context, R.layout.dog_card, dogs);
                                dogListView.setAdapter(dogAdapter);
                            } else {
                                dogAdapter.notifyDataSetChanged();
                            }
                            dogHasBeenAdded = true;
                            dialog.dismiss();
                            showToast(Toast.makeText(context, "Dog has been added", Toast.LENGTH_SHORT));
                        } else {
                            showToast(Toast.makeText(context, "Failed to add the dog", Toast.LENGTH_SHORT));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Dog> call, @NonNull Throwable t) {
                        showToast(Toast.makeText(context, "Failed to add the dog", Toast.LENGTH_SHORT));
                        Logger.getLogger(DogTabFragment.class.getName()).log(Level.SEVERE, "Error Occurred", t);
                    }
                });
            }
        });

        dialog.show();
    }

    public void deleteDog(Dog dog) {
        int foundIndex = -1;
        for (int i = 0; i < dogs.size(); i++) {
            if (dogs.get(i).getId() == dog.getId()) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex>=0) {
            dogs.set(foundIndex, dog);
        } else {
            dogs.add(dog);
        }
        if (dogAdapter==null) {
            dogAdapter = new DogAdapter(context, R.layout.dog_card, dogs);
            dogListView.setAdapter(dogAdapter);
        } else {
            dogAdapter.notifyDataSetChanged();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void showToast(Toast toast) {
        AdminActivity adminActivity = AdminActivity.getInstanceActivity();
        if (adminActivity != null) {
            adminActivity.showToast(toast);
        }
    }
}
