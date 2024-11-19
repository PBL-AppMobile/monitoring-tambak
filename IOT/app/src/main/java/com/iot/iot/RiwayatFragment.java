package com.iot.iot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import androidx.lifecycle.ViewModelProvider;

public class RiwayatFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<SensorData> itemList;
    // Di dalam deklarasi class RiwayatFragment
    private SharedViewModel sharedViewModel;

    public RiwayatFragment() {
        // Required empty public constructor
    }

    public static RiwayatFragment newInstance() {
        return new RiwayatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_riwayat, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(itemList);
        recyclerView.setAdapter(myAdapter);


// Di dalam onCreateView atau onViewCreated
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

// Setelah data suhu berhasil diparsing dalam metode parseSensorData
        sharedViewModel.setSuhuData(itemList);

        fetchDataFromInfluxDB();

        return rootView;
    }

    private void fetchDataFromInfluxDB() {
        OkHttpClient client = new OkHttpClient();

        String url = "http://10.2.23.178:8086/query?q=SELECT%20*%20FROM%20sensor_data%20ORDER%20BY%20time%20DESC%20LIMIT%20100&db=pbl_agri";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.e("HTTP Error", "Error fetching data: " + e.getMessage());

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    Log.d("HTTP Response", responseData);

                    requireActivity().runOnUiThread(() -> parseSensorData(responseData));
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Response not successful: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void parseSensorData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            if (resultsArray.length() > 0) {
                JSONObject resultObject = resultsArray.getJSONObject(0);
                JSONArray seriesArray = resultObject.getJSONArray("series");

                if (seriesArray.length() > 0) {
                    JSONObject seriesObject = seriesArray.getJSONObject(0);
                    JSONArray valuesArray = seriesObject.getJSONArray("values");

                    itemList.clear();

                    for (int i = 0; i < valuesArray.length(); i++) {
                        JSONArray valueEntry = valuesArray.getJSONArray(i);
                        String time = valueEntry.getString(0);
                        double value = valueEntry.getDouble(1);

                        itemList.add(new SensorData(time, value));
                    }

                    myAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
