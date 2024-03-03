package ru.niv.bible.mediator.core;

import android.content.Context;

import ru.niv.bible.mediator.view.Dialog;
import ru.niv.bible.mediator.view.Sheet;

public class Show {

    private final Context context;
    private Dialog dialog;
    private Sheet sheet;

    public Show(Context context) {
        this.context = context;
    }

    public Dialog dialog() {
        if (dialog == null) dialog = new Dialog(context);
        return dialog;
    }

    public Sheet sheet() {
        if (sheet == null) sheet = new Sheet();
        return sheet;
    }

}