package ru.niv.bible.mediator.contract;

import ru.niv.bible.component.immutable.box.Speech;

public interface FragmentContract {

    interface MainChild {
        Speech getSpeech();
    }

    interface Settings {
        Speech getSpeech();
    }

}