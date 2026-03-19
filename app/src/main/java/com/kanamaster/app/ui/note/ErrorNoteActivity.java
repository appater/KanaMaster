package com.kanamaster.app.ui.note;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kanamaster.app.R;
import com.kanamaster.app.data.PrefsManager;
import com.kanamaster.app.databinding.ActivityErrorNoteBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErrorNoteActivity extends AppCompatActivity {

    private ActivityErrorNoteBinding b;
    private PrefsManager prefs;
    private ErrorNoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityErrorNoteBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("📝 오답 노트");
        }

        prefs = PrefsManager.get(this);

        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ErrorNoteAdapter();
        b.recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        Map<String, PrefsManager.ErrorEntry> note = prefs.getErrorNote();
        List<PrefsManager.ErrorEntry> list = new ArrayList<>(note.values());

        // 오답 횟수 많은 순으로 정렬
        list.sort((a, b) -> Integer.compare(b.count, a.count));

        if (list.isEmpty()) {
            b.tvEmpty.setVisibility(View.VISIBLE);
            b.recyclerView.setVisibility(View.GONE);
        } else {
            b.tvEmpty.setVisibility(View.GONE);
            b.recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            new AlertDialog.Builder(this)
                    .setTitle("오답 노트 초기화")
                    .setMessage("모든 오답 기록을 삭제할까요?")
                    .setPositiveButton("삭제", (d, w) -> {
                        prefs.clearErrorNote();
                        loadData();
                    })
                    .setNegativeButton("취소", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}