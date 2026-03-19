package com.kanamaster.app.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.kanamaster.app.R;
import com.kanamaster.app.data.KanaData;
import com.kanamaster.app.data.PrefsManager;
import com.kanamaster.app.data.QuizSession;
import com.kanamaster.app.databinding.ActivityQuizBinding;
import com.kanamaster.app.ui.result.ResultActivity;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding b;
    private QuizSession session;
    private PrefsManager prefs;
    private boolean answered = false;
    private boolean isMorning = false;
    private int round = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = PrefsManager.get(this);

        String script = getIntent().getStringExtra(QuizSession.EXTRA_SCRIPT);
        String mode   = getIntent().getStringExtra(QuizSession.EXTRA_MODE);
        int limit     = getIntent().getIntExtra(QuizSession.EXTRA_LIMIT, -1);
        isMorning     = getIntent().getBooleanExtra(QuizSession.EXTRA_IS_MORNING, false);
        round         = prefs.getTotalSessions() + 1;

        if (script == null) script = "hira";
        if (mode == null)   mode   = "mcq";

        session = new QuizSession(script, mode, limit, round);

        String title = isMorning
                ? "☀️ 아침 퀴즈 " + prefs.getMorningCount() + "문제"
                : "전체 퀴즈";
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        b.btnNext.setOnClickListener(v -> nextQuestion());
        b.btnSubmitSubj.setOnClickListener(v -> checkSubjective());
        b.etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkSubjective();
                return true;
            }
            return false;
        });

        renderQuestion();
    }

    private void renderQuestion() {
        if (session.isFinished()) { showResult(); return; }

        answered = false;
        b.btnNext.setVisibility(View.GONE);
        b.tvFeedback.setVisibility(View.GONE);

        KanaData.KanaItem item = session.currentItem();

        // 진행도
        b.progressBar.setProgress(session.getProgress());
        b.tvProgress.setText((session.getIndex() + 1) + " / " + session.getDeckSize());
        b.tvRound.setText(round + "회차");

        // 가나 표시
        b.tvKana.setText(item.kana);

        if ("mcq".equals(session.getMode())) {
            b.layoutMcq.setVisibility(View.VISIBLE);
            b.layoutSubj.setVisibility(View.GONE);
            setupMcqOptions();
        } else {
            b.layoutMcq.setVisibility(View.GONE);
            b.layoutSubj.setVisibility(View.VISIBLE);
            b.etAnswer.setText("");
            b.etAnswer.setEnabled(true);
            b.btnSubmitSubj.setEnabled(true);
            b.etAnswer.requestFocus();
            showKeyboard();
        }
    }

    private void setupMcqOptions() {
        List<KanaData.KanaItem> opts = session.getOptions();
        Button[] btns = { b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4 };

        for (int i = 0; i < 4; i++) {
            final String romaji = opts.get(i).romaji;
            btns[i].setText(romaji);
            btns[i].setEnabled(true);
            btns[i].setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.paper2));
            btns[i].setTextColor(
                    ContextCompat.getColor(this, R.color.ink));
            final int idx = i;
            btns[i].setOnClickListener(v ->
                    checkMcq(romaji, btns, idx));
        }
    }

    private void checkMcq(String chosen, Button[] btns, int chosenIdx) {
        if (answered) return;
        answered = true;

        KanaData.KanaItem correct = session.currentItem();
        boolean ok = session.answer(chosen);

        for (Button btn : btns) btn.setEnabled(false);

        if (ok) {
            btns[chosenIdx].setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.green));
            btns[chosenIdx].setTextColor(
                    ContextCompat.getColor(this, R.color.white));
            showFeedback(true, correct.romaji);
        } else {
            btns[chosenIdx].setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.red));
            btns[chosenIdx].setTextColor(
                    ContextCompat.getColor(this, R.color.white));
            // 정답 버튼 초록으로 표시
            for (Button btn : btns) {
                if (btn.getText().toString().equals(correct.romaji)) {
                    btn.setBackgroundTintList(
                            ContextCompat.getColorStateList(this, R.color.green));
                    btn.setTextColor(
                            ContextCompat.getColor(this, R.color.white));
                }
            }
            showFeedback(false, correct.romaji);
            prefs.recordError(correct.kana, correct.romaji, round);
        }

        b.btnNext.setVisibility(View.VISIBLE);
        b.btnNext.setText(session.isFinished() ? "결과 보기" : "다음 →");
    }

    private void checkSubjective() {
        if (answered) return;
        String input = b.etAnswer.getText().toString().trim();
        if (TextUtils.isEmpty(input)) return;
        answered = true;

        hideKeyboard();
        b.etAnswer.setEnabled(false);
        b.btnSubmitSubj.setEnabled(false);

        KanaData.KanaItem correct = session.currentItem();
        boolean ok = session.answer(input);

        if (ok) {
            showFeedback(true, correct.romaji);
        } else {
            showFeedback(false, correct.romaji);
            prefs.recordError(correct.kana, correct.romaji, round);
        }

        b.btnNext.setVisibility(View.VISIBLE);
        b.btnNext.setText(session.isFinished() ? "결과 보기" : "다음 →");
    }

    private void nextQuestion() {
        if (session.isFinished()) { showResult(); return; }
        renderQuestion();
    }

    private void showFeedback(boolean ok, String correct) {
        b.tvFeedback.setVisibility(View.VISIBLE);
        if (ok) {
            b.tvFeedback.setText("✓ 정답!");
            b.tvFeedback.setTextColor(
                    ContextCompat.getColor(this, R.color.green));
        } else {
            b.tvFeedback.setText("✗ 오답   정답: " + correct);
            b.tvFeedback.setTextColor(
                    ContextCompat.getColor(this, R.color.red));
        }
    }

    private void showResult() {
        prefs.incrementSessions();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("correct",    session.getCorrectCount());
        intent.putExtra("wrong",      session.getWrongCount());
        intent.putExtra("total",      session.getDeckSize());
        intent.putExtra("round",      round);
        intent.putExtra("is_morning", isMorning);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void showKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(b.etAnswer, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(b.etAnswer.getWindowToken(), 0);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}