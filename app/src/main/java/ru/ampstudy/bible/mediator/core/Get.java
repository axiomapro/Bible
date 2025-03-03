package ru.ampstudy.bible.mediator.core;

import android.content.Context;

import ru.ampstudy.bible.model.data.Data;
import ru.ampstudy.bible.model.data.ListData;

public class Get {

    private final Context context;
    private ListData listData;
    private Data data;

    public Get(Context context) {
        this.context = context;
    }

    public ListData list() {
        if (listData == null) listData = new ListData(context);
        return listData;
    }

    public Data data() {
        if (data == null) data = new Data(context);
        return data;
    }

}