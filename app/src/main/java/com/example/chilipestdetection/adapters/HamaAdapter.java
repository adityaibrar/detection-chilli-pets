package com.example.chilipestdetection.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.models.HamaRequest;
import com.example.chilipestdetection.models.HamaResponse;

import java.util.List;

public class HamaAdapter extends RecyclerView.Adapter<HamaAdapter.HamaViewHolder> {
    private List<HamaRequest> hamaList;
    private Context context;
    private OnHamaActionListener listener;

    public interface OnHamaActionListener {
        void onEditHama(String kodeHama, String namaHama, String type, String pengendalianRekomendasi, String pestisidaYangDisarankan, String catatanTambahan);
        void onDeleteHama(String kodeHama);
    }

    public HamaAdapter(Context context, List<HamaRequest> hamaList) {
        this.context = context;
        this.hamaList = hamaList;
    }

    public void setOnHamaActionListener(OnHamaActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HamaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hama_card, parent, false);
        return new HamaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HamaViewHolder holder, int position) {
        HamaRequest hama = hamaList.get(position);

        holder.tvKodeHama.setText(hama.getKodeHama());
        holder.tvNamaHama.setText(hama.getNamaHama());
        holder.tvType.setText(hama.getType());
        holder.tvPengendalian.setText(hama.getPengendalianRekomndasi());
        holder.tvPestisida.setText(hama.getPestisidaYangDisarankan());
        holder.tvCatatan.setText(hama.getCatatanTambahan());
        holder.btnEdit.setOnClickListener(v -> showEditDialog(hama));
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(hama));
    }

    @Override
    public int getItemCount() {
        return hamaList != null ? hamaList.size() : 0;
    }

    public void updateData(List<HamaRequest> newHamaList) {
        if (newHamaList != null) {
            this.hamaList.clear();
            this.hamaList.addAll(newHamaList);
            notifyDataSetChanged();
        } else {
            // Jika data null, kosongkan list
            this.hamaList.clear();
            notifyDataSetChanged();
        }
    }

    private void showEditDialog(HamaRequest hama) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_hama, null);

        EditText etNamaHama = dialogView.findViewById(R.id.etNamaHama);
        EditText etType = dialogView.findViewById(R.id.etType);
        EditText etPengendalianRekomndasi = dialogView.findViewById(R.id.etPengedalian);
        EditText etPestisida = dialogView.findViewById(R.id.etPestisida);
        EditText etCatatan = dialogView.findViewById(R.id.etCatatan);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        etNamaHama.setText(hama.getNamaHama());
        etType.setText(hama.getType());
        etPengendalianRekomndasi.setText(hama.getPengendalianRekomndasi());
        etPestisida.setText(hama.getPestisidaYangDisarankan());
        etCatatan.setText(hama.getCatatanTambahan());

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String namaHama = etNamaHama.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String pengendalian = etPengendalianRekomndasi.getText().toString().trim();
            String pestisida = etPestisida.getText().toString().trim();
            String catatan = etCatatan.getText().toString().trim();

            if (!namaHama.isEmpty() && !type.isEmpty()) {
                if (listener != null) {
                    listener.onEditHama(hama.getKodeHama(), namaHama, type, pengendalian, pestisida, catatan);
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteDialog(HamaRequest hama) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Hama")
                .setMessage("Apakah Anda yakin ingin menghapus hama " + hama.getNamaHama() + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (listener != null) {
                        listener.onDeleteHama(hama.getKodeHama());
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    public static class HamaViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvKodeHama, tvNamaHama, tvType, tvPengendalian, tvPestisida, tvCatatan;
        Button btnEdit, btnDelete;

        public HamaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvKodeHama = itemView.findViewById(R.id.tvKodeHama);
            tvNamaHama = itemView.findViewById(R.id.tvNamaHama);
            tvPengendalian = itemView.findViewById(R.id.tvPengendalian);
            tvPestisida = itemView.findViewById(R.id.tvPestisida);
            tvCatatan = itemView.findViewById(R.id.tvCatatan);
            tvType = itemView.findViewById(R.id.tvType);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}