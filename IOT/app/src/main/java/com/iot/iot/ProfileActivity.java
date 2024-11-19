package com.iot.iot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
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
}
