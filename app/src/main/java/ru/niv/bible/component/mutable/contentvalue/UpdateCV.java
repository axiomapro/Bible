package ru.niv.bible.component.mutable.contentvalue;

import android.content.ContentValues;

import ru.niv.bible.model.action.Update;

public class UpdateCV {

    private final ContentValues cv;

    public UpdateCV() {
        cv = new ContentValues();
    }

    public ContentValues favorite(int folder, String note,int favorite,int underline,int color) {
        cv.clear();
        cv.put("folder",folder);
        cv.put("note",note);
        cv.put("favorite",favorite);
        cv.put("underline",underline);
        cv.put("color",color);
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

}