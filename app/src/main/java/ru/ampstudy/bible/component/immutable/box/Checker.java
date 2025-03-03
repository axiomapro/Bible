package ru.ampstudy.bible.component.immutable.box;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Checker {

    private final Context context;

    public Checker(Context context) {
        this.context = context;
    }

    public boolean gmail() {
        try {
            context.getPackageManager().getApplicationInfo(Static.gmailPackage, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean internet() {
        boolean wifi = false;
        boolean edge = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos) {
            if (info.getTypeName().equalsIgnoreCase("WIFI")) {
                if (info.isConnected()) wifi = true;
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (info.isConnected()) edge = true;
            }
        }

        return wifi||edge;
    }

}
