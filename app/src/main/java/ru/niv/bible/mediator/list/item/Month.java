package ru.niv.bible.mediator.list.item;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Month extends ExpandableGroup<Day> {

    public Month(String title, List<Day> items) {
        super(title, items);
    }

}