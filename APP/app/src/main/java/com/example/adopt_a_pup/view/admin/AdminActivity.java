package com.example.adopt_a_pup.view.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.adopt_a_pup.MainActivity;
import com.example.adopt_a_pup.R;
import com.example.adopt_a_pup.view.user.UserActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;

public class AdminActivity extends AppCompatActivity {
    public static WeakReference<AdminActivity> weakActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        weakActivity = new WeakReference<>(AdminActivity.this);

        ImageView profile = findViewById(R.id.profile);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        FragmentStateAdapter adminViewAdapter = new AdminViewAdapter(this);

        viewPager.setAdapter(adminViewAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab!=null) {
                    tab.select();
                }
            }
        });


        profile.setOnClickListener(view -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View bottomSheet = inflater.inflate(R.layout.admin_user_drawer, null);
            dialog.setContentView(bottomSheet);

            LinearLayout adminButton = dialog.findViewById(R.id.login_as_admin_button);
            if (adminButton!=null) {
                adminButton.setVisibility(View.GONE);
            }
            LinearLayout userButton = dialog.findViewById(R.id.login_as_user_button);

            if (userButton==null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                userButton.setOnClickListener(view1 ->{
                    Intent intent = new Intent(this, UserActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onResume();
    }

    public static AdminActivity getInstanceActivity() {
        if (weakActivity != null) {
            return weakActivity.get();
        } else {
            return null;
        }
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
