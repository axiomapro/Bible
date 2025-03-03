package ru.ampstudy.bible.mediator.contract;

import ru.ampstudy.bible.component.immutable.box.Speech;

public interface FragmentContract {

    interface MainChild {
        Speech getSpeech();
    }

    interface Settings {
        Speech getSpeech();
    }

}