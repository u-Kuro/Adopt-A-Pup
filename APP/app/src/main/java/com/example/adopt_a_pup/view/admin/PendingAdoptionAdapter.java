package com.example.adopt_a_pup.view.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.adopt_a_pup.R;
import com.example.adopt_a_pup.model.Dog;
import com.example.adopt_a_pup.model.PendingAdoption;
import com.example.adopt_a_pup.model.Person;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringJoiner;

public class PendingAdoptionAdapter extends ArrayAdapter<PendingAdoption> {
    final Context mContext;
    final int mResource;
    public PendingAdoptionAdapter(@NonNull Context context, int resource, @NonNull ArrayList<PendingAdoption> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(mResource, parent, false);
        }
        ImageView dogImage = view.findViewById(R.id.dogImage);
        TextView dogName = view.findViewById(R.id.dogName);
        TextView dogBreedAndAge = view.findViewById(R.id.dogBreedAndAge);

        // ImageView personImage = view.findViewById(R.id.personImage);
        TextView personName = view.findViewById(R.id.personName);
        TextView personEmail = view.findViewById(R.id.personEmail);

        TextView pendingAdoptionDate = view.findViewById(R.id.pending_adoption_date);

        PendingAdoption pendingAdoption = getItem(position);
        if (pendingAdoption != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Calendar pendingAdoptionCalendar = Calendar.getInstance();
                long pendingAdoptionTimestamp = pendingAdoption.getPendingAdoptionDateTimestamp();
                pendingAdoptionCalendar.setTimeInMillis(pendingAdoptionTimestamp);
                String myFormat = "MMMM dd, yyyy hh:mm a";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                String dateString = dateFormat.format(pendingAdoptionCalendar.getTime());
                pendingAdoptionDate.setText(dateString);
            }
            System.out.println("dsadadadasda | "+pendingAdoption);
            Person currentPerson = pendingAdoption.person;
            if (currentPerson!=null) {
                updatePerson(currentPerson, personName, personEmail);
            }
            new Thread(() -> {
                Person person = pendingAdoption.getUser();
                new Handler(Looper.getMainLooper()).post(() -> updatePerson(person, personName, personEmail));
            }).start();
            Dog currentDog = pendingAdoption.dog;
            if (currentDog!=null) {
                updateDog(currentDog, dogImage, dogName, dogBreedAndAge);
            }
            new Thread(() -> {
                Dog dog = pendingAdoption.getDog();
                new Handler(Looper.getMainLooper()).post(() -> updateDog(dog, dogImage, dogName, dogBreedAndAge));
            }).start();
        }
        return view;
    }
    @SuppressLint("SetTextI18n")
    public void updatePerson(Person person, TextView personName, TextView personEmail) {
        if (person==null || personName==null || personEmail==null) return;
        // personImage.setImageBitmap();
        String fullName = person.getFullName();
        if (fullName!=null) {
            personName.setText(fullName);
        } else {
            personName.setText("NA");
        }
        String email = person.getEmail();
        if (email!=null) {
            personEmail.setText(email);
        } else {
            personEmail.setText("NA");
        }
    }
    private void updateDog(Dog dog, ImageView dogImage, TextView dogName, TextView dogBreedAndAge) {
        if (dog==null || dogImage==null || dogName==null || dogBreedAndAge==null) return;
        if (dog.getImage()!=null) {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(dog.getImage(), 0, dog.getImage().length);
            dogImage.setImageBitmap(imageBitmap);
        }
        if (dog.getName()!=null) {
            dogName.setText(dog.getName());
        } else {
            dogName.setText(R.string.na);
        }
        StringJoiner sj = new StringJoiner("  ·  ");
        String breed = dog.getBreed();
        if (breed!=null) {
            sj.add(breed);
        }
        int age = -1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            age = dog.getAge();
        }
        if (age>=0) {
            sj.add(age + " " + (age>1?"years":"year") + " old");
        }
        String dogAgeAndBreed = sj.toString();
        dogBreedAndAge.setText(dogAgeAndBreed);
    }
}
