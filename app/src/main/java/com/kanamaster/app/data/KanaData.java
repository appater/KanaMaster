package com.kanamaster.app.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KanaData {

    public static class KanaItem {
        public final String kana;
        public final String romaji;
        public final String group;

        public KanaItem(String kana, String romaji, String group) {
            this.kana   = kana;
            this.romaji = romaji;
            this.group  = group;
        }
    }

    public static final List<KanaItem> HIRAGANA = Arrays.asList(
            new KanaItem("あ","a","vowel"), new KanaItem("い","i","vowel"),
            new KanaItem("う","u","vowel"), new KanaItem("え","e","vowel"), new KanaItem("お","o","vowel"),
            new KanaItem("か","ka","k"),    new KanaItem("き","ki","k"),
            new KanaItem("く","ku","k"),    new KanaItem("け","ke","k"),    new KanaItem("こ","ko","k"),
            new KanaItem("さ","sa","s"),    new KanaItem("し","shi","s"),
            new KanaItem("す","su","s"),    new KanaItem("せ","se","s"),    new KanaItem("そ","so","s"),
            new KanaItem("た","ta","t"),    new KanaItem("ち","chi","t"),
            new KanaItem("つ","tsu","t"),   new KanaItem("て","te","t"),    new KanaItem("と","to","t"),
            new KanaItem("な","na","n_row"),new KanaItem("に","ni","n_row"),
            new KanaItem("ぬ","nu","n_row"),new KanaItem("ね","ne","n_row"),new KanaItem("の","no","n_row"),
            new KanaItem("は","ha","h"),    new KanaItem("ひ","hi","h"),
            new KanaItem("ふ","fu","h"),    new KanaItem("へ","he","h"),    new KanaItem("ほ","ho","h"),
            new KanaItem("ま","ma","m"),    new KanaItem("み","mi","m"),
            new KanaItem("む","mu","m"),    new KanaItem("め","me","m"),    new KanaItem("も","mo","m"),
            new KanaItem("や","ya","y"),    new KanaItem("ゆ","yu","y"),    new KanaItem("よ","yo","y"),
            new KanaItem("ら","ra","r"),    new KanaItem("り","ri","r"),
            new KanaItem("る","ru","r"),    new KanaItem("れ","re","r"),    new KanaItem("ろ","ro","r"),
            new KanaItem("わ","wa","w"),    new KanaItem("を","wo","w"),    new KanaItem("ん","n","w")
    );

    public static final List<KanaItem> KATAKANA = Arrays.asList(
            new KanaItem("ア","a","vowel"), new KanaItem("イ","i","vowel"),
            new KanaItem("ウ","u","vowel"), new KanaItem("エ","e","vowel"), new KanaItem("オ","o","vowel"),
            new KanaItem("カ","ka","k"),    new KanaItem("キ","ki","k"),
            new KanaItem("ク","ku","k"),    new KanaItem("ケ","ke","k"),    new KanaItem("コ","ko","k"),
            new KanaItem("サ","sa","s"),    new KanaItem("シ","shi","s"),
            new KanaItem("ス","su","s"),    new KanaItem("セ","se","s"),    new KanaItem("ソ","so","s"),
            new KanaItem("タ","ta","t"),    new KanaItem("チ","chi","t"),
            new KanaItem("ツ","tsu","t"),   new KanaItem("テ","te","t"),    new KanaItem("ト","to","t"),
            new KanaItem("ナ","na","n_row"),new KanaItem("ニ","ni","n_row"),
            new KanaItem("ヌ","nu","n_row"),new KanaItem("ネ","ne","n_row"),new KanaItem("ノ","no","n_row"),
            new KanaItem("ハ","ha","h"),    new KanaItem("ヒ","hi","h"),
            new KanaItem("フ","fu","h"),    new KanaItem("ヘ","he","h"),    new KanaItem("ホ","ho","h"),
            new KanaItem("マ","ma","m"),    new KanaItem("ミ","mi","m"),
            new KanaItem("ム","mu","m"),    new KanaItem("メ","me","m"),    new KanaItem("モ","mo","m"),
            new KanaItem("ヤ","ya","y"),    new KanaItem("ユ","yu","y"),    new KanaItem("ヨ","yo","y"),
            new KanaItem("ラ","ra","r"),    new KanaItem("リ","ri","r"),
            new KanaItem("ル","ru","r"),    new KanaItem("レ","re","r"),    new KanaItem("ロ","ro","r"),
            new KanaItem("ワ","wa","w"),    new KanaItem("ヲ","wo","w"),    new KanaItem("ン","n","w")
    );

    public static List<KanaItem> getList(String scriptType) {
        return "kata".equals(scriptType)
                ? new ArrayList<>(KATAKANA)
                : new ArrayList<>(HIRAGANA);
    }

    public static List<KanaItem> getDistractors(KanaItem correct, List<KanaItem> pool) {
        List<KanaItem> sameGroup = new ArrayList<>();
        List<KanaItem> others    = new ArrayList<>();

        for (KanaItem item : pool) {
            if (item.kana.equals(correct.kana)) continue;
            if (item.group.equals(correct.group)) sameGroup.add(item);
            else                                  others.add(item);
        }
        Collections.shuffle(sameGroup);
        Collections.shuffle(others);

        List<KanaItem> result = new ArrayList<>(sameGroup);
        result.addAll(others);
        return result.subList(0, Math.min(3, result.size()));
    }
}