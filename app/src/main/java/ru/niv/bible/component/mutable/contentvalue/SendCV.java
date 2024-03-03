package ru.niv.bible.component.mutable.contentvalue;

import android.content.ContentValues;

public class SendCV {

    private final ContentValues cv;

    public SendCV() {
        cv = new ContentValues();
    }

    public SendCV folder(String name) {
        cv.clear();
        cv.put("name",name);
        return this;
    }

    public SendCV note(String name,String text) {
        cv.clear();
        cv.put("name",name);
        cv.put("text",text);
        return this;
    }

    public SendCV dailyVerse(String name,String chapters,String updated,String notification) {
        cv.clear();
        cv.put("name",name);
        cv.put("chapters",chapters);
        cv.put("updated",updated);
        cv.put("notification",notification);
        return this;
    }

    public SendCV dailyVerse(String name,String chapters,String notification) {
        cv.clear();
        cv.put("name",name);
        cv.put("chapters",chapters);
        cv.put("notification",notification);
        return this;
    }

    public ContentValues get() {
        return cv;
    }

}