package ru.ampstudy.bible.component.mutable.contentvalue;

import android.content.ContentValues;

public class ToggleCV {

    private final ContentValues cv;

    public ToggleCV() {
        cv = new ContentValues();
    }

    public ContentValues favorite(int favorite) {
        cv.clear();
        cv.put("favorite",favorite);
        return cv;
    }

    public ContentValues status(int status) {
        cv.clear();
        cv.put("status",status);
        return cv;
    }

    public ContentValues del(int del) {
        cv.clear();
        cv.put("del",del);
        return cv;
    }

    public ContentValues del() {
        cv.clear();
        cv.put("del",1);
        return cv;
    }

}