package com.iot.iot;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

        // Jadwalkan pemanggilan ulang fetchDataFromApi setelah 1 detik
        handler.postDelayed(dataFetcher, 1000);
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
            boolean[] servoState = {false};

            for (int i = 0; i < valuesArray.length(); i++) {
                JSONArray dataRow = valuesArray.getJSONArray(i);
                float ph = (float) dataRow.getDouble(1);
                float salinity = (float) dataRow.getDouble(2);
                float suhu = (float) dataRow.getDouble(4);
                int servo = dataRow.getInt(3);

                phEntries.add(new Entry(i, ph));
                suhuEntries.add(new Entry(i, suhu));
                salinityEntries.add(new Entry(i, salinity));

                if (i == 0) servoState[0] = servo > 0;
            }

            requireActivity().runOnUiThread(() -> {
                updateLineChart(lineChartPh, phEntries, "pH Level", Color.BLUE);
                updateLineChart(lineChartSuhuAir, suhuEntries, "Suhu Air", Color.RED);
                updateLineChart(lineChartKualitasAir, salinityEntries, "Kadar Garam", Color.GREEN);

                tvPhLevel.setText(String.format("pH: %.2f", phEntries.get(0).getY()));
                tvWaterTemp.setText(String.format("Temp: %.2f°C", suhuEntries.get(0).getY()));
                tvSaltLevel.setText(String.format("Salt: %.2f%%", salinityEntries.get(0).getY()));

                swBaru.setOnCheckedChangeListener(null);
                swBaru.setChecked(servoState[0]);
                swBaru.setOnCheckedChangeListener((buttonView, isChecked) -> sendServoCommand(isChecked ? 1 : 0));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLineChart(LineChart chart, ArrayList<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(5f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void sendServoCommand(int command) {
        System.out.println("Servo Command: " + command);
        // Tambahkan logika kontrol ke server
    }
}
