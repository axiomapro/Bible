package ru.niv.bible.basic.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import ru.niv.bible.basic.component.Alarm;
import ru.niv.bible.mvp.model.DailyVerseModel;
import ru.niv.bible.mvp.model.ReadingPlanModel;

public class RebootReceiver extends BroadcastReceiver {

    @SuppressLint("Range")
    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = new Alarm(context);
        ReadingPlanModel model = new ReadingPlanModel(context);
        Cursor cursor = model.getNotifications();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String notification = cursor.getString(cursor.getColumnIndex("notification"));
            alarm.set(id,alarm.getTime(notification),true);
        }
        cursor.close();

        DailyVerseModel modelDailyVerse = new DailyVerseModel(context);
        Cursor cursorDailyVerse = modelDailyVerse.getNotifications();
        while (cursorDailyVerse.moveToNext()) {
            int id = cursorDailyVerse.getInt(cursorDailyVerse.getColumnIndex("id"));
            String notification = cursorDailyVerse.getString(cursorDailyVerse.getColumnIndex("notification"));
            alarm.set(id,alarm.getTime(notification),false);
        }
        cursorDailyVerse.close();
    }
}
