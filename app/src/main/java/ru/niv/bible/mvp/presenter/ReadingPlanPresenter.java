package ru.niv.bible.mvp.presenter;

import java.util.List;

import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.ReadingPlanContract;
import ru.niv.bible.mvp.model.ReadingPlanModel;
import ru.niv.bible.mvp.view.ReadingPlanFragment;

public class ReadingPlanPresenter implements ReadingPlanContract.Presenter {

    private final ReadingPlanModel model;

    public ReadingPlanPresenter(ReadingPlanContract.View view) {
        model = new ReadingPlanModel(((ReadingPlanFragment) view).getContext());
    }

    public List<Item> getList() {
        return model.getList();
    }

}
