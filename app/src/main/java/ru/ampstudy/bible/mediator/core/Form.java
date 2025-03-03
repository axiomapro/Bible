package ru.ampstudy.bible.mediator.core;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;

import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.mutable.contentvalue.ExtraCV;
import ru.ampstudy.bible.component.mutable.contentvalue.SendCV;
import ru.ampstudy.bible.mediator.contract.MessageContract;

public class Form {

    private final Context context;
    private SendCV send;
    private ExtraCV extra;

    public Form(Context context) {
        this.context = context;
    }

    public SendCV send() {
        if (send == null) send = new SendCV();
        return send;
    }

    public ExtraCV extra() {
        if (extra == null) extra = new ExtraCV();
        return extra;
    }

    public boolean check(String[] args, ContentValues cv, MessageContract listener) {
        boolean result = false;
        if (Arrays.asList(args).contains("name") && TextUtils.isEmpty(cv.getAsString("name"))) listener.message(context.getString(R.string.write_the_name));
        else if (Arrays.asList(args).contains("name") && cv.getAsString("name").equals(context.getString(R.string.default_folder))) listener.message(context.getString(R.string.name_is_not_available));
        else if (Arrays.asList(args).contains("text") && TextUtils.isEmpty(cv.getAsString("text"))) listener.message(context.getString(R.string.write_the_text));
        else result = true;
        return result;
    }

}