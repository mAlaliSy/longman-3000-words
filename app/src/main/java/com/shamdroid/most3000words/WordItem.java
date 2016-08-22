package com.shamdroid.most3000words;

/**
 * Created by Mohammad Al-Ali on 03/12/15.
 */
public class WordItem {
    int id ;
    boolean isFav;
    String wordEn , wordAr;

    public WordItem(int id , String wordEn, String wordAr , boolean isFav) {
        this.id = id;
        this.isFav = isFav;
        this.wordEn = wordEn;
        this.wordAr = wordAr;
    }

    public int getId() {
        return id;
    }

    public boolean isFav() {
        return isFav;
    }

    public String getWordEn() {
        return wordEn;
    }

    public String getWordAr() {
        return wordAr;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsFav(boolean isFav) {
        this.isFav = isFav;
    }

    public void setWordEn(String wordEn) {
        this.wordEn = wordEn;
    }

    public void setWordAr(String wordAr) {
        this.wordAr = wordAr;
    }
}
