package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.niv.bible.R;
import ru.niv.bible.basic.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.item.Item;
import ru.niv.bible.mvp.contract.FavoritesContract;
import ru.niv.bible.mvp.presenter.FavoritesPresenter;

public class FavoritesFragment extends Fragment implements View.OnClickListener, FavoritesContract.View {

    private FavoritesPresenter presenter;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView ivFolder, ivBook, ivBack, ivFavorites;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites,container,false);
        initViews(v);
        presenter = new FavoritesPresenter(this);
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

    private void initRecyclerView(int tab) {
        String screen = tab == 1?Static.favoritesFolder:Static.favoritesBook;
        adapter = new RecyclerViewAdapter(screen,presenter.getList(tab));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                getParentFragmentManager().beginTransaction().replace(R.id.container,FolderFragment.newInstance(adapter.getItem(position).getName(),tab,adapter.getItem(position).getId()),Static.folder).addToBackStack(Static.folder).commit();
            }

            @Override
            public void onLongClick(int position) {
                if (tab == 1) presenter.folderDialog(false,adapter.getItem(position).getName(),adapter.getItem(position).getId(),position);
            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {

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

    private void setClickListeners() {
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
                getParentFragmentManager().beginTransaction().replace(R.id.container,FolderFragment.newInstance(Static.favorites,3,0),Static.folder).addToBackStack(Static.folder).commit();
                break;
            case R.id.fab:
                presenter.folderDialog(true,null,0,0);
                break;
        }
    }

    @Override
    public void addItem(int id,String name, int position) {
        adapter.getList().add(position,new Item().favorites(id,name,0));
        adapter.notifyItemInserted(position);
    }

    @Override
    public void updateItem(String name, int position) {
        adapter.getItem(position).setName(name);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void deleteItem(int position) {
        adapter.getList().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,adapter.getList().size());
    }
}
