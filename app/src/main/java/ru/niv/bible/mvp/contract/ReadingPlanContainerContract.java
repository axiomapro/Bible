package ru.niv.bible.mvp.contract;

public interface ReadingPlanContainerContract {

    interface View {
        void redraw();
        void notification(String notification);
    }

    interface Presenter {

    }

}
