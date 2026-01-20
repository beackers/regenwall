package com.beackers.regenwall.crashcar;

import com.beackers.regenwall.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class CrashAdapter extends RecyclerView.Adapter<CrashAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(File file);
    }

    private final List<File> files;
    private final OnItemClickListener listener;

    public CrashAdapter(List<File> files, OnItemClickListener listener) {
        this.files = files;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.filename);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crash_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = files.get(position);

        String label = file.getName()
                .replace("exc_", "")
                .replace(".txt", "");

        holder.textView.setText(label);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
