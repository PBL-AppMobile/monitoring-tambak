package com.iot.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, usernameInput, passwordInput;
    private Button registerButton;
    private static final String API_SEND_URL = "http://10.2.23.178:8086/write?db=pbl_agri";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register);

        // Inisialisasi Views
        emailInput = findViewById(R.id.email_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.login_button);

        // Event Listener untuk Tombol Register
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
                sendToDb(email, username, password);
            }
        });
    }

    private void sendToDb(String email, String username, String password) {
        String API_SEND_URL = "http://10.2.23.178:8086/write?db=pbl_agri";

        // Kirim data ke API menggunakan line protocol
        OkHttpClient client = new OkHttpClient();

        // Format data dalam line protocol untuk InfluxDB
        String data = "user_registration,email=" + email + ",username=" + username + " password=\"" + password + "\"";
        RequestBody body = RequestBody.create(data, MediaType.get("text/plain"));

        // Buat request untuk mengirim data
        Request request = new Request.Builder()
                .url(API_SEND_URL)
                .post(body)
                .build();

        // Kirim request menggunakan enqueue agar tidak memblok UI
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        // Jika sukses, tampilkan pesan
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                        Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(toLogin);
                    } else {
                        // Jika gagal, tampilkan pesan error
                        Toast.makeText(RegisterActivity.this, "Gagal mengirim data: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
