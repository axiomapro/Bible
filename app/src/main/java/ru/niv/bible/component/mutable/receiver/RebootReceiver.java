package ru.niv.bible.component.mutable.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import ru.niv.bible.component.immutable.box.Alarm;
import ru.niv.bible.model.data.Data;

public class RebootReceiver extends BroadcastReceiver {

    @SuppressLint("Range")
    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = new Alarm(context);
        Data data = new Data(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !alarmManager.canScheduleExactAlarms()) return;

        Cursor cursor = data.getNotificationsReadingPlan();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String notification = cursor.getString(cursor.getColumnIndex("notification"));
            alarm.set(id,alarm.getTime(notification),true);
        }
        cursor.close();

        Cursor cursorDailyVerse = data.getNotificationsDailyVerse();
        while (cursorDailyVerse.moveToNext()) {
            int id = cursorDailyVerse.getInt(cursorDailyVerse.getColumnIndex("id"));
            String notification = cursorDailyVerse.getString(cursorDailyVerse.getColumnIndex("notification"));
            alarm.set(id,alarm.getTime(notification),false);
        }
        cursorDailyVerse.close();
    }
}