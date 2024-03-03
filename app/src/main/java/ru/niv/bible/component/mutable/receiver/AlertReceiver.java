package ru.niv.bible.component.mutable.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Alarm;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.model.data.Data;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = new Alarm(context);
        String type =intent.getStringExtra("type");
        int alarmId = intent.getIntExtra("alarmId",1);
        if (type.equals("reading plan")) { // every day
            alarm.set(alarmId,System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,true);
            notification(context,context.getString(R.string.notification_title),context.getString(R.string.notification_text));
        } else { // 5 days
            alarm.set(alarmId,System.currentTimeMillis() + AlarmManager.INTERVAL_DAY * 5,false);

            Data data = new Data(context);
            data.refreshDailyVerse(alarmId, (chapter, page, position, chapterName, text) -> {
                String title = chapterName+" "+page+":"+position;
                notification(context,title,text.replaceAll("\\<[^>]*>",""));
            });
        }
    }

    private void notification(Context context,String title,String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                .setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent, 0);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        android.app.Notification notification = new NotificationCompat.Builder(context, Static.notificationChannel)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        notificationManagerCompat.notify(1,notification);
    }

}