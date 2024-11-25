package com.iot.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private LineChart lineChart;


    private SharedViewModel sharedViewModel;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Di dalam onCreateView pada DashboardFragment

// Inisialisasi LineCharts
        LineChart lineChartPh = view.findViewById(R.id.lineChartPh);
        LineChart lineChartSuhuAir = view.findViewById(R.id.lineChartSuhuAir);
        LineChart lineChartKualitasAir = view.findViewById(R.id.lineChartSalinity);

// Contoh data dummy untuk grafik pH
        ArrayList<Entry> phEntries = new ArrayList<>();
        phEntries.add(new Entry(0, 7));
        phEntries.add(new Entry(1, 6.8f));
        phEntries.add(new Entry(2, 7.2f));
        phEntries.add(new Entry(3, 7.0f));

        LineDataSet phDataSet = new LineDataSet(phEntries, "pH Level");
        phDataSet.setColor(Color.BLUE);
        phDataSet.setLineWidth(2f);
        phDataSet.setCircleColor(Color.BLUE);
        phDataSet.setCircleRadius(5f);

        LineData lineDataPh = new LineData(phDataSet);
        lineChartPh.setData(lineDataPh);
        lineChartPh.invalidate(); // Refresh grafik pH

// Inisialisasi SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

// Amati data suhu dan tampilkan grafik suhu air
        sharedViewModel.getSuhuData().observe(getViewLifecycleOwner(), sensorDataList -> {
            ArrayList<Entry> tempEntries = new ArrayList<>();
            for (int i = 0; i < sensorDataList.size(); i++) {
                tempEntries.add(new Entry(i, (float) sensorDataList.get(i).getValue()));
            }

            LineDataSet tempDataSet = new LineDataSet(tempEntries, "Suhu Air");
            tempDataSet.setColor(Color.RED);
            tempDataSet.setLineWidth(2f);
            tempDataSet.setCircleColor(Color.RED);
            tempDataSet.setCircleRadius(5f);

            LineData lineDataSuhu = new LineData(tempDataSet);
            lineChartSuhuAir.setData(lineDataSuhu);
            lineChartSuhuAir.invalidate(); // Refresh grafik suhu air
        });

// Contoh data dummy untuk grafik salinitas
        ArrayList<Entry> salinityEntries = new ArrayList<>();
        salinityEntries.add(new Entry(0, 5.0f));
        salinityEntries.add(new Entry(1, 6.5f));
        salinityEntries.add(new Entry(2, 4.2f));
        salinityEntries.add(new Entry(3, 7.0f));
        salinityEntries.add(new Entry(4, 5.5f));

        LineDataSet qualityDataSet = new LineDataSet(salinityEntries, "Kadar Garam");
        qualityDataSet.setColor(Color.GREEN);
        qualityDataSet.setLineWidth(2f);
        qualityDataSet.setCircleColor(Color.GREEN);
        qualityDataSet.setCircleRadius(5f);

        LineData lineDataQuality = new LineData(qualityDataSet);
        lineChartKualitasAir.setData(lineDataQuality);
        lineChartKualitasAir.invalidate(); // Refresh grafik kualitas air

        return view;

    }

}
