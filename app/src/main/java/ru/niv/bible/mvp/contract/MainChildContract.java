package ru.niv.bible.mvp.contract;

public interface MainChildContract {

    interface View {
        void onSetItem(String folderName,String type,String note,int folder,int value);
        void onAction(String type);
        void onScroll(int height);
        void restorePreviousList();
    }

    interface Presenter {

    }

}
