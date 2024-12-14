package com.iot.iot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class JadwalFragment extends Fragment {

    private LinearLayout btn_setJadwal;
    private RecyclerView recyclerViewJadwal;
    private JadwalAdapter jadwalAdapter;
    private List<ModelJadwal> jadwalList;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "jadwalPrefs";
    private static final String KEY_JADWAL_LIST = "jadwalList";

    private static final String INFLUXDB_URL = "http://10.2.23.178:8086/write?db=pbl_agri";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jadwal, container, false);

        // Inisialisasi SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Inisialisasi RecyclerView
        recyclerViewJadwal = rootView.findViewById(R.id.recyclerView);
        recyclerViewJadwal.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inisialisasi List dan Adapter
        jadwalList = loadJadwalFromPreferences();
        jadwalAdapter = new JadwalAdapter(jadwalList, this::deleteJadwal);
        recyclerViewJadwal.setAdapter(jadwalAdapter);

        // Menambahkan ItemTouchHelper untuk swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Aksi saat item di-swipe
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewJadwal);

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
            ModelJadwal newJadwal = new ModelJadwal(selectedDay, time, "Keterangan Pakan", true);
            jadwalList.add(newJadwal);
            jadwalAdapter.notifyDataSetChanged();

            // Simpan ke SharedPreferences
            saveJadwalToPreferences();

            // Kirim data ke InfluxDB
            sendDataToInflux(selectedDay, time, "Keterangan Pakan");

            // Tampilkan konfirmasi
            Toast.makeText(requireContext(), "Jadwal disimpan: " + selectedDay + " jam " + time, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        // Menampilkan dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendDataToInflux(String day, String time, String description) {
        new Thread(() -> {
            try {
                // Format Line Protocol
                String lineProtocol = String.format(
                        "jadwal_pakan,day=%s description=\"%s\",time=\"%s\"",
                        day.replace(" ", "_"), description, time
                );

                // Buat koneksi HTTP
                URL url = new URL(INFLUXDB_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "text/plain");

                // Kirim data
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(lineProtocol.getBytes());
                outputStream.flush();
                outputStream.close();

                // Cek respons
                int responseCode = connection.getResponseCode();
                if (responseCode == 204) {
                    // Berhasil
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Data berhasil dikirim ke server", Toast.LENGTH_SHORT).show());
                } else {
                    // Gagal
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Gagal mengirim data ke server: " + responseCode, Toast.LENGTH_SHORT).show());
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void saveJadwalToPreferences() {
        Set<String> jadwalSet = new HashSet<>();
        for (ModelJadwal jadwal : jadwalList) {
            // Menyimpan data dalam format day|time|active
            jadwalSet.add(jadwal.getHari() + "|" + jadwal.getJam() + "|" + jadwal.isActive());
        }
        sharedPreferences.edit().putStringSet(KEY_JADWAL_LIST, jadwalSet).apply();
    }

    private List<ModelJadwal> loadJadwalFromPreferences() {
        Set<String> jadwalSet = sharedPreferences.getStringSet(KEY_JADWAL_LIST, new HashSet<>());
        List<ModelJadwal> list = new ArrayList<>();
        for (String jadwal : jadwalSet) {
            String[] parts = jadwal.split("\\|");
            // Membaca data dari format day|time|active
            String hari = parts[0];
            String time = parts[1];
            boolean active = Boolean.parseBoolean(parts[2]);
            list.add(new ModelJadwal(hari, time, "Keterangan Pakan", active));
        }
        return list;
    }


    private void deleteJadwal(int position) {
        jadwalList.remove(position);
        jadwalAdapter.notifyDataSetChanged();
        saveJadwalToPreferences();
        Toast.makeText(requireContext(), "Jadwal berhasil dihapus", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Jadwal")
                .setMessage("Apakah Anda yakin ingin menghapus jadwal ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    deleteJadwal(position);
                })
                .setNegativeButton("Batal", (dialog, which) -> {
                    // Menyembunyikan item kembali setelah swipe
                    jadwalAdapter.notifyItemChanged(position);
                })
                .show();
    }


}
