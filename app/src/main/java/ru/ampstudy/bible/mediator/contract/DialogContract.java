package ru.ampstudy.bible.mediator.contract;

import android.content.ContentValues;

import androidx.appcompat.app.AlertDialog;

public interface DialogContract {

    interface List {
        void item();
    }

    interface Confirm {
        void agree();
    }

    interface Action {
        void delete();
        void result(ContentValues cvDialog, AlertDialog dialog, MessageContract listenerMessage);
    }

    interface Share {
        void twitter();
        void share();
    }

    interface Rate{
        void rate(String type);
    }

    interface ReadingPlanContainer {
        void number(int number,GetList listener);
        void result(int number);
    }

    interface Notification {
        void result(String time,boolean status);
    }

    interface GetList {
        void list(String listUpdate);
    }

}