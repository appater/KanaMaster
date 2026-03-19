package com.kanamaster.app.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizSession {

    // Activity 간 데이터 전달용 키
    public static final String EXTRA_SCRIPT     = "extra_script";
    public static final String EXTRA_MODE       = "extra_mode";
    public static final String EXTRA_LIMIT      = "extra_limit";      // -1 = 전체
    public static final String EXTRA_IS_MORNING = "extra_is_morning";

    private final List<KanaData.KanaItem> deck;     // 이번 판 문제 목록
    private final List<KanaData.KanaItem> allItems; // 오답 후보 전체 풀
    private final String mode;
    private final int    round;

    private int index        = 0;
    private int correctCount = 0;
    private int wrongCount   = 0;

    public QuizSession(String scriptType, String mode, int limit, int round) {
        this.mode     = mode;
        this.round    = round;
        this.allItems = KanaData.getList(scriptType);

        List<KanaData.KanaItem> full = new ArrayList<>(allItems);
        Collections.shuffle(full);

        // 아침 퀴즈면 limit 개수만, 전체면 46개 전부
        if (limit > 0 && limit < full.size())
            this.deck = new ArrayList<>(full.subList(0, limit));
        else
            this.deck = full;
    }

    /** 현재 문제 반환 */
    public KanaData.KanaItem currentItem() {
        return index < deck.size() ? deck.get(index) : null;
    }

    /** 객관식 보기 4개 (정답 1 + 같은 행 오답 3) 섞어서 반환 */
    public List<KanaData.KanaItem> getOptions() {
        KanaData.KanaItem correct = currentItem();
        if (correct == null) return new ArrayList<>();

        List<KanaData.KanaItem> opts = new ArrayList<>(
                KanaData.getDistractors(correct, allItems)
        );
        opts.add(correct);
        Collections.shuffle(opts);
        return opts;
    }

    /** 답 제출 — true: 정답 / false: 오답. 자동으로 다음 문제로 이동 */
    public boolean answer(String romaji) {
        KanaData.KanaItem item = currentItem();
        if (item == null) return false;
        boolean ok = item.romaji.equalsIgnoreCase(romaji.trim());
        if (ok) correctCount++;
        else    wrongCount++;
        index++;
        return ok;
    }

    public boolean isFinished()  { return index >= deck.size(); }
    public int getIndex()        { return index; }
    public int getDeckSize()     { return deck.size(); }
    public int getCorrectCount() { return correctCount; }
    public int getWrongCount()   { return wrongCount; }
    public String getMode()      { return mode; }
    public int getRound()        { return round; }
    public int getProgress()     { return (int)(index * 100f / deck.size()); }
}