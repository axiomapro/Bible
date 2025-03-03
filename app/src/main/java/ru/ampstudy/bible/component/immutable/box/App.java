package ru.ampstudy.bible.component.immutable.box;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.cleversolutions.ads.AdType;
import com.cleversolutions.ads.ConsentFlow;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.android.CAS;

import ru.ampstudy.bible.BuildConfig;

public class App extends Application {

    public static MediationManager adManager;
    public static final String TAG = "CAS Sample";
    public static final String CAS_ID = "ru.ampstudy.bible";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Initialize SDK
        adManager = CAS.buildManager()
                .withManagerId(CAS_ID)
                .withAdTypes(AdType.Banner)
                // .withTestAdMode(BuildConfig.DEBUG)
                .withConsentFlow(
                        new ConsentFlow(true)
                                .withDismissListener(status -> {
                                    Log.d(TAG, "Consent Flow dismissed");
                                })
                )
                .withCompletionListener(config -> {
                    if (config.getError() == null) {
                        Log.d(TAG, "Ad manager initialized");
                    } else {
                        Log.d(TAG, "Ad manager initialization failed: " + config.getError());
                    }
                })
                .build(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Static.notificationChannel,"Channel 1", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Description");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}