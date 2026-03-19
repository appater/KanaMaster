package com.kanamaster.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kanamaster.app.R;
import com.kanamaster.app.data.PrefsManager;
import com.kanamaster.app.data.QuizSession;
import com.kanamaster.app.databinding.ActivityMainBinding;
import com.kanamaster.app.ui.note.ErrorNoteActivity;
import com.kanamaster.app.ui.quiz.QuizActivity;
import com.kanamaster.app.ui.settings.SettingsActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);

        prefs = PrefsManager.get(this);

        setupScriptToggle();
        setupModeToggle();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateErrorBadge();
        updateStats();
    }

    private void setupScriptToggle() {
        // 저장된 설정값으로 초기 선택 상태 설정
        if ("hira".equals(prefs.getScript())) {
            b.btnHira.setChecked(true);
        } else {
            b.btnKata.setChecked(true);
        }

        b.toggleScript.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnHira) prefs.setScript("hira");
            else                           prefs.setScript("kata");
        });
    }

    private void setupModeToggle() {
        // 저장된 설정값으로 초기 선택 상태 설정
        if ("mcq".equals(prefs.getMode())) {
            b.btnMcq.setChecked(true);
        } else {
            b.btnSubj.setChecked(true);
        }

        b.toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnMcq) prefs.setMode("mcq");
            else                          prefs.setMode("subj");
        });
    }

    private void setupButtons() {
        // 전체 퀴즈
        b.btnFullQuiz.setOnClickListener(v -> startQuiz(false));

        // 아침 퀴즈
        b.btnMorningQuiz.setOnClickListener(v -> startQuiz(true));

        // 오답 노트
        b.btnErrorNote.setOnClickListener(v ->
                startActivity(new Intent(this, ErrorNoteActivity.class)));
    }

    private void startQuiz(boolean morning) {
        int limit = morning ? prefs.getMorningCount() : -1;
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(QuizSession.EXTRA_SCRIPT, prefs.getScript());
        intent.putExtra(QuizSession.EXTRA_MODE, prefs.getMode());
        intent.putExtra(QuizSession.EXTRA_LIMIT, limit);
        intent.putExtra(QuizSession.EXTRA_IS_MORNING, morning);
        startActivity(intent);
    }

    private void updateErrorBadge() {
        Map<String, PrefsManager.ErrorEntry> note = prefs.getErrorNote();
        int count = note.size();
        if (count > 0) {
            b.tvErrorBadge.setText(count + "개 오답 기록 중");
            b.tvErrorBadge.setVisibility(View.VISIBLE);
        } else {
            b.tvErrorBadge.setVisibility(View.GONE);
        }
    }

    private void updateStats() {
        int sessions = prefs.getTotalSessions();
        b.tvSessionCount.setText(sessions + "회 완료");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}