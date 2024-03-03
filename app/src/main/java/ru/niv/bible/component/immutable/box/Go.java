package ru.niv.bible.component.immutable.box;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;

import ru.niv.bible.R;
import ru.niv.bible.mediator.contract.MessageContract;

public class Go {

    private final Context context;

    public Go(Context context) {
        this.context = context;
    }

    public void gmail(String subject, String message, String to) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        intent.setPackage(Static.gmailPackage);
        context.startActivity(intent);
    }

    public void mail(String subject, String message, String to, MessageContract listener) {
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

    public void twitter(String app,MessageContract listener) {
        try {
            String text = URLEncoder.encode(context.getString(R.string.twitter_text)+":\n"+app+"\n" + context.getString(R.string.twitter_hashtag));
            String url = "http://www.twitter.com/intent/tweet?text="+text;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (Exception e) {
            listener.message(context.getString(R.string.twitter_not_installed));
        }
    }

    public void browser(String url,String message,MessageContract listener) {
        execute(url,message,listener);
    }

    public void instagram(MessageContract listener) {
        execute(Config.url().instagram(),context.getString(R.string.browser_not_found),listener);
    }

    public void policy(MessageContract listener) {
        execute(Config.url().policy(),context.getString(R.string.browser_not_found),listener);
    }

    public void googlePlay(MessageContract listener) {
        execute("market://details?id="+context.getPackageName(),context.getString(R.string.google_play_not_found),listener);
    }

    private void execute(String url, String message, MessageContract listener) {
        try {
            Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
            context.startActivity(browse);
        } catch (Exception e) {
            listener.message(message);
        }
    }

}