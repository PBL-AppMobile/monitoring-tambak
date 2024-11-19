package com.iot.iot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class JadwalFragment extends Fragment {

    private LinearLayout btn_setJadwal;
    private RecyclerView recyclerViewJadwal;
    private JadwalAdapter jadwalAdapter;
    private List<ModelJadwal> jadwalList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jadwal, container, false);

        // Inisialisasi RecyclerView
        recyclerViewJadwal = rootView.findViewById(R.id.recyclerView);
        recyclerViewJadwal.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inisialisasi List dan Adapter
        jadwalList = new ArrayList<>();
        jadwalAdapter = new JadwalAdapter(jadwalList);
        recyclerViewJadwal.setAdapter(jadwalAdapter);

        // Inisialisasi button dan set onClickListener
        btn_setJadwal = rootView.findViewById(R.id.btn_setJadwal);
        btn_setJadwal.setOnClickListener(view -> showSetJadwalDialog());

        return rootView; 
    }

    private void showSetJadwalDialog() {
        // Membuat AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Jadwal Pakan Ikan");

        // Membuat Layout Custom untuk AlertDialog
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Spinner untuk hari
        Spinner daySpinner = new Spinner(requireContext());
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                new String[]{"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu", "Setiap Hari"});
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        layout.addView(daySpinner);

        // TimePicker untuk jam
        TimePicker timePicker = new TimePicker(requireContext());
        timePicker.setIs24HourView(true); // Format 24 jam
        layout.addView(timePicker);

        // Menambahkan layout custom ke dalam AlertDialog
        builder.setView(layout);

        // Menambahkan tombol "Simpan" dan "Batal"
        builder.setPositiveButton("Simpan", (dialog, which) -> {
            // Mengambil nilai dari Spinner dan TimePicker
            String selectedDay = daySpinner.getSelectedItem().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);

            // Menambahkan jadwal baru ke dalam list dan memperbarui RecyclerView
            jadwalList.add(new ModelJadwal(selectedDay, time, "Keterangan Pakan", true));
            jadwalAdapter.notifyDataSetChanged();

            // Tampilkan konfirmasi
            Toast.makeText(requireContext(), "Jadwal disimpan: " + selectedDay + " jam " + time, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        // Menampilkan dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
