package com.iot.iot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import com.iot.iot.databinding.ActivityProfilesBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfilesBinding binding;
    private static final String THEME_PREFS = "theme_prefs";
    private static final String IS_DARK_MODE = "is_dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ibDarkMode.setOnClickListener(view1 -> {
            toggleTheme();
        });

        ImageView btn_back = findViewById(R.id.clickback);

        btn_back.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack(); // Kembali ke Fragment sebelumnya
            } else {
                // Jika tidak ada Fragment di back stack, Anda bisa menutup Activity atau melakukan tindakan lain
                finish(); // Menutup Activity saat tidak ada Fragment yang tersisa
            }
        });

    }

    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean(IS_DARK_MODE, false);

        preferences.edit()
                .putBoolean(IS_DARK_MODE, !isDarkMode)
                .apply();

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES
        );
    }


}
