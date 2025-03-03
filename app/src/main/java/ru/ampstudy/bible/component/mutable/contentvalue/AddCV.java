package ru.ampstudy.bible.component.mutable.contentvalue;

import android.content.ContentValues;

public class AddCV {

    private final ContentValues cv;

    public AddCV() {
        cv = new ContentValues();
    }

    public ContentValues folder(String name) {
        cv.clear();
        cv.put("name",name);
        cv.put("del",0);
        return cv;
    }

    public ContentValues favorite(int folder,int textId,String note,int favorite,int underline,int color,String date) {
        cv.clear();
        cv.put("folder",folder);
        cv.put("text_id",textId);
        cv.put("favorite",favorite);
        cv.put("underline",underline);
        cv.put("note",note);
        cv.put("color",color);
        cv.put("date",date);
        cv.put("del",0);
        return cv;
    }

    public ContentValues note(String name,String text,String date) {
        cv.clear();
        cv.put("name",name);
        cv.put("text",text);
        cv.put("date",date);
        cv.put("del",0);
        return cv;
    }

    public ContentValues dailyVerse(String name,String chapters,String updated,String notification) {
        cv.clear();
        cv.put("name",name);
        cv.put("chapters",chapters);
        cv.put("sort",0);
        cv.put("updated",updated);
        cv.put("notification",notification);
        cv.put("del",0);
        return cv;
    }

}