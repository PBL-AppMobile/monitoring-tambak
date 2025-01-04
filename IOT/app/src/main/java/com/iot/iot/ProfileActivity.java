package com.iot.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import com.iot.iot.databinding.ActivityProfilesBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfilesBinding binding;
    private static final String THEME_PREFS = "theme_prefs";
    private static final String IS_DARK_MODE = "is_dark_mode";
    private static final String LOGIN_PREFS = "UserPrefs";
    private static final String IS_LOGGED_IN = "is_logged_in";

    private boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.BLACK);
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        super.onCreate(savedInstanceState);

        binding = ActivityProfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences themePreferences = getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        isDarkMode = themePreferences.getBoolean(IS_DARK_MODE, false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        binding.ibDarkMode.setOnClickListener(view -> {
            isDarkMode = !isDarkMode;

            themePreferences.edit()
                    .putBoolean(IS_DARK_MODE, isDarkMode)
                    .apply();

            AppCompatDelegate.setDefaultNightMode(
                    isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        ImageView btnBack = findViewById(R.id.clickback);
        btnBack.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                finish();
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> logout());
    }

    private void logout() {
        // Hapus data login dari SharedPreferences
        SharedPreferences loginPreferences = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        loginPreferences.edit()
                .clear()
                .apply();

        // Arahkan pengguna kembali ke halaman login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
