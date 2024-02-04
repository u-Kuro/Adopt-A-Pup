package com.example.adopt_a_pup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.adopt_a_pup.view.admin.AdminActivity;
import com.example.adopt_a_pup.view.user.UserActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button adminButton = findViewById(R.id.goToAdmin);
        Button userButton = findViewById(R.id.goToUser);
        adminButton.setOnClickListener((v)->{
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.remove);
        });
        userButton.setOnClickListener((v)->{
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.remove);
        });
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.none, R.anim.fade_out);
        super.onResume();
    }
}