package ru.niv.bible.basic.component;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ru.niv.bible.basic.receiver.AlertReceiver;

public class Alarm {

    private final Context context;

    public Alarm(Context context) {
        this.context = context;
    }

    public boolean checkAlarm(int id) {
        Intent intent = new Intent(context, AlertReceiver.class);
        return (PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public void setRepeating(int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("alarmId",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        long interval = 5 * 24 * 60 * 60 * 1000;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,interval,interval,pendingIntent);
    }

    public void cancel(int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}
