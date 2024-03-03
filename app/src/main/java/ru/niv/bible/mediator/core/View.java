package ru.niv.bible.mediator.core;

import ru.niv.bible.mediator.view.Empty;
import ru.niv.bible.mediator.view.Rview;
import ru.niv.bible.mediator.view.Vp;

public class View {

    public Rview rview() {
        return new Rview();
    }

    public Vp vp() {
        return new Vp();
    }

    public Empty empty() {
        return new Empty();
    }

}