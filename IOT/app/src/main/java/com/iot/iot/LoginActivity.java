package com.iot.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;

    private static final String API_QUERY_URL = "http://10.2.23.178:8086/query?db=pbl_agri&q=SELECT+*+FROM+user_registration";

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

        // Cek status login
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Langsung pindah ke MainActivity jika sudah login
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.username_input);
        etPassword = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.login_button);
        btnRegister = findViewById(R.id.register_button);

        btnRegister.setOnClickListener(view -> {
            Intent regisPage = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(regisPage);
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_QUERY_URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray valuesArray = jsonObject
                                .getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONArray("series")
                                .getJSONObject(0)
                                .getJSONArray("values");

                        boolean isValid = false;

                        for (int i = 0; i < valuesArray.length(); i++) {
                            JSONArray userData = valuesArray.getJSONArray(i);
                            String dbUsername = userData.getString(3);
                            String dbPassword = userData.getString(2);

                            if (dbUsername.equals(username) && dbPassword.equals(password)) {
                                isValid = true;
                                break;
                            }
                        }

                        if (isValid) {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                                // Simpan status login di SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("username", username);
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(LoginActivity.this, "Username atau password salah", Toast.LENGTH_SHORT).show()
                            );
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Kesalahan parsing data", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Gagal mendapatkan data dari server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
