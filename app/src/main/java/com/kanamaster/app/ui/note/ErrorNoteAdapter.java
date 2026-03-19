package com.kanamaster.app.ui.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kanamaster.app.R;
import com.kanamaster.app.data.PrefsManager;

import java.util.ArrayList;
import java.util.List;

public class ErrorNoteAdapter extends RecyclerView.Adapter<ErrorNoteAdapter.VH> {

    private List<PrefsManager.ErrorEntry> data = new ArrayList<>();

    public void setData(List<PrefsManager.ErrorEntry> d) {
        data = d;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_error_note, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        PrefsManager.ErrorEntry e = data.get(pos);

        h.tvKana.setText(e.kana);
        h.tvRomaji.setText(e.romaji);
        h.tvCount.setText(e.count + "회 오답");

        // 회차 칩 추가
        h.chipGroup.removeAllViews();
        for (int r : e.rounds) {
            Chip chip = new Chip(h.itemView.getContext());
            chip.setText(r + "회차");
            chip.setClickable(false);
            chip.setCheckable(false);
            h.chipGroup.addView(chip);
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvKana, tvRomaji, tvCount;
        ChipGroup chipGroup;

        VH(View v) {
            super(v);
            tvKana    = v.findViewById(R.id.tvKana);
            tvRomaji  = v.findViewById(R.id.tvRomaji);
            tvCount   = v.findViewById(R.id.tvCount);
            chipGroup = v.findViewById(R.id.chipGroup);
        }
    }
}