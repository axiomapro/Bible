package ru.niv.bible.component.immutable.box;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import ru.niv.bible.MainActivity;

public class Permission {

    private final Context context;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 1;

    public Permission(Context context) {
        this.context = context;
    }

    public boolean check(String permission) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
        }
        return result;
    }

    public void show(String permission,int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                ((MainActivity) context).requestPermissions(new String[]{permission},code);
            }
        }
    }

}