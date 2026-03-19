package com.kanamaster.app.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kanamaster.app.data.PrefsManager;
import com.kanamaster.app.databinding.ActivitySettingsBinding;
import com.kanamaster.app.util.AlarmReceiver;

import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding b;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("⚙️ 설정");
        }

        prefs = PrefsManager.get(this);
        loadValues();
        setupListeners();
    }

    private void loadValues() {
        b.switchMorning.setChecked(prefs.isMorningEnabled());
        updateTimeDisplay();
        b.sliderCount.setValue(prefs.getMorningCount());
        b.tvCountValue.setText(prefs.getMorningCount() + "문제");
        setControlsEnabled(prefs.isMorningEnabled());
    }

    private void setupListeners() {
        // 알림 스위치
        b.switchMorning.setOnCheckedChangeListener((btn, checked) -> {
            prefs.setMorningEnabled(checked);
            setControlsEnabled(checked);
            if (checked) scheduleAlarm();
            else         cancelAlarm();
        });

        // 시간 선택
        b.btnTimePicker.setOnClickListener(v -> {
            new TimePickerDialog(this, (tp, hour, minute) -> {
                prefs.setMorningHour(hour);
                prefs.setMorningMinute(minute);
                updateTimeDisplay();
                if (prefs.isMorningEnabled()) scheduleAlarm();
            }, prefs.getMorningHour(), prefs.getMorningMinute(), true).show();
        });

        // 문제 수 슬라이더
        b.sliderCount.addOnChangeListener((slider, value, fromUser) -> {
            int count = (int) value;
            prefs.setMorningCount(count);
            b.tvCountValue.setText(count + "문제");
        });
    }

    private void updateTimeDisplay() {
        b.tvTime.setText(String.format(Locale.KOREA, "%02d:%02d",
                prefs.getMorningHour(), prefs.getMorningMinute()));
    }

    private void setControlsEnabled(boolean enabled) {
        b.btnTimePicker.setEnabled(enabled);
        b.sliderCount.setEnabled(enabled);
        b.tvTime.setAlpha(enabled ? 1f : 0.4f);
    }

    private void scheduleAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, prefs.getMorningHour());
        cal.set(Calendar.MINUTE,      prefs.getMorningMinute());
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);

        // 이미 지난 시간이면 내일로
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        } else {
            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pi);
        }
    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pi);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}