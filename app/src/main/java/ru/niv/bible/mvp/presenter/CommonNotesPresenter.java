package ru.niv.bible.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.CommonNotesContract;
import ru.niv.bible.mvp.model.CommonNotesModel;
import ru.niv.bible.mvp.model.FavoritesModel;
import ru.niv.bible.mvp.view.CommonNotesFragment;

public class CommonNotesPresenter implements CommonNotesContract.Presenter {

    private CommonNotesContract.View view;
    private final Context context;
    private final CommonNotesModel model;
    private final Dialog dialog;
    private final Converter converter;

    public CommonNotesPresenter(CommonNotesContract.View view) {
        this.view = view;
        this.context = ((CommonNotesFragment) view).getContext();
        model = new CommonNotesModel(context);
        dialog = new Dialog(context);
        converter = new Converter();
    }

    public List<Item> getList() {
        return model.getList();
    }

    public void dialog(boolean statusAdd,String name,String note,int id,int position) {
        if (!statusAdd && id == 0) return; // edit
        dialog.commonNotes(statusAdd,name,note, new Dialog.Note() {
            @Override
            public void onResult(String name, String note, AlertDialog dialog, Dialog.Message listener) {
                String correctName = converter.getNameUppercase(name);
                String correctNote = converter.getTextUppercase(note);
                if (TextUtils.isEmpty(correctName)) listener.onMessage(context.getString(R.string.write_the_name));
                else if (TextUtils.isEmpty(correctNote)) listener.onMessage(context.getString(R.string.write_the_text));
                else {
                    if (statusAdd) {
                        model.add(correctName, correctNote, new FavoritesModel.Action() {
                            @Override
                            public void onDuplicate() {
                                listener.onMessage(context.getString(R.string.duplicate_note));
                            }

                            @Override
                            public void onSuccess(int id,int newPosition) {
                                dialog.dismiss();
                                view.addItem(id,correctName,correctNote,converter.getDatetime(),newPosition);
                            }
                        });
                    } else {
                        model.edit(correctName, correctNote, id, new FavoritesModel.Action() {
                            @Override
                            public void onDuplicate() {
                                listener.onMessage(context.getString(R.string.duplicate_note));
                            }

                            @Override
                            public void onSuccess(int id,int newPosition) {
                                dialog.dismiss();
                                view.updateItem(correctName,correctNote,position);
                            }
                        });
                    }
                }
            }

            @Override
            public void onDelete() {
                dialog.delete(context.getString(R.string.dialog_delete_note), () -> {
                    model.delete(id);
                    view.deleteItem(position);
                });
            }
        });
    }

}
