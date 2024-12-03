package com.iot.iot;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<List<SensorData>> sensorDataList = new MutableLiveData<>();
    private final OkHttpClient client = new OkHttpClient();

    public LiveData<List<SensorData>> getSensorDataList() {
        return sensorDataList;
    }
    private boolean isLoading = false;
    public void fetchDataFromInfluxDB() {
        if (isLoading) return; // Hindari pemanggilan ulang
        isLoading = true;
        String url = "http://10.2.23.178:8086/query?q=SELECT%20*%20FROM%20sensor_data%20ORDER%20BY%20time%20DESC%20LIMIT%20100&db=pbl_agri";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HTTP Error", "Error fetching data: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<SensorData> parsedData = parseSensorData(responseData);
                        sensorDataList.postValue(parsedData);
                    } catch (Exception e) {
                        Log.e("Parsing Error", "Error parsing response: " + e.getMessage());
                    }
                } else {
                    Log.e("HTTP Response", "Response not successful: " + response.code());
                }
            }
        });
    }

    private List<SensorData> parseSensorData(String result) {
        List<SensorData> dataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            if (resultsArray.length() > 0) {
                JSONObject resultObject = resultsArray.getJSONObject(0);
                JSONArray seriesArray = resultObject.getJSONArray("series");

                if (seriesArray.length() > 0) {
                    JSONObject seriesObject = seriesArray.getJSONObject(0);
                    JSONArray valuesArray = seriesObject.getJSONArray("values");

                    for (int i = 0; i < valuesArray.length(); i++) {
                        JSONArray valueEntry = valuesArray.getJSONArray(i);

                        String time = valueEntry.getString(0);
                        double ph = valueEntry.getDouble(1);
                        double suhuAir = valueEntry.getDouble(2);
                        double kadarGaram = valueEntry.getDouble(4);

                        dataList.add(new SensorData(time, ph, suhuAir, kadarGaram));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Parsing Error", "Error parsing JSON: " + e.getMessage());
        }
        return dataList;
    }
}
