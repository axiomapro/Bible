package ru.niv.bible.mvp.contract;

public interface MainContract {

    interface View {
        void message(String message);
        void updateChapterAndPage(String chapter,int page);
    }

    interface Presenter {

    }

}
