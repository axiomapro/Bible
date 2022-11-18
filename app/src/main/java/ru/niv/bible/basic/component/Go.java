package ru.niv.bible.basic.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.niv.bible.R;

public class Go {

    private final Context context;

    public interface Message {
        void message(String message);
    }

    public Go(Context context) {
        this.context = context;
    }

    public void share(String title,String text,String titlePopup) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        context.startActivity(Intent.createChooser(intent, titlePopup));
    }

    public void browser(String url,String message,Message listener) {
        try {
            Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
            context.startActivity(browse);
        } catch (Exception e) {
            listener.message(message);
        }
    }

    public void gmail(String subject,String message,String to) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        intent.setPackage(Static.gmailPackage);
        context.startActivity(intent);
    }

    public void mail(String subject,String message,String to,Message listener) {
        try {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, message);
            email.setType("message/rfc822");
            context.startActivity(Intent.createChooser(email, context.getString(R.string.share_popup_email)));
        } catch (Exception e) {
            listener.message(context.getString(R.string.mail_app_not_found));
        }
    }

}
