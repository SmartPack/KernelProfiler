/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Script Manager, an app to create, import, edit
 * and easily execute any properly formatted shell scripts.
 *
 */

package com.smartpack.kernelprofiler.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelprofiler.R;

import java.io.File;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<String> data;
    public RecycleViewAdapter (List<String> data){
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        RecycleViewAdapter.onItemClickListener = onItemClickListener;
    }

    private static OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public String getName(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_recycle_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(this.data.get(position));
        String description = KP.getProfileDescription(KP.KPFile() + "/" + this.data.get(position) + ".sh");
        if (description == null) {
            description = holder.mDescription.getContext().getString(R.string.description_unknown);
        }
        holder.mDescription.setText(description);
        holder.mCheckBox.setChecked((this.data.get(position) + ".sh").equals(KP.getDefaultProfile()));
        holder.mCheckBox.setOnClickListener(v -> {
            if (!new File(KP.KPFile() + "/" + this.data.get(position) + ".sh").getName().equals(KP.getDefaultProfile())) {
                Utils.create(Utils.readFile(KP.KP_CONFIG).replaceAll(Objects.requireNonNull(
                        KP.getDefaultProfile()), new File(KP.KPFile() + "/" + this.data.get(position) + ".sh").getName()), KP.KP_CONFIG);
                Utils.snackbar(holder.mCheckBox, holder.mCheckBox.getContext().getString(R.string.on_boot_message, new File(KP.KPFile() + "/" + this.data.get(position) + ".sh").getName()));
            } else {
                Utils.snackbar(holder.mCheckBox, holder.mCheckBox.getContext().getString(R.string.on_boot_conformation, new File(KP.KPFile() + "/" + this.data.get(position) + ".sh").getName()));
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MaterialTextView mTitle;
        private MaterialTextView mDescription;
        private MaterialCheckBox mCheckBox;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mCheckBox = view.findViewById(R.id.checkbox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, ViewHolder.super.getAdapterPosition());
        }
    }

}