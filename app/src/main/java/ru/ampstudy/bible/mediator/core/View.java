package ru.ampstudy.bible.mediator.core;

import ru.ampstudy.bible.mediator.view.Empty;
import ru.ampstudy.bible.mediator.view.Rview;
import ru.ampstudy.bible.mediator.view.Vp;

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