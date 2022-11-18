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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Param;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.ListContract;
import ru.niv.bible.mvp.presenter.ListPresenter;

public class ListFragment extends Fragment implements View.OnClickListener, ListContract.View {

    private ListPresenter presenter;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private View vAllBottom, vOtBottom, vNtBottom;
    private GridLayout glTabs, glSearch;
    private RelativeLayout rlAll, rlOt, rlNt;
    private ImageView ivSearch, ivClear, ivBack;
    private EditText etSearch;
    private int tab = 1;
    private boolean isVisibleSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list,container,false);
        initViews(v);
        setParams();
        setTab();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        rlAll = v.findViewById(R.id.relativeLayoutAll);
        rlOt = v.findViewById(R.id.relativeLayoutOt);
        rlNt = v.findViewById(R.id.relativeLayoutNt);
        recyclerView = v.findViewById(R.id.recyclerView);
        glTabs = v.findViewById(R.id.gridLayoutTabs);
        glSearch = v.findViewById(R.id.gridLayoutSearch);
        etSearch = v.findViewById(R.id.editTextSearch);
        ivBack = v.findViewById(R.id.imageViewBack);
        ivClear = v.findViewById(R.id.imageViewClear);
        ivSearch = v.findViewById(R.id.imageViewSearch);
        vAllBottom = v.findViewById(R.id.viewAllBottom);
        vOtBottom = v.findViewById(R.id.viewOtBottom);
        vNtBottom = v.findViewById(R.id.viewNtBottom);
    }

    private void setParams() {
        presenter = new ListPresenter(this);
        if (adapter == null) {
            Param param = new Param(getContext());
            tab = presenter.getTabByPosition(param.getInt(Static.paramPosition) + 1);
            initRecyclerView();
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
            recyclerView.setAdapter(adapter);
        }
    }

    private void initRecyclerView() {
        adapter = new RecyclerViewAdapter(Static.list,presenter.getList(tab));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
        adapter.setTab(1);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                closeKeyboard();
                getParentFragmentManager().beginTransaction().replace(R.id.container,ContentFragment.newInstance(adapter.getItem(position).getId(),adapter.getItem(position).getName()),Static.content).addToBackStack(Static.content).commit();
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {

            }
        });
    }

    private void setClickListeners() {
        rlAll.setOnClickListener(this);
        rlNt.setOnClickListener(this);
        rlOt.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivClear.setOnClickListener(this);

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

    private void redraw(List<Item> list) {
        adapter.getList().clear();
        adapter.getList().addAll(list);
        adapter.setListFiler(list);
        adapter.notifyDataSetChanged();
    }

    private void tabClick(int tab) {
        this.tab = tab;
        setTab();
        adapter.setTab(tab);
        redraw(presenter.getList(tab));
    }

    private void setTab() {
        if (tab == 1) {
            vAllBottom.setVisibility(View.VISIBLE);
            vOtBottom.setVisibility(View.INVISIBLE);
            vNtBottom.setVisibility(View.INVISIBLE);
        }
        else if (tab == 2) {
            vAllBottom.setVisibility(View.INVISIBLE);
            vOtBottom.setVisibility(View.VISIBLE);
            vNtBottom.setVisibility(View.INVISIBLE);
        }
        else if (tab == 3) {
            vAllBottom.setVisibility(View.INVISIBLE);
            vOtBottom.setVisibility(View.INVISIBLE);
            vNtBottom.setVisibility(View.VISIBLE);
        }
    }

    private void toggleSearch() {
        if (isVisibleSearch) {
            isVisibleSearch = false;
            etSearch.getText().clear();
            glTabs.setVisibility(View.VISIBLE);
            glSearch.setVisibility(View.GONE);
            ivSearch.setImageResource(R.drawable.ic_search);
            closeKeyboard();
        } else {
            isVisibleSearch = true;
            glTabs.setVisibility(View.GONE);
            glSearch.setVisibility(View.VISIBLE);
            ivSearch.setImageResource(R.drawable.ic_close_search);
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager =(InputMethodManager) etSearch.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    public boolean checkBackSearch() {
        boolean result = isVisibleSearch;
        if (isVisibleSearch) toggleSearch();
        return !result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayoutAll:
                tabClick(1);
                break;
            case R.id.relativeLayoutOt:
                tabClick(2);
                break;
            case R.id.relativeLayoutNt:
                tabClick(3);
                break;
            case R.id.imageViewBack:
                closeKeyboard();
                getParentFragmentManager().popBackStack();
                break;
            case R.id.imageViewSearch:
                toggleSearch();
                break;
            case R.id.imageViewClear:
                etSearch.getText().clear();
                break;
        }
    }
}
