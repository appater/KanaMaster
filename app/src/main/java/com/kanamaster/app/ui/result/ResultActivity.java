package com.kanamaster.app.ui.result;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.kanamaster.app.R;
import com.kanamaster.app.data.PrefsManager;
import com.kanamaster.app.data.QuizSession;
import com.kanamaster.app.databinding.ActivityResultBinding;
import com.kanamaster.app.ui.home.MainActivity;
import com.kanamaster.app.ui.note.ErrorNoteActivity;
import com.kanamaster.app.ui.quiz.QuizActivity;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        int correct   = getIntent().getIntExtra("correct", 0);
        int wrong     = getIntent().getIntExtra("wrong", 0);
        int total     = getIntent().getIntExtra("total", 1);
        int round     = getIntent().getIntExtra("round", 1);
        boolean isMorning = getIntent().getBooleanExtra("is_morning", false);

        int pct = (int)(correct * 100f / total);

        // 텍스트 설정
        b.tvRound.setText(round + "회차 완료");
        b.tvScore.setText(pct + "%");
        b.tvDetail.setText("정답 " + correct + " / " + total);
        b.tvWrong.setText("오답 " + wrong + "개");

        // 점수별 이모지
        String emoji;
        if (pct == 100)     emoji = "🎉";
        else if (pct >= 80) emoji = "😊";
        else if (pct >= 50) emoji = "💪";
        else                emoji = "📚";
        b.tvEmoji.setText(emoji);

        // 점수별 색상
        if (pct >= 80) {
            b.tvScore.setTextColor(
                    ContextCompat.getColor(this, R.color.green));
        } else if (pct >= 50) {
            b.tvScore.setTextColor(
                    ContextCompat.getColor(this, R.color.gold));
        } else {
            b.tvScore.setTextColor(
                    ContextCompat.getColor(this, R.color.red));
        }

        PrefsManager prefs = PrefsManager.get(this);

        // 다시 도전
        b.btnRetry.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra(QuizSession.EXTRA_SCRIPT, prefs.getScript());
            intent.putExtra(QuizSession.EXTRA_MODE, prefs.getMode());
            intent.putExtra(QuizSession.EXTRA_LIMIT,
                    isMorning ? prefs.getMorningCount() : -1);
            intent.putExtra(QuizSession.EXTRA_IS_MORNING, isMorning);
            startActivity(intent);
            finish();
        });

        // 홈으로
        b.btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

        // 오답 보기
        b.btnSeeErrors.setOnClickListener(v ->
                startActivity(new Intent(this, ErrorNoteActivity.class)));
    }
}