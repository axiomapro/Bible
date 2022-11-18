package ru.niv.bible.basic.sqlite;

import android.content.ContentValues;

public class ContentValue {

    private final ContentValues cv;

    public ContentValue() {
        cv = new ContentValues();
    }

    public ContentValues addFolder(String name) {
        cv.clear();
        cv.put("name",name);
        cv.put("del",0);
        return cv;
    }

    public ContentValues editFolder(String name) {
        cv.clear();
        cv.put("name",name);
        return cv;
    }

    public ContentValues addRead(int position,String date) {
        cv.clear();
        cv.put("position",position);
        cv.put("date",date);
        cv.put("del",0);
        return cv;
    }

    public ContentValues addFavorite(int folder,int textId,int favorite,int underline,int color,String date) {
        cv.clear();
        cv.put("folder",folder);
        cv.put("text_id",textId);
        cv.put("favorite",favorite);
        cv.put("underline",underline);
        cv.put("note",(String) null);
        cv.put("color",color);
        cv.put("date",date);
        cv.put("del",0);
        return cv;
    }

    public ContentValues editFavorite(int folder,int favorite,int underline,int color) {
        cv.clear();
        cv.put("folder",folder);
        cv.put("favorite",favorite);
        cv.put("underline",underline);
        cv.put("color",color);
        return cv;
    }

    public ContentValues position(int position) {
        cv.clear();
        cv.put("position",position);
        return cv;
    }

    public ContentValues delete() {
        cv.clear();
        cv.put("del",1);
        return cv;
    }

}
