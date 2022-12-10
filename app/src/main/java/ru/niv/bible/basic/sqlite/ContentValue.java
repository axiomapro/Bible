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

    public ContentValues addFavorite(int folder,int textId,String note,int favorite,int underline,int color,String date) {
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

    public ContentValues editFavorite(int folder, String note,int favorite,int underline,int color) {
        cv.clear();
        cv.put("folder",folder);
        cv.put("note",note);
        cv.put("favorite",favorite);
        cv.put("underline",underline);
        cv.put("color",color);
        return cv;
    }

    public ContentValues addNote(String name,String text,String date) {
        cv.clear();
        cv.put("name",name);
        cv.put("text",text);
        cv.put("date",date);
        cv.put("del",0);
        return cv;
    }

    public ContentValues editNote(String name,String text) {
        cv.clear();
        cv.put("name",name);
        cv.put("text",text);
        return cv;
    }

    public ContentValues readingPlanActive(int type,String start,String finish) {
        cv.clear();
        cv.put("type",type);
        cv.put("start",start);
        cv.put("finish",finish);
        cv.put("status",1);
        return cv;
    }

    public ContentValues readingPlanInactive() {
        cv.clear();
        cv.put("type",0);
        cv.put("start",(String) null);
        cv.put("finish",(String) null);
        cv.put("notification",(String) null);
        cv.put("status",0);
        return cv;
    }

    public ContentValues notificationReadingPlan(String notification) {
        cv.clear();
        cv.put("notification",notification);
        return cv;
    }

    public ContentValues addDailyVerse(String name,String chapters,String updated,String notification) {
        cv.clear();
        cv.put("name",name);
        cv.put("chapters",chapters);
        cv.put("sort",0);
        cv.put("updated",updated);
        cv.put("notification",notification);
        cv.put("del",0);
        return cv;
    }

    public ContentValues editDailyVerse(String name,String chapters,String updated,String notification) {
        cv.clear();
        cv.put("name",name);
        cv.put("chapters",chapters);
        cv.put("updated",updated);
        cv.put("notification",notification);
        return cv;
    }

    public ContentValues status(int status) {
        cv.clear();
        cv.put("status",status);
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
