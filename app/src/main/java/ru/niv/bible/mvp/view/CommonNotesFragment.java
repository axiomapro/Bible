package ru.niv.bible.mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Param;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.CommonNotesContract;
import ru.niv.bible.mvp.presenter.CommonNotesPresenter;

public class CommonNotesFragment extends Fragment implements View.OnClickListener, CommonNotesContract.View {

    private CommonNotesPresenter presenter;
    private Param param;
    private RecyclerViewAdapter adapter;
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
        presenter = new CommonNotesPresenter(this);
        param = new Param(getContext());
    }

    private void setParams() {
        isToggleView = param.getBoolean(Static.paramToggleView);
        ivToggleView.setImageResource(isToggleView?R.drawable.ic_list:R.drawable.ic_grid);
    }

    private void initRecyclerView() {
        adapter = new RecyclerViewAdapter(isToggleView?Static.commonNotesGrid:Static.commonNotesList,presenter.getList());
        recyclerView.setLayoutManager(isToggleView?new GridLayoutManager(getContext(),2):new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
            }

            @Override
            public void onLongClick(int position) {
                presenter.dialog(false,adapter.getItem(position).getName(),adapter.getItem(position).getNote(),adapter.getItem(position).getId(),position);
            }

            @Override
            public void onCheckBox(int position, boolean isChecked) {

            }
        });
    }

    private void setClickListeners() {
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
                adapter.getFilter().filter(query);
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
        param.setBoolean(Static.paramToggleView,isToggleView);
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
                closeKeyboard();
                getParentFragmentManager().popBackStack();
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
                presenter.dialog(true,null,null,0,0);
                break;
        }
    }

    @Override
    public void addItem(int id,String name,String note,String date, int position) {
        adapter.getList().add(position,new Item().commonNotes(id,name,note,date));
        adapter.getListFilter().add(position,new Item().commonNotes(id,name,note,date));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateItem(String name, String note, int position) {
        adapter.getItem(position).setName(name);
        adapter.getItem(position).setNote(note);
        adapter.getFilterItem(position).setName(name);
        adapter.getFilterItem(position).setNote(note);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void deleteItem(int position) {
        adapter.getList().remove(position);
        adapter.getListFilter().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,adapter.getList().size());
    }
}
