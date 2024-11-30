package ru.niv.bible.fragment.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.fragment.FolderFragment;
import ru.niv.bible.fragment.MainFragment;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.view.Rview;

public class FolderChildFragment extends Fragment {

    private Mediator mediator;
    private Rview rview;
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
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
    }

    private void initRecyclerView(View v) {
        rview.setRecyclerView(v.findViewById(R.id.recyclerView));
        rview.initialize(Config.recyclerView().folder(), mediator.get().list().folder(type, cat, tab), new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                ((FolderFragment) getParentFragmentManager().findFragmentByTag(Config.screen().folder())).closeKeyboard();
                mediator.transition(getParentFragmentManager(),MainFragment.newInstance(rview.getItem(position).getChapter(),rview.getItem(position).getPage(),rview.getItem(position).getPosition()),Config.screen().main(), Static.DOWN_ANIMATION,true,true);
            }

            @Override
            public void longClick(int position) {
                mediator.show().dialog().delete(getString(R.string.dialog_delete_item), () -> {
                    mediator.handler().toggle().execute(Config.table().favorite(),Config.column().del(),rview.getItem(position).getId());
                    rview.removeItem(position);
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

    public void search(String query) {
        if (rview == null) return;
        rview.filter(query);
    }

}