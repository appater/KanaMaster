package com.kanamaster.app.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.kanamaster.app.R;
import com.kanamaster.app.data.PrefsManager;
import com.kanamaster.app.data.QuizSession;
import com.kanamaster.app.ui.quiz.QuizActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "kana_morning";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        PrefsManager prefs = PrefsManager.get(ctx);
        if (!prefs.isMorningEnabled()) return;

        createChannel(ctx);

        // 알림 탭하면 바로 퀴즈 시작
        Intent quizIntent = new Intent(ctx, QuizActivity.class);
        quizIntent.putExtra(QuizSession.EXTRA_SCRIPT, prefs.getScript());
        quizIntent.putExtra(QuizSession.EXTRA_MODE, prefs.getMode());
        quizIntent.putExtra(QuizSession.EXTRA_LIMIT, prefs.getMorningCount());
        quizIntent.putExtra(QuizSession.EXTRA_IS_MORNING, true);
        quizIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pi = PendingIntent.getActivity(ctx, 0, quizIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notif = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("☀️ 아침 가나 퀴즈")
                .setContentText(prefs.getMorningCount() + "문제 풀 시간이에요!")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager nm =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(1001, notif);
    }

    private void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "아침 퀴즈 알림",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager nm =
                    ctx.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }
}