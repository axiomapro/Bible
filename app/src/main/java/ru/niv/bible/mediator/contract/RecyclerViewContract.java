package ru.niv.bible.mediator.contract;

public interface RecyclerViewContract {

    interface Click {
        void click(int position);
        void longClick(int position);
        void checkBox(int position,boolean isChecked);
    }

    interface Scroll {
        void onEnd();
    }

    interface Empty {
        void visible(boolean status);
    }

    interface DailyVerse {
        void click(int position);
        void longClick(int position);
        void share(int position);
        void copy(int position);
        void edit(int position);
        void refresh(int position);
    }

    interface ReadingPlanMaterial {
        void link(String link);
        void checkBox(int position,int day,boolean status);
    }

}