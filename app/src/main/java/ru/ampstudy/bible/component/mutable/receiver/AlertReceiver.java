package ru.ampstudy.bible.component.mutable.receiver;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.ampstudy.bible.MainActivity;
import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Alarm;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.model.data.Data;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarmId", 1);
        String type = intent.getStringExtra("type");

        Alarm alarm = new Alarm(context);
        if (type.equals("reading plan")) { // every day
            alarm.set(alarmId, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, true);
            notification(context, context.getString(R.string.notification_title), context.getString(R.string.notification_text));
        } else { // 5 days
            alarm.set(alarmId, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY * 5, false);

            Data data = new Data(context);
            data.refreshDailyVerse(alarmId, (chapter, page, position, chapterName, text) -> {
                String title = chapterName + " " + page + ":" + position;
                notification(context, title, text.replaceAll("\\<[^>]*>", ""));
            });
        }
    }

    private void notification(Context context, String title, String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                .setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

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

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(1, notification);
        }
    }

}