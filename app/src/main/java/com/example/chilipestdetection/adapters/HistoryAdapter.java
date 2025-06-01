package com.example.chilipestdetection.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.chilipestdetection.models.DetectionHistory;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<DetectionHistory> historyList;
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onItemClick(DetectionHistory history);
        void onDeleteClick(DetectionHistory history);
    }

    public HistoryAdapter(Context context, List<DetectionHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<DetectionHistory> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DetectionHistory history = historyList.get(position);

        // Set pest type
        holder.tvPestType.setText(history.getPestType());

        // Format and set date
        holder.tvDate.setText(formatDate(history.getDetectionDate()));

        // Set severity with color
        holder.tvSeverity.setText(history.getSeverity());
        setSeverityColor(holder.tvSeverity, history.getSeverity());

        // Set accuracy
        holder.tvAccuracy.setText(String.format(Locale.getDefault(), "%.1f%%", history.getAccuracy()));

        // Load image
        loadImage(holder.ivPestImage, history.getImagePath());

        // Set click listeners
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(history);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(history);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    private void loadImage(ImageView imageView, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.baseline_aspect_ratio_24);
                }
            } else {
                imageView.setImageResource(R.drawable.baseline_aspect_ratio_24);
            }
        } else {
            imageView.setImageResource(R.drawable.baseline_aspect_ratio_24);
        }
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString; // Return original if parsing fails
        }
    }

    private void setSeverityColor(TextView textView, String severity) {
        int color;
        switch (severity.toLowerCase()) {
            case "ringan":
                color = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "sedang":
                color = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "parah":
                color = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                color = context.getResources().getColor(android.R.color.black);
                break;
        }
        textView.setTextColor(color);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivPestImage;
        ImageView ivDelete;
        TextView tvPestType;
        TextView tvDate;
        TextView tvSeverity;
        TextView tvAccuracy;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivPestImage = itemView.findViewById(R.id.ivPestImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvPestType = itemView.findViewById(R.id.tvPestType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);
            tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
        }
    }
}