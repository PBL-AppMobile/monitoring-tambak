package com.iot.iot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final List<SensorData> dataList;

    public MyAdapter(List<SensorData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_riwayat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SensorData data = dataList.get(position);

        holder.tvTanggal.setText(data.getTimeFormatted());
        holder.tvJam.setText(data.getTimeFormatted().split(" ")[1]); // Ambil hanya jam
        holder.tvPh.setText(String.format("pH: %.2f", data.getPh()));
        holder.tvSuhuAir.setText(String.format("Suhu Air: %.2f°C", data.getTemp()));
        holder.tvKualitasAir.setText(String.format("Kadar Garam: %.2f%%", data.getSalinity()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvJam, tvPh, tvSuhuAir, tvKualitasAir;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Hubungkan elemen-elemen dengan ID pada layout
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvJam = itemView.findViewById(R.id.tv_jam);
            tvPh = itemView.findViewById(R.id.tv_ph);
            tvSuhuAir = itemView.findViewById(R.id.tv_suhu_air);
            tvKualitasAir = itemView.findViewById(R.id.tv_kualitas_air);
        }
    }
}
