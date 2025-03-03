package ru.ampstudy.bible.component.immutable.box;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class Content {

    private final Context context;

    public Content(Context context) {
        this.context = context;
    }

    public void download(String filename,String link) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Config.url().site()+"/"+link);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(filename);
        request.setDescription("Description");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        downloadManager.enqueue(request);
    }

    public void share(String title,String text,String titlePopup) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, titlePopup));
    }

}