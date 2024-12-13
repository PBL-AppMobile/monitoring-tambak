package com.iot.iot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardFragment extends Fragment {

    private LineChart lineChartPh, lineChartSuhuAir, lineChartKualitasAir;
    private SwitchMaterial swBaru;
    private TextView tvPhLevel, tvWaterTemp, tvSaltLevel;

    private final String API_URL = "http://10.2.23.178:8086/query?q=SELECT%20*%20FROM%20sensor_data%20ORDER%20BY%20time%20DESC%20LIMIT%20100&db=pbl_agri";
    private Handler handler;
    private Runnable dataFetcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Inisialisasi LineCharts
        lineChartPh = view.findViewById(R.id.lineChartPh);
        lineChartSuhuAir = view.findViewById(R.id.lineChartSuhuAir);
        lineChartKualitasAir = view.findViewById(R.id.lineChartSalinity);

        // Inisialisasi TextViews
        tvPhLevel = view.findViewById(R.id.tv_ph_level);
        tvWaterTemp = view.findViewById(R.id.tv_water_temp);
        tvSaltLevel = view.findViewById(R.id.tv_salt_level);

        // Inisialisasi SwitchMaterial
        swBaru = view.findViewById(R.id.swbaru);

        // Inisialisasi Handler untuk polling data setiap detik
        handler = new Handler();
        dataFetcher = this::fetchDataFromApi;

        // Mulai polling data
        handler.post(dataFetcher);
        swBaru.setOnCheckedChangeListener(null); // Hentikan sementara listener

// Ambil status terakhir yang disimpan di SharedPreferences atau dari API
        boolean isServoOn = getSwitchStatus();

// Hanya set status jika statusnya berbeda
        if (swBaru.isChecked() != isServoOn) {
            swBaru.setChecked(isServoOn); // Set status switch sesuai dengan status servo terakhir
        }

        swBaru.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendServoCommand(isChecked ? 1 : 0);  // Ganti dengan logika yang sesuai
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hentikan polling data saat fragment dihancurkan
        if (handler != null) {
            handler.removeCallbacks(dataFetcher);
        }
    }

    private void fetchDataFromApi() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        tvPhLevel.setText("Gagal memuat data")
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        parseAndDisplayData(jsonData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Ambil status terakhir servo dari API dan update status Switch
        client.newCall(new Request.Builder().url(API_URL_SERVO).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Gagal memuat status servo", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Ambil data JSON
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray valuesArray = jsonObject
                                .getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONArray("series")
                                .getJSONObject(0)
                                .getJSONArray("values");

                        // Ambil status terakhir (values[1] adalah value dari servo)
                        if (valuesArray.length() > 0) {
                            // Ambil nilai terakhir (status servo)
                            int lastCommand = valuesArray.getJSONArray(0).getInt(1); // Mengambil nilai kedua dari array

                            // Simpan status ke SharedPreferences setelah mendapatkan status terakhir dari API
                            saveSwitchStatus(lastCommand == 1);

                            // Update tampilan Switch berdasarkan status terakhir
                            requireActivity().runOnUiThread(() -> {
                                swBaru.setOnCheckedChangeListener(null); // Lepaskan listener sementara
                                swBaru.setChecked(lastCommand == 1); // Set checked jika 1 (ON)
                                swBaru.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    sendServoCommand(isChecked ? 1 : 0); // Kirim nilai baru ke servo jika switch berubah
                                });
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void parseAndDisplayData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray valuesArray = jsonObject
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("series")
                    .getJSONObject(0)
                    .getJSONArray("values");

            ArrayList<Entry> phEntries = new ArrayList<>();
            ArrayList<Entry> suhuEntries = new ArrayList<>();
            ArrayList<Entry> salinityEntries = new ArrayList<>();


            for (int i = 0; i < valuesArray.length(); i++) {
                JSONArray dataRow = valuesArray.getJSONArray(i);
                float ph = (float) dataRow.getDouble(1);
                float salinity = (float) dataRow.getDouble(2);
                float suhu = (float) dataRow.getDouble(4);
                int servo = dataRow.getInt(3);

                phEntries.add(new Entry(i, ph));
                suhuEntries.add(new Entry(i, suhu));
                salinityEntries.add(new Entry(i, salinity));


            }

            requireActivity().runOnUiThread(() -> {
                updateLineChart(lineChartPh, phEntries, "pH Level", Color.BLUE);
                updateLineChart(lineChartSuhuAir, suhuEntries, "Suhu Air", Color.RED);
                updateLineChart(lineChartKualitasAir, salinityEntries, "Kadar Garam", Color.GREEN);

                tvPhLevel.setText(String.format("pH: %.2f", phEntries.get(0).getY()));
                tvWaterTemp.setText(String.format("Temp: %.2f°C", suhuEntries.get(0).getY()));
                tvSaltLevel.setText(String.format("Salt: %.2f%%", salinityEntries.get(0).getY()));


            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLineChart(LineChart chart, ArrayList<Entry> entries, String label, int color) {
        // Konfigurasi data set
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color); // Warna garis
        dataSet.setLineWidth(2f); // Lebar garis
        dataSet.setCircleColor(color); // Warna lingkaran
        dataSet.setCircleRadius(5f); // Ukuran lingkaran
        dataSet.setValueTextColor(color); // Warna nilai teks pada data
        dataSet.setValueTextSize(10f); // Ukuran teks nilai

        // Tambahkan data ke grafik
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Konfigurasi legend
        Legend legend = chart.getLegend();
        legend.setTextColor(color); // Warna teks legend
        legend.setTextSize(12f); // Ukuran teks legend

        // Atur properti chart
        chart.getDescription().setEnabled(false); // Nonaktifkan deskripsi default
        chart.getXAxis().setTextColor(Color.DKGRAY); // Warna sumbu X
        chart.getAxisLeft().setTextColor(Color.DKGRAY); // Warna sumbu Y kiri
        chart.getAxisRight().setTextColor(Color.DKGRAY); // Warna sumbu Y kanan

        chart.invalidate(); // Refresh chart
    }

    // Menyimpan status switch ke SharedPreferences
    private void saveSwitchStatus(boolean isChecked) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ServoPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("switchStatus", isChecked); // Menyimpan status switch
        editor.apply();
    }

    // Mengambil status switch dari SharedPreferences
    private boolean getSwitchStatus() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ServoPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("switchStatus", false); // Default ke false jika tidak ada status
    }

    private final String API_URL_SERVO = "http://10.2.23.178:8086/query?q=SELECT%20*%20FROM%20servo_data%20ORDER%20BY%20time%20DESC%20LIMIT%201&db=pbl_agri";

    private void sendServoCommand(int command) {
        String API_SEND_URL = "http://10.2.23.178:8086/write?db=pbl_agri";


        // Kirim data ke API untuk mengontrol servo
        OkHttpClient client = new OkHttpClient();

        // Format data dalam protokol line untuk InfluxDB
        String data = "servo_data value=" + command;
        RequestBody body = RequestBody.create(data, MediaType.get("text/plain"));

        // Buat request untuk mengirim perintah baru ke servo
        Request request = new Request.Builder()
                .url(API_SEND_URL)
                .post(body)
                .build();

        // Kirim request untuk mengontrol servo
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        // Jika sukses, tampilkan pesan
                        Toast.makeText(getContext(), "Perintah servo berhasil dikirim", Toast.LENGTH_SHORT).show();
                        // Simpan status ke SharedPreferences setelah berhasil mengirim perintah
                        saveSwitchStatus(command == 1);
                    } else {
                        // Jika gagal, tampilkan pesan error
                        Toast.makeText(getContext(), "Gagal mengirim perintah: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Ambil status terakhir servo dari API untuk menentukan status Switch
        client.newCall(new Request.Builder().url(API_URL_SERVO).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Gagal memuat status servo", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Ambil data JSON
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray valuesArray = jsonObject
                                .getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONArray("series")
                                .getJSONObject(0)
                                .getJSONArray("values");

                        // Ambil status terakhir (values[1] adalah value dari servo)
                        if (valuesArray.length() > 0) {
                            // Ambil nilai terakhir (status servo)
                            int lastCommand = valuesArray.getJSONArray(0).getInt(1); // Mengambil nilai kedua dari array

                            // Simpan status ke SharedPreferences setelah mendapatkan status terakhir dari API
                            saveSwitchStatus(lastCommand == 1);

                            // Update tampilan Switch berdasarkan status terakhir
                            requireActivity().runOnUiThread(() -> {
                                swBaru.setOnCheckedChangeListener(null); // Lepaskan listener sementara
                                swBaru.setChecked(lastCommand == 1); // Set checked jika 1 (ON)
                                swBaru.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    sendServoCommand(isChecked ? 1 : 0); // Kirim nilai baru ke servo jika switch berubah
                                });
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
