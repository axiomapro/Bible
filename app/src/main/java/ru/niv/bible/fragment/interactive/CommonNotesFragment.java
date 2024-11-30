package ru.niv.bible.fragment.interactive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Convert;
import ru.niv.bible.component.immutable.box.Datetime;
import ru.niv.bible.component.immutable.box.Param;
import ru.niv.bible.mediator.contract.DialogContract;
import ru.niv.bible.mediator.contract.MessageContract;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.contract.ResultContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.list.item.Item;
import ru.niv.bible.mediator.view.Rview;

public class CommonNotesFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private Rview rview;
    private Datetime datetime;
    private Param param;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ImageView ivBack, ivClear, ivSearch, ivToggleView;
    private GridLayout glTitle, glSearch;
    private EditText etSearch;
    private boolean isVisibleSearch, isToggleView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common_notes,container,false);
        initViews(v);
        initClasses();
        setParams();
        initRecyclerView();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        recyclerView = v.findViewById(R.id.recyclerView);
        fab = v.findViewById(R.id.fab);
        ivBack = v.findViewById(R.id.imageViewBack);
        ivClear = v.findViewById(R.id.imageViewClear);
        ivSearch = v.findViewById(R.id.imageViewSearch);
        ivToggleView = v.findViewById(R.id.imageViewToggleView);
        glTitle = v.findViewById(R.id.gridLayoutTitle);
        glSearch = v.findViewById(R.id.gridLayoutSearch);
        etSearch = v.findViewById(R.id.editTextSearch);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
        datetime = new Datetime();
        param = new Param(getContext());
    }

    private void setParams() {
        isToggleView = param.getBoolean(Config.param().toggleView());
        ivToggleView.setImageResource(isToggleView?R.drawable.ic_list:R.drawable.ic_grid);
    }

    private void initRecyclerView() {
        rview.setRecyclerView(recyclerView);
        rview.initialize(isToggleView ? Config.recyclerView().commonNotesGrid() : Config.recyclerView().commonNotesList(), mediator.get().list().commonNotes(), isToggleView ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {

            }

            @Override
            public void longClick(int position) {
                mediator.show().dialog().commonNotes(false, mediator.form().send().note(rview.getItem(position).getName(), rview.getItem(position).getText()), new DialogContract.Action() {
                    @Override
                    public void delete() {
                        mediator.show().dialog().delete(getString(R.string.dialog_delete_note), () -> {
                            mediator.handler().toggle().execute(Config.table().note(),Config.column().del(),rview.getItem(position).getId());
                            rview.removeItem(position);
                        });
                    }

                    @Override
                    public void result(ContentValues cvDialog, AlertDialog dialog, MessageContract listenerMessage) {
                        boolean form = mediator.form().check(new String[]{"name","text"},cvDialog,listenerMessage);
                        if (!form) return;

                        mediator.handler().edit().commonNotes(rview.getItem(position).getId(),cvDialog, new ResultContract() {
                            @Override
                            public void extra(ContentValues cv) {
                                dialog.dismiss();

                                rview.getItem(position).setName(cvDialog.getAsString("name"));
                                rview.getItem(position).setText(cvDialog.getAsString("text"));
                                rview.updateItem(position);
                            }

                            @Override
                            public void duplicate() {
                                ((MainActivity) getActivity()).message(getString(R.string.duplicate_note));
                            }
                        });
                    }
                });
            }

            @Override
            public void checkBox(int position, int day, boolean status) {

            }

            @Override
            public void link(String link) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListeners() {
        ivToggleView.setOnTouchListener((v2, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) ivToggleView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) ivToggleView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });

        ivBack.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivToggleView.setOnClickListener(this);
        fab.setOnClickListener(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(query)) ivClear.setVisibility(View.GONE);
                else ivClear.setVisibility(View.VISIBLE);
                rview.filter(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void toggleSearch() {
        if (isVisibleSearch) {
            isVisibleSearch = false;
            etSearch.getText().clear();
            glTitle.setVisibility(View.VISIBLE);
            glSearch.setVisibility(View.GONE);
            ivSearch.setImageResource(R.drawable.ic_search);
            closeKeyboard();
        } else {
            isVisibleSearch = true;
            glTitle.setVisibility(View.GONE);
            glSearch.setVisibility(View.VISIBLE);
            ivSearch.setImageResource(R.drawable.ic_close_search);
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager =(InputMethodManager) etSearch.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    private void toggleView() {
        isToggleView = !isToggleView;
        param.setBoolean(Config.param().toggleView(),isToggleView);
        ivToggleView.setImageResource(isToggleView?R.drawable.ic_list:R.drawable.ic_grid);
        initRecyclerView();
    }

    public boolean checkBackSearch() {
        boolean result = isVisibleSearch;
        if (isVisibleSearch) toggleSearch();
        return !result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                if (isVisibleSearch) toggleSearch();
                else getParentFragmentManager().popBackStack();
                break;
            case R.id.imageViewClear:
                etSearch.getText().clear();
                break;
            case R.id.imageViewSearch:
                toggleSearch();
                break;
            case R.id.imageViewToggleView:
                toggleView();
                if (isVisibleSearch) toggleSearch();
                break;
            case R.id.fab:
                add();
                break;
        }
    }

    public void add() {
        mediator.show().dialog().commonNotes(true, mediator.form().send().note(null, null), new DialogContract.Action() {
            @Override
            public void delete() {

            }

            @Override
            public void result(ContentValues cvDialog, AlertDialog dialog, MessageContract listenerMessage) {
                boolean form = mediator.form().check(new String[]{"name","text"},cvDialog,listenerMessage);
                if (!form) return;

                mediator.handler().add().commonNotes(cvDialog, new ResultContract() {
                    @Override
                    public void extra(ContentValues cv) {
                        dialog.dismiss();
                        rview.addItem(cv.getAsInteger("position"),new Item().commonNotes(cv.getAsInteger("id"),cvDialog.getAsString("name"),cvDialog.getAsString("text"),datetime.getDatetime()));
                    }

                    @Override
                    public void duplicate() {
                        ((MainActivity) getActivity()).message(getString(R.string.duplicate_note));
                    }
                });
            }
        });
    }
}