package com.iot.iot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RiwayatFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<SensorData> itemList;
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

        // Setup RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        // Setup Adapter
        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(itemList);
        recyclerView.setAdapter(myAdapter);

        // Setup ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe data dari ViewModel
        sharedViewModel.getSensorDataList().observe(getViewLifecycleOwner(), new Observer<List<SensorData>>() {
            @Override
            public void onChanged(List<SensorData> sensorData) {
                itemList.clear();
                itemList.addAll(sensorData);
                myAdapter.notifyDataSetChanged();
            }
        });

        // Mulai mengambil data
        sharedViewModel.fetchDataFromInfluxDB();

        return rootView;
    }
}
