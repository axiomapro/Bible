package ru.niv.bible.mvp.presenter;

import ru.niv.bible.mvp.contract.ReadingPlanMaterialContract;
import ru.niv.bible.mvp.model.ReadingPlanMaterialModel;
import ru.niv.bible.mvp.view.ReadingPlanMaterialFragment;

public class ReadingPlanMaterialPresenter {

    private final ReadingPlanMaterialModel model;

    public ReadingPlanMaterialPresenter(ReadingPlanMaterialContract.View view) {
        model = new ReadingPlanMaterialModel(((ReadingPlanMaterialFragment) view).getContext());
    }

    public void getData(int id, ReadingPlanMaterialModel.Data listener) {
        model.getData(id,listener);
    }

    public String getLinks(int plan,int type,int day) {
        return model.getLinks(plan,type,day);
    }

    public boolean isCheckBox(int plan,int type,int day) {
        return model.isCheckBox(plan,type,day);
    }

    public void updateItemsByDay(int plan,int type,int day,boolean status) {
        model.updateItemsByDay(plan,type,day,status);
    }

}
