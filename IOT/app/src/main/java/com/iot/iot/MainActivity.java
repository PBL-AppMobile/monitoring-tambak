package com.iot.iot;

import static com.iot.iot.R.id.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.iot.iot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThemeFromPreferences();
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Dashboard");
        setSupportActionBar(binding.toolbar);

        getSupportFragmentManager().beginTransaction().replace(fragment, new DashboardFragment()).commit();

        binding.profileIcon.setOnClickListener(view1 -> {
            Intent profilePage = new Intent(this, ProfileActivity.class);
            startActivity(profilePage);
        });

        binding.ibDarkMode.setOnClickListener(view1 -> {
            toggleTheme();
        });

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == home_nav) {
                    selectedFragment = new DashboardFragment();
                    binding.toolbar.setTitle("Dashboard");
                } else if (item.getItemId() == jadwal_nav) {
                    selectedFragment = new JadwalFragment();
                    binding.toolbar.setTitle("Jadwal");
                } else if (item.getItemId() == riwayat_nav) {
                    selectedFragment = new RiwayatFragment();
                    binding.toolbar.setTitle("Riwayat");
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
                }
                return true;
            }
        });

    }


    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("is_dark_mode", false);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_dark_mode", !isDarkMode);
        editor.apply();

        if (!isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setThemeFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("is_dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
