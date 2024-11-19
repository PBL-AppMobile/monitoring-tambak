package com.iot.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Use Shared Preferences to check if onboarding is completed
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isOnboardingCompleted = sharedPreferences.getBoolean("OnboardingCompleted", false);
        boolean isFirstRun = sharedPreferences.getBoolean("IsFirstRun", true);

        // Show splash screen for 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent;
            if (isFirstRun) {
                // If it's the first run, show NavigationActivity for onboarding
                intent = new Intent(SplashActivity.this, NavigationActivity.class);

                // Set IsFirstRun to false so that onboarding won't be shown again
                sharedPreferences.edit().putBoolean("IsFirstRun", false).apply();
            } else if (!isOnboardingCompleted) {
                // If onboarding is not completed, show NavigationActivity
                intent = new Intent(SplashActivity.this, NavigationActivity.class);
            } else {
                // If onboarding is completed, go directly to LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // Close SplashActivity so the user can't return to it
        }, 3000); // 3000 milliseconds (3 seconds)
    }

    public void resetPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IsFirstRun", true);
        editor.putBoolean("OnboardingCompleted", false);
        editor.apply();
    }
}
