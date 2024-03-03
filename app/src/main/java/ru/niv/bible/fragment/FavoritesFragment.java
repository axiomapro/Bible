package ru.niv.bible.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Convert;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.mediator.contract.DialogContract;
import ru.niv.bible.mediator.contract.MessageContract;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.contract.ResultContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.list.item.Item;
import ru.niv.bible.mediator.view.Rview;

public class FavoritesFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private Rview rview;
    private RecyclerView recyclerView;
    private ImageView ivFolder, ivBook, ivBack, ivFavorites;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites,container,false);
        initViews(v);
        initClasses();
        initRecyclerView(1);
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        ivFolder = v.findViewById(R.id.imageViewFolder);
        ivBook = v.findViewById(R.id.imageViewBook);
        ivFavorites = v.findViewById(R.id.imageViewFavorites);
        ivBack = v.findViewById(R.id.imageViewBack);
        fab = v.findViewById(R.id.fab);
        recyclerView = v.findViewById(R.id.recyclerView);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
    }

    private void initRecyclerView(int tab) {
        String screen = tab == 1?Config.recyclerView().favoritesFolder():Config.recyclerView().favoritesBook();

        rview.setRecyclerView(recyclerView);
        rview.initialize(screen, mediator.get().list().favorite(tab), new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                mediator.transition(getParentFragmentManager(),FolderFragment.newInstance(rview.getItem(position).getName(),tab,rview.getItem(position).getId()),Config.screen().folder(), Static.RIGHT_ANIMATION,true,true);
            }

            @Override
            public void longClick(int position) {
                if (tab != 1 || rview.getItem(position).getId() == 0) return;

                mediator.show().dialog().folder(false, mediator.form().send().folder(rview.getItem(position).getName()), new DialogContract.Action() {
                    @Override
                    public void delete() {
                        mediator.show().dialog().delete(getString(R.string.dialog_delete_folder), () -> {
                            mediator.handler().toggle().deleteFolder(rview.getItem(position).getId());
                            rview.removeItem(position);
                        });
                    }

                    @Override
                    public void result(ContentValues cvDialog, AlertDialog dialog, MessageContract listenerMessage) {
                        boolean form = mediator.form().check(new String[]{"name"},cvDialog,listenerMessage);
                        if (!form) return;

                        mediator.handler().edit().favorite(rview.getItem(position).getId(),cvDialog, new ResultContract() {
                            @Override
                            public void extra(ContentValues cv) {
                                dialog.dismiss();

                                rview.getItem(position).setName(cvDialog.getAsString("name"));
                                rview.updateItem(position);
                            }

                            @Override
                            public void duplicate() {
                                ((MainActivity) getActivity()).message(getString(R.string.duplicate_folder));
                            }
                        });
                    }
                });
            }

            @Override
            public void checkBox(int position, boolean isChecked) {

            }
        });

        if (tab == 1) {
            int top = 20;
            int bottom = 10;
            float scale = getResources().getDisplayMetrics().density;
            int topAsPixels = (int) (top * scale + 0.5f);
            int bottomAsPixels = (int) (bottom * scale + 0.5f);
            recyclerView.setPadding(0,topAsPixels,0,bottomAsPixels);
        } else recyclerView.setPadding(0,0,0,0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListeners() {
        ivFolder.setOnTouchListener((v1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) ivFolder.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) ivFolder.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });
        ivBook.setOnTouchListener((v2, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) ivBook.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) ivBook.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });
        ivFavorites.setOnTouchListener((v3, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) ivFavorites.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) ivFavorites.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });

        ivBack.setOnClickListener(this);
        ivFolder.setOnClickListener(this);
        ivBook.setOnClickListener(this);
        ivFavorites.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    private void tabClick(int tab) {
        if (tab == 1) {
            fab.setVisibility(View.VISIBLE);
            ivFolder.setImageResource(R.drawable.ic_folder_active);
            ivBook.setImageResource(R.drawable.ic_book);
        } else {
            fab.setVisibility(View.GONE);
            ivFolder.setImageResource(R.drawable.ic_folder);
            ivBook.setImageResource(R.drawable.ic_book_active);
        }
        initRecyclerView(tab);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.imageViewFolder:
                tabClick(1);
                break;
            case R.id.imageViewBook:
                tabClick(2);
                break;
            case R.id.imageViewFavorites:
                mediator.transition(getParentFragmentManager(),FolderFragment.newInstance(getString(R.string.favorites),3,0),Config.screen().folder(),Static.RIGHT_ANIMATION,true,true);
                break;
            case R.id.fab:
                add();
                break;
        }
    }

    public void add() {
        mediator.show().dialog().folder(true, mediator.form().send().folder(null), new DialogContract.Action() {
            @Override
            public void delete() {

            }

            @Override
            public void result(ContentValues cvDialog, AlertDialog dialog, MessageContract listenerMessage) {
                boolean form = mediator.form().check(new String[]{"name"},cvDialog,listenerMessage);
                if (!form) return;

                mediator.handler().add().folder(cvDialog, new ResultContract() {
                    @Override
                    public void extra(ContentValues cv) {
                        dialog.dismiss();
                        rview.addItem(cv.getAsInteger("position"),new Item().favorites(cv.getAsInteger("id"), cvDialog.getAsString("name"), 0));
                    }

                    @Override
                    public void duplicate() {
                        ((MainActivity) getActivity()).message(getString(R.string.duplicate_folder));
                    }
                });
            }
        });
    }
}