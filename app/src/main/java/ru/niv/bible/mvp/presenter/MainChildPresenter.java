package ru.niv.bible.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Sheet;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.MainChildContract;
import ru.niv.bible.mvp.model.MainChildModel;
import ru.niv.bible.mvp.view.MainChildFragment;

public class MainChildPresenter implements MainChildContract.Presenter {

    private final MainChildContract.View view;
    private final MainChildModel model;
    private final Sheet sheet;
    private final Converter converter;

    public MainChildPresenter(MainChildContract.View view) {
        model = new MainChildModel(((MainChildFragment) view).getContext());
        sheet = new Sheet();
        converter = new Converter();
        this.view = view;
    }

    public List<Item> getList(int chapter,int page) {
        return model.getList(chapter,page);
    }

    public int getCorrectPosition(int chapter,int page,int position) {
        return model.getCorrectPosition(chapter,page,position);
    }

    public void deleteFavorite(int textId) {
        model.deleteFavorite(textId);
    }

    public void setFavorite(int folder,int textId,String note,int favorite,int underline, int color) {
        model.setFavorite(folder,textId,note,favorite,underline,color);
    }

    public boolean getStateReadButton(int position) {
        return model.getStateReadButton(position);
    }

    public boolean toggleReadButton(int position) {
        return model.toggleReadButtonPosition(position);
    }

    public void initBottomSheet(View v) {
        Context context = v.getContext();
        sheet.setListFolder(model.getListFolder());
        sheet.initViews(v);
        sheet.setListener(new Sheet.BottomSheet() {
            @Override
            public void onSetItem(String folderName, String type, String note, int folder, int value) {
                view.onSetItem(folderName,type,note,folder,value);
            }

            @Override
            public void onAction(String type) {
                view.onAction(type);
            }

            @Override
            public void onScroll(int height) {
                view.onScroll(height);
            }

            @Override
            public void onHidden() {
                view.restorePreviousList();
            }

            @Override
            public void onSend(String name) {
                String correctName = converter.getNameUppercase(name);
                if (TextUtils.isEmpty(correctName)) sheet.message(v.getContext().getString(R.string.write_the_name));
                else if (correctName.equals(context.getString(R.string.default_folder))) sheet.message(context.getString(R.string.name_is_not_available));
                else {
                    model.add(correctName, new MainChildModel.Action() {
                        @Override
                        public void onDuplicate() {
                            sheet.message(context.getString(R.string.duplicate_folder));
                        }

                        @Override
                        public void onSuccess(int id, int newPosition) {
                            sheet.addFolder(id,correctName,newPosition);
                        }
                    });
                }
            }
        });
    }

    public void showAudio() {
        sheet.visibleBottomSheet(true);
        sheet.showAudio();
    }

    public void visibleBottomSheet(boolean status) {
        sheet.visibleBottomSheet(status);
    }

    public void visibleEdit(boolean status) {
        sheet.visibleEdit(status);
    }

    public void setNote(String note) {
        sheet.setNote(note);
    }

    public int getHeightBottomSheet() {
        return sheet.getHeightBottomSheet();
    }

}
