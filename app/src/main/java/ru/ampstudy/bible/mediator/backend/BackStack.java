package ru.ampstudy.bible.mediator.backend;

import androidx.fragment.app.FragmentManager;

import ru.ampstudy.bible.component.immutable.box.Config;

public class BackStack {

    private FragmentManager fm;
    private boolean isMain;

    public void setFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }

    public void lamp(String screen,boolean status) {
        if (screen.equals(Config.screen().main()) && isMain != status) isMain = status;
    }

    public boolean checkExit() {
        return !isMain;
    }

    public boolean checkBack() {
        boolean result = true;
        for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
            String tag = getGeneralScreen(fm.getBackStackEntryAt(i).getName());
            if (tag.equals(Config.screen().main())) {
                if (isMain) break;
                else {
                    result = false;
                    fm.popBackStackImmediate();
                }
            }
        }
        return result;
    }

    private String getGeneralScreen(String tagTop) {
        String result = null;
        if (tagTop.equals(Config.screen().main())) result = Config.screen().main();
        return result;
    }

}