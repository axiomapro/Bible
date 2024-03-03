package ru.niv.bible.component.immutable.box;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Static.notificationChannel,"Channel 1", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Channel description");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

}
