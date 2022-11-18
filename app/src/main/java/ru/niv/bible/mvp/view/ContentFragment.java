package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.mvp.contract.ContentContract;
import ru.niv.bible.mvp.presenter.ContentPresenter;

public class ContentFragment extends Fragment implements ContentContract.View, View.OnClickListener {

    private ContentPresenter presenter;
    private RecyclerViewAdapter adapter;
    private TextView tvChapter, tvCurrent;
    private ImageView ivBack;
    private String name;
    private boolean isTransition;
    private int id, page;

    public static ContentFragment newInstance(int id,String name) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        bundle.putString("name",name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content,container,false);
        id = getArguments().getInt("id");
        name = getArguments().getString("name");
        initViews(v);
        presenter = new ContentPresenter(this);
        initRecyclerView(v);
        ivBack.setOnClickListener(this);
        return v;
    }

    private void initViews(View v) {
        ivBack = v.findViewById(R.id.imageViewBack);
        tvChapter = v.findViewById(R.id.textViewChapter);
        tvCurrent = v.findViewById(R.id.textViewCurrent);
        tvCurrent.setText(name);
    }

    private void initRecyclerView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(Static.content,presenter.getList(id,0));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                if (isTransition) {
                    int item = adapter.getItem(position).getNumber();
                    for (int i = 0; i < getParentFragmentManager().getBackStackEntryCount(); i++) {
                        getParentFragmentManager().popBackStack();
                    }
                    getParentFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(id,page,item),Static.main).commit();
                } else {
                    isTransition = true;
                    page = adapter.getItem(position).getNumber();
                    tvChapter.setText(getString(R.string.content_verse));
                    tvCurrent.setText(name+" "+page);
                    redraw(presenter.getList(id,page));
                }
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {

            }
        });
    }

    private void redraw(List<Item> list) {
        adapter.getList().clear();
        adapter.getList().addAll(list);
        adapter.notifyDataSetChanged();
    }

    public boolean checkBack() {
        boolean result = isTransition;
        if (isTransition) {
            isTransition = false;
            tvChapter.setText(getString(R.string.content_chapter));
            tvCurrent.setText(name);
            redraw(presenter.getList(id,0));
        }
        return !result;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageViewBack) {
            if (isTransition) checkBack();
            else getParentFragmentManager().popBackStack();
        }
    }
}
