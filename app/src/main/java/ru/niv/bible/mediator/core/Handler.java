package ru.niv.bible.mediator.core;

import android.content.Context;

import ru.niv.bible.model.action.Add;
import ru.niv.bible.model.action.Edit;
import ru.niv.bible.model.action.Toggle;

public class Handler {

    private final Context context;
    private Toggle toggle;
    private Add add;
    private Edit edit;

    public Handler(Context context) {
        this.context = context;
    }

    public Add add() {
        if (add == null) add = new Add(context);
        return add;
    }

    public Edit edit() {
        if (edit == null) edit = new Edit(context);
        return edit;
    }

    public Toggle toggle() {
        if (toggle == null) toggle = new Toggle(context);
        return toggle;
    }

}