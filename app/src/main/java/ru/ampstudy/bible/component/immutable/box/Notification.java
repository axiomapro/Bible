package ru.ampstudy.bible.component.immutable.box;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import ru.ampstudy.bible.MainActivity;

public class Notification {

    private final Context context;

    public Notification(Context context) {
        this.context = context;
    }

    public void show(String title, String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                .setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent,PendingIntent.FLAG_IMMUTABLE);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        android.app.Notification notification = new NotificationCompat.Builder(context, Static.NOTIFICATION_CHANNEL)
                .setSmallIcon(0)
                .setColor(ContextCompat.getColor(context, 0))
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
