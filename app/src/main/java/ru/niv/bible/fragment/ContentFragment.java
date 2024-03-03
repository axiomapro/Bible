package ru.niv.bible.fragment;

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

import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.view.Rview;

public class ContentFragment extends Fragment {

    private Mediator mediator;
    private Rview rview;
    private RecyclerView recyclerView;
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
        initClasses();
        initRecyclerView();
        ivBack.setOnClickListener(v1 -> {
            if (isTransition) checkBack();
            else getParentFragmentManager().popBackStack();
        });
        return v;
    }

    private void initViews(View v) {
        ivBack = v.findViewById(R.id.imageViewBack);
        tvChapter = v.findViewById(R.id.textViewChapter);
        tvCurrent = v.findViewById(R.id.textViewCurrent);
        recyclerView = v.findViewById(R.id.recyclerView);
        tvCurrent.setText(name);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
    }

    private void initRecyclerView() {
        rview.setRecyclerView(recyclerView);
        rview.initialize(Config.recyclerView().content(), mediator.get().list().content(id, 0), new GridLayoutManager(getContext(), 6), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                if (isTransition) {
                    int item = rview.getItem(position).getNumber();
                    for (int i = 0; i < getParentFragmentManager().getBackStackEntryCount(); i++) {
                        getParentFragmentManager().popBackStack();
                    }
                    mediator.transition(getParentFragmentManager(),MainFragment.newInstance(id,page,item),Config.screen().main(), Static.DOWN_ANIMATION,true,false);
                } else {
                    isTransition = true;
                    page = rview.getItem(position).getNumber();
                    tvChapter.setText(getString(R.string.content_verse));
                    tvCurrent.setText(name+" "+page);
                    rview.redraw(mediator.get().list().content(id,page));
                }
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, boolean isChecked) {

            }
        });
    }

    public boolean checkBack() {
        boolean result = isTransition;
        if (isTransition) {
            isTransition = false;
            tvChapter.setText(getString(R.string.content_chapter));
            tvCurrent.setText(name);
            rview.redraw(mediator.get().list().content(id,0));
        }
        return !result;
    }
}