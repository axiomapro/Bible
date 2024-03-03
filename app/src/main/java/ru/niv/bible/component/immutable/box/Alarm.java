package ru.niv.bible.component.immutable.box;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import ru.niv.bible.component.mutable.receiver.AlertReceiver;

public class Alarm {

    private final Context context;

    public Alarm(Context context) {
        this.context = context;
    }

    public boolean checkAlarm(int id,boolean readingPlan) {
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, readingPlan?id:(10000+id), intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, readingPlan?id:(10000+id), intent, PendingIntent.FLAG_NO_CREATE);
        }
        return (pendingIntent != null);
    }

    public void set(int id,long time,boolean readingPlan) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("alarmId",id);
        intent.putExtra("type",readingPlan?"reading plan":"daily verse");
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context,readingPlan?id:(10000+id),intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context,readingPlan?id:(10000+id),intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        }
    }

    public long getTime(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));
        return calendar.getTimeInMillis();
    }

    public String restoreTime(String time) {
        String[] cutTime = time.split(":");
        String hour = cutTime[0];
        String minute = cutTime[1];
        return (hour.length() > 1?hour:"0"+hour)+":"+(minute.length() > 1?minute:"0"+minute);
    }

    public void cancel(int id,boolean readingPlan) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context,readingPlan?id:(10000+id),intent,PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context,readingPlan?id:(10000+id),intent,0);
        }
        alarmManager.cancel(pendingIntent);
    }

}
