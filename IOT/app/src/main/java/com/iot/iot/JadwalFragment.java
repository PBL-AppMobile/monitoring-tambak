package com.iot.iot;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        recyclerViewJadwal = rootView.findViewById(R.id.recyclerView);
        recyclerViewJadwal.setLayoutManager(new LinearLayoutManager(requireContext()));

        jadwalList = loadJadwalFromPreferences();
        jadwalAdapter = new JadwalAdapter(jadwalList, this::deleteJadwal);
        recyclerViewJadwal.setAdapter(jadwalAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewJadwal);

        btn_setJadwal = rootView.findViewById(R.id.btn_setJadwal);
        btn_setJadwal.setOnClickListener(view -> showSetJadwalDialog());

        return rootView;
    }

    private void showSetJadwalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Jadwal Pakan Ikan");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        Spinner daySpinner = new Spinner(requireContext());
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                new String[]{"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu", "Setiap Hari"});
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        layout.addView(daySpinner);

        TimePicker timePicker = new TimePicker(requireContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(12);
            timePicker.setMinute(0);
        } else {
            timePicker.setIs24HourView(true);
        }
        layout.addView(timePicker);

        builder.setView(layout);
        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String selectedDay = daySpinner.getSelectedItem().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);

            ModelJadwal newJadwal = new ModelJadwal(selectedDay, time, "Keterangan Pakan", true);
            jadwalList.add(newJadwal);
            jadwalAdapter.notifyDataSetChanged();

            saveJadwalToPreferences();
            sendDataToInflux(selectedDay, time, "Keterangan Pakan");
            setAlarm(requireContext(), selectedDay, time);

            Toast.makeText(requireContext(), "Jadwal disimpan: " + selectedDay + " jam " + time, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendDataToInflux(String day, String time, String description) {
        new Thread(() -> {
            try {
                String lineProtocol = String.format("jadwal_pakan,day=%s description=\"%s\",time=\"%s\"",
                        day.replace(" ", "_"), description, time);

                URL url = new URL(INFLUXDB_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "text/plain");

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(lineProtocol.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == 204) {
                    getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Data berhasil dikirim ke server", Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Gagal mengirim data ke server: " + responseCode, Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void saveJadwalToPreferences() {
        Set<String> jadwalSet = new HashSet<>();
        for (ModelJadwal jadwal : jadwalList) {
            jadwalSet.add(jadwal.getHari() + "|" + jadwal.getJam() + "|" + jadwal.isActive());
        }
        sharedPreferences.edit().putStringSet(KEY_JADWAL_LIST, jadwalSet).apply();
    }

    private List<ModelJadwal> loadJadwalFromPreferences() {
        Set<String> jadwalSet = sharedPreferences.getStringSet(KEY_JADWAL_LIST, new HashSet<>());
        List<ModelJadwal> list = new ArrayList<>();
        for (String jadwal : jadwalSet) {
            String[] parts = jadwal.split("\\|");
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
                .setPositiveButton("Hapus", (dialog, which) -> deleteJadwal(position))
                .setNegativeButton("Batal", (dialog, which) -> jadwalAdapter.notifyItemChanged(position))
                .show();
    }

    private void setAlarm(Context context, String day, String time) {
        try {
            // Parse time into hours and minutes
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Calendar to set the alarm
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // Check the day and adjust the calendar accordingly
            switch (day) {
                case "Senin":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    break;
                case "Selasa":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    break;
                case "Rabu":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    break;
                case "Kamis":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    break;
                case "Jumat":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    break;
                case "Sabtu":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    break;
                case "Minggu":
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    break;
                case "Setiap Hari":
                    // No need to adjust day for everyday
                    break;
                default:
                    break;
            }

            // Create and configure the alarm
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Intent intent = new Intent(context, AlarmReceiver.class); // Assume you have a receiver for the alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Set the alarm (for example, repeating every day at the same time)
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
