package com.iot.iot;

import android.annotation.SuppressLint;
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
    private boolean isDarkMode; // Status tema saat ini

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mengatur Binding
        binding = ActivityProfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mendapatkan status tema dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        isDarkMode = preferences.getBoolean(IS_DARK_MODE, false);

        // Mengatur tema saat aplikasi dibuka
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Tombol Dark Mode Toggle
        binding.ibDarkMode.setOnClickListener(view -> {
            isDarkMode = !isDarkMode; // Toggle status tema

            // Simpan status ke SharedPreferences
            preferences.edit()
                    .putBoolean(IS_DARK_MODE, isDarkMode)
                    .apply();

            // Terapkan tema
            AppCompatDelegate.setDefaultNightMode(
                    isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Tombol Kembali
        ImageView btn_back = findViewById(R.id.clickback);
        btn_back.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack(); // Kembali ke Fragment sebelumnya
            } else {
                finish(); // Menutup Activity saat tidak ada Fragment yang tersisa
            }
        });
    }
}
