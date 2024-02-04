package com.example.adopt_a_pup.view;

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

import java.util.ArrayList;
import java.util.StringJoiner;

public class DogAdapter extends ArrayAdapter<Dog> {
    final Context mContext;
    final int mResource;
    public DogAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Dog> objects) {
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

        Dog dog = getItem(position);
        if (dog != null) {
            if (dog.getImage()!=null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(dog.getImage(), 0, dog.getImage().length);
                new Handler(Looper.getMainLooper()).post(() -> dogImage.setImageBitmap(imageBitmap));
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
        return view;
    }
}
