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
    private static final String THEME_PREFS = "theme_prefs";
    private static final String IS_DARK_MODE = "is_dark_mode";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThemeFromPreferences();
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Dashboard");
        setSupportActionBar(binding.toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new DashboardFragment()).commit();

        binding.profileIcon.setOnClickListener(view1 -> {
            Intent profilePage = new Intent(this, ProfileActivity.class);
            startActivity(profilePage);
        });



        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";

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
                    binding.toolbar.setTitle(title);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public void setThemeFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean(IS_DARK_MODE, false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
