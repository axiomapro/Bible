package ru.niv.bible.mediator.contract;

public interface RequestContract {

    void success(String response);
    void error(String message);

}