package com.example.chilipestdetection.adapters;
// PenangananAdapter.java
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chilipestdetection.R;
import com.example.chilipestdetection.helpers.DatabaseHelper;
import com.example.chilipestdetection.models.PenangananData;

import java.io.File;
import java.util.List;

public class PenangananAdapter extends RecyclerView.Adapter<PenangananAdapter.ViewHolder> {

    private List<PenangananData> penangananList;
    private Context context;
    private OnItemClickListener listener;
    private DatabaseHelper databaseHelper;

    public interface OnItemClickListener {
        void onItemClick(PenangananData penanganan);
        void onEditClick(PenangananData penanganan);
        void onDeleteClick(PenangananData penanganan);
    }

    public PenangananAdapter(Context context, List<PenangananData> penangananList) {
        this.context = context;
        this.penangananList = penangananList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_penanganan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PenangananData penanganan = penangananList.get(position);

        holder.tvKodePHama.setText(penanganan.getKode_p_hama());
        holder.tvTanaman.setText("Tanaman: " + penanganan.getTanaman());
        holder.tvHama.setText("Hama: " + penanganan.getHama());
        holder.tvGejala.setText("Gejala: " + penanganan.getGejala());
        holder.tvAturanFuzzy.setText("Aturan Fuzzy: " + penanganan.getAturan_fuzzy());

        // Load image from local storage
        String imagePath = databaseHelper.getImagePath(penanganan.getKode_p_hama());
        if (imagePath != null && new File(imagePath).exists()) {
            holder.ivGambar.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            holder.ivGambar.setImageResource(R.drawable.baseline_aspect_ratio_24);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(penanganan);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(penanganan);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(penanganan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return penangananList != null ? penangananList.size() : 0;
    }

    public void updateData(List<PenangananData> newData) {
        this.penangananList = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivGambar;
        TextView tvKodePHama, tvTanaman, tvHama, tvGejala, tvAturanFuzzy;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            tvKodePHama = itemView.findViewById(R.id.tvKodePHama);
            tvTanaman = itemView.findViewById(R.id.tvTanaman);
            tvHama = itemView.findViewById(R.id.tvHama);
            tvGejala = itemView.findViewById(R.id.tvGejala);
            tvAturanFuzzy = itemView.findViewById(R.id.tvAturanFuzzy);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}