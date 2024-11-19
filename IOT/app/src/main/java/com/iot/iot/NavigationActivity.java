package com.iot.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class NavigationActivity extends AppCompatActivity {

    ViewPager slideViewPager; // Untuk menampilkan slide/page
    LinearLayout dotIndicator; // Indikator titik untuk menandai slide aktif
    Button backButton, nextButton, skipButton; // Tombol Back, Next, dan Skip
    TextView[] dots; // Array untuk menyimpan indikator titik
    ViewPagerAdapter viewPagerAdapter; // Adapter untuk menghubungkan slide dengan ViewPager

    // Listener yang akan memantau perubahan halaman di ViewPager
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Kosong, tidak diperlukan fungsionalitas khusus saat halaman di-scroll
        }

        @Override
        public void onPageSelected(int position) {
            // Menentukan indikator titik yang aktif berdasarkan posisi slide
            setDotIndicator(position);

            // Menampilkan tombol Back jika posisi bukan di halaman pertama
            if (position > 0) {
                backButton.setVisibility(View.VISIBLE);
            } else {
                backButton.setVisibility(View.INVISIBLE);
            }

            // Mengubah tombol Next menjadi "Finish" jika di halaman terakhir
            if (position == 2){
                nextButton.setText("Finish");
            } else {
                nextButton.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Kosong, tidak diperlukan fungsionalitas khusus untuk perubahan status scroll
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation); // Mengatur layout untuk activity ini

        // Menghubungkan variabel tombol dengan ID dari XML
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);

        // Listener untuk tombol Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jika halaman bukan halaman pertama, maka geser ke halaman sebelumnya
                if (getItem(0) > 0) {
                    slideViewPager.setCurrentItem(getItem(-1), true);
                }
            }
        });

        // Listener untuk tombol Next
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jika belum di halaman terakhir, pindah ke halaman berikutnya
                if (getItem(0) < 2) {
                    slideViewPager.setCurrentItem(getItem(1), true);
                } else {
                    // Jika di halaman terakhir, tandai onboarding sudah selesai dengan SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("OnboardingCompleted", true);
                    editor.apply();

                    // Pindah ke getStartedActivity setelah onboarding selesai
                    Intent i = new Intent(NavigationActivity.this, GetStarted.class);
                    startActivity(i);
                    finish(); // Mengakhiri aktivitas ini
                }
            }
        });

        // Listener untuk tombol Skip
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Langsung ke MainActivity tanpa menyelesaikan onboarding
                Intent i = new Intent(NavigationActivity.this, LoginActivity.class);
                startActivity(i);
                finish(); // Mengakhiri aktivitas ini
            }
        });

        // Menghubungkan variabel slideViewPager dan dotIndicator dengan ID dari XML
        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);

        // Mengatur adapter untuk ViewPager
        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);

        // Mengatur indikator titik awal pada posisi 0 (slide pertama)
        setDotIndicator(0);

        // Menambahkan listener untuk perubahan halaman di ViewPager
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }

    // Fungsi untuk mengatur indikator titik (dot) sesuai dengan halaman aktif
    public void setDotIndicator(int position) {

        dots = new TextView[3]; // Mengatur jumlah titik untuk 3 halaman
        dotIndicator.removeAllViews(); // Menghapus semua indikator titik yang sebelumnya

        // Membuat titik indikator untuk setiap halaman
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            // Membuat titik dengan kode HTML •
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35); // Ukuran titik
            dots[i].setTextColor(getResources().getColor(R.color.grey, getApplicationContext().getTheme())); // Warna default titik
            dotIndicator.addView(dots[i]); // Menambahkan titik ke dalam layout indikator
        }
        // Mengatur warna titik aktif sesuai dengan posisi halaman
        dots[position].setTextColor(getResources().getColor(R.color.lavender, getApplicationContext().getTheme()));
    }

    // Fungsi untuk mendapatkan posisi halaman saat ini, digunakan untuk navigasi ke halaman berikut atau sebelumnya
    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}
