package ru.niv.bible.mvp.presenter;

import android.content.Context;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.mvp.contract.ReadingPlanContainerContract;
import ru.niv.bible.mvp.model.ReadingPlanContainerModel;
import ru.niv.bible.mvp.view.ReadingPlanContainerFragment;

public class ReadingPlanContainerPresenter {

    private final ReadingPlanContainerContract.View view;
    private final Context context;
    private final ReadingPlanContainerModel model;
    private final Dialog dialog;

    public ReadingPlanContainerPresenter(ReadingPlanContainerContract.View view) {
        this.view = view;
        this.context = ((ReadingPlanContainerFragment) view).getContext();
        model = new ReadingPlanContainerModel(context);
        dialog = new Dialog(context);
    }

    public void getData(int id, ReadingPlanContainerModel.Data listener) {
        model.getData(id,listener);
    }

    public float getProgress(int plan,int type) {
        return model.getProgress(plan,type);
    }

    public int getTotalLeftDay(int plan,int type,int day) {
        return model.getTotalLeftDay(plan,type,day);
    }

    public void dialog(int id,int type,int total) {
        dialog.readingPlan(model.getListDialog(id,type,1),total, new Dialog.ReadingPlan() {
            @Override
            public void onNumber(int number,Dialog.GetList listener) {
                listener.onList(model.getListDialog(id,type,number));
            }

            @Override
            public void onResult(int number) {
                model.active(id,type,number);
                view.redraw();
            }
        });
    }

    public void dialogInactive(int id) {
        dialog.delete(context.getString(R.string.dialog_stop_reading_plan), () -> {
            model.inactive(id);
            view.redraw();
        });
    }

}
