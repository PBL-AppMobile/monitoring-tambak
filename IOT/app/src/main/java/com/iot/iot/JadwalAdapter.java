package com.iot.iot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;

import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder> {
    private List<ModelJadwal> jadwalList;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public JadwalAdapter(List<ModelJadwal> jadwalList, OnDeleteClickListener onDeleteClickListener) {
        this.jadwalList = jadwalList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_jadwal, parent, false);
        return new JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        ModelJadwal jadwal = jadwalList.get(position);
        holder.tvHari.setText(jadwal.getHari());
        holder.tvJam.setText(jadwal.getJam());
        holder.tvKeterangan.setText(jadwal.getKeterangan());
        holder.switchCompat.setChecked(jadwal.isActive());

        // Set listener untuk switch on/off
        holder.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            jadwal.setActive(isChecked);
            // Tambahkan logika untuk menyimpan status switch jika perlu
        });

        // Set listener untuk tombol hapus
        holder.itemView.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            onDeleteClickListener.onDeleteClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return jadwalList.size();
    }

    public static class JadwalViewHolder extends RecyclerView.ViewHolder {
        TextView tvHari, tvJam, tvKeterangan;
        SwitchCompat switchCompat;

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHari = itemView.findViewById(R.id.tv_hari);
            tvJam = itemView.findViewById(R.id.tv_jam);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
            switchCompat = itemView.findViewById(R.id.switch_compat);
        }
    }
}
