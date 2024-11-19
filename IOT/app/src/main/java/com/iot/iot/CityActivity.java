package com.iot.iot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CityActivity extends AppCompatActivity {
    private TextView tv_data_time; // Hanya menyisakan satu TextView

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        // Inisialisasi TextView
        tv_data_time = findViewById(R.id.tv_data_time);

        // Panggil metode untuk mengambil data
        fetchDataFromInfluxDB();
    }

    private void fetchDataFromInfluxDB() {
        OkHttpClient client = new OkHttpClient();

        String url = "http://10.2.23.178:8086/query?q=SELECT%20*%20FROM%20sensor_data%20ORDER%20BY%20time%20DESC%20LIMIT%20100&db=pbl_agri";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("HTTP Error", "Error fetching data: " + e.getMessage());

                // Menampilkan Toast saat terjadi kesalahan
                runOnUiThread(() -> Toast.makeText(CityActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    Log.d("HTTP Response", responseData);

                    // Parsing JSON
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray resultsArray = jsonObject.getJSONArray("results");
                        JSONObject firstResult = resultsArray.getJSONObject(0);
                        JSONArray seriesArray = firstResult.getJSONArray("series");
                        JSONObject firstSeries = seriesArray.getJSONObject(0);
                        JSONArray valuesArray = firstSeries.getJSONArray("values");

                        // Loop untuk mengambil semua data yang ada di values
                        StringBuilder dataBuilder = new StringBuilder();
                        for (int i = 0; i < valuesArray.length(); i++) {
                            JSONArray dataRow = valuesArray.getJSONArray(i);

                            // Ambil data time dan value
                            String time = dataRow.getString(0);
                            String value = String.valueOf(dataRow.getDouble(1));

                            // Menyusun string dari data yang diterima
                            dataBuilder.append("Time: ").append(time).append("\n")
                                    .append("Value: ").append(value).append("\n\n");
                        }

                        final String allData = dataBuilder.toString();

                        // Update UI dengan data yang sudah diparsing
                        runOnUiThread(() -> tv_data_time.setText(allData)); // Tampilkan semua data di TextView

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON Error", "Error parsing JSON: " + e.getMessage());

                        // Menampilkan Toast saat parsing gagal
                        runOnUiThread(() -> Toast.makeText(CityActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show());
                    }

                } else {
                    final String errorResponse = response.body().string();
                    Log.e("HTTP Error", "Response not successful: " + response.code() + ", " + errorResponse);
                    runOnUiThread(() -> Toast.makeText(CityActivity.this, "Response not successful: " + response.code() + ", " + errorResponse, Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}
