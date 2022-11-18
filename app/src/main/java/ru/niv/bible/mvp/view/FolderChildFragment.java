package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.mvp.contract.FolderChildContract;
import ru.niv.bible.mvp.presenter.FolderChildPresenter;

public class FolderChildFragment extends Fragment implements FolderChildContract.View {

    private FolderChildPresenter presenter;
    private RecyclerViewAdapter adapter;
    private List<Item> listSave;
    private int type,cat,tab;

    public static FolderChildFragment newInstance(int type,int cat,int tab) {
        FolderChildFragment fragment = new FolderChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putInt("cat",cat);
        bundle.putInt("tab",tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder_child,container,false);
        type = getArguments().getInt("type");
        cat = getArguments().getInt("cat");
        tab = getArguments().getInt("tab") + 1;
        initClasses();
        initRecyclerView(v);
        return v;
    }

    private void initClasses() {
        presenter = new FolderChildPresenter(this);
        listSave = new ArrayList<>(presenter.getList(type,cat,tab));
    }

    private void initRecyclerView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(Static.folder,presenter.getList(type,cat,tab));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                getParentFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(adapter.getItem(position).getChapter(),adapter.getItem(position).getPage(),adapter.getItem(position).getPosition()),Static.main).addToBackStack(Static.main).commit();
            }

            @Override
            public void onLongClick(int position) {
                presenter.deleteDialog(adapter.getItem(position).getId(),position);
            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {

            }
        });
    }

    private void redraw(List<Item> list) {
        adapter.getList().clear();
        adapter.getList().addAll(list);
        adapter.setListFiler(list);
        adapter.notifyDataSetChanged();
    }

    public void search(String query) {
        if (adapter == null) return;
        if (query.length() > 0) adapter.getFilter().filter(query);
        else redraw(listSave);
    }

    @Override
    public void removeItem(int position) {
        adapter.getList().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,adapter.getList().size());
    }
}
