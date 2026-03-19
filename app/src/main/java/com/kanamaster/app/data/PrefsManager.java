package com.kanamaster.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefsManager {

    private static final String PREFS_NAME      = "kana_prefs";
    private static final String KEY_SCRIPT      = "script";
    private static final String KEY_MODE        = "mode";
    private static final String KEY_ERROR_NOTE  = "error_note";
    private static final String KEY_MORNING_ON  = "morning_enabled";
    private static final String KEY_MORNING_H   = "morning_hour";
    private static final String KEY_MORNING_M   = "morning_minute";
    private static final String KEY_MORNING_CNT = "morning_count";
    private static final String KEY_SESSIONS    = "total_sessions";

    private final SharedPreferences prefs;
    private static PrefsManager instance;

    public static PrefsManager get(Context ctx) {
        if (instance == null)
            instance = new PrefsManager(ctx.getApplicationContext());
        return instance;
    }

    private PrefsManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ── 문자 종류 / 문제 형식 ──────────────────────────
    public String getScript()        { return prefs.getString(KEY_SCRIPT, "hira"); }
    public void   setScript(String s){ prefs.edit().putString(KEY_SCRIPT, s).apply(); }

    public String getMode()          { return prefs.getString(KEY_MODE, "mcq"); }
    public void   setMode(String m)  { prefs.edit().putString(KEY_MODE, m).apply(); }

    // ── 아침 퀴즈 설정 ────────────────────────────────
    public boolean isMorningEnabled()        { return prefs.getBoolean(KEY_MORNING_ON, false); }
    public void setMorningEnabled(boolean b) { prefs.edit().putBoolean(KEY_MORNING_ON, b).apply(); }

    public int  getMorningHour()       { return prefs.getInt(KEY_MORNING_H, 7); }
    public void setMorningHour(int h)  { prefs.edit().putInt(KEY_MORNING_H, h).apply(); }

    public int  getMorningMinute()     { return prefs.getInt(KEY_MORNING_M, 0); }
    public void setMorningMinute(int m){ prefs.edit().putInt(KEY_MORNING_M, m).apply(); }

    public int  getMorningCount()      { return prefs.getInt(KEY_MORNING_CNT, 5); }
    public void setMorningCount(int c) { prefs.edit().putInt(KEY_MORNING_CNT, c).apply(); }

    // ── 세션 카운터 ───────────────────────────────────
    public int  getTotalSessions()  { return prefs.getInt(KEY_SESSIONS, 0); }
    public void incrementSessions() {
        prefs.edit().putInt(KEY_SESSIONS, getTotalSessions() + 1).apply();
    }

    // ── 오답 노트 ─────────────────────────────────────
    public static class ErrorEntry {
        public String       kana;
        public String       romaji;
        public int          count;
        public List<Integer> rounds;

        public ErrorEntry(String kana, String romaji) {
            this.kana   = kana;
            this.romaji = romaji;
            this.count  = 0;
            this.rounds = new ArrayList<>();
        }
    }

    public Map<String, ErrorEntry> getErrorNote() {
        String json = prefs.getString(KEY_ERROR_NOTE, null);
        if (json == null) return new HashMap<>();
        Type type = new TypeToken<Map<String, ErrorEntry>>(){}.getType();
        try {
            Map<String, ErrorEntry> map = new Gson().fromJson(json, type);
            return map != null ? map : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void saveErrorNote(Map<String, ErrorEntry> note) {
        prefs.edit().putString(KEY_ERROR_NOTE, new Gson().toJson(note)).apply();
    }

    public void recordError(String kana, String romaji, int round) {
        Map<String, ErrorEntry> note = getErrorNote();
        if (!note.containsKey(kana))
            note.put(kana, new ErrorEntry(kana, romaji));
        ErrorEntry entry = note.get(kana);
        entry.count++;
        if (!entry.rounds.contains(round)) entry.rounds.add(round);
        saveErrorNote(note);
    }

    public void clearErrorNote() {
        prefs.edit().remove(KEY_ERROR_NOTE).apply();
    }
}