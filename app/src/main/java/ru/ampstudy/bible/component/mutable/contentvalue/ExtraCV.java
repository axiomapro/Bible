package ru.ampstudy.bible.component.mutable.contentvalue;

import android.content.ContentValues;

public class ExtraCV {

    private final ContentValues cv;

    public ExtraCV() {
        cv = new ContentValues();
    }

    public ContentValues dailyVerse(int id,int chapter, int page, int position, String chapterName, String text) {
        cv.clear();
        cv.put("id",id);
        cv.put("chapter",chapter);
        cv.put("page",page);
        cv.put("position",position);
        cv.put("chapterName",chapterName);
        cv.put("text",text);
        return cv;
    }

    public ContentValues status(int status) {
        cv.clear();
        cv.put("status",status);
        return cv;
    }

    public ContentValues delete() {
        cv.clear();
        cv.put("del",1);
        return cv;
    }

    public ContentValues id(int id) {
        cv.clear();
        cv.put("id",id);
        return cv;
    }

    public ContentValues position(int position) {
        cv.clear();
        cv.put("position",position);
        return cv;
    }

    public ContentValues item(int id, int position) {
        cv.clear();
        cv.put("id",id);
        cv.put("position",position);
        return cv;
    }

}