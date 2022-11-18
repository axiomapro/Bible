package ru.niv.bible.mvp.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.ReadingPlanContract;
import ru.niv.bible.mvp.presenter.ReadingPlanPresenter;

public class ReadingPlanFragment extends Fragment implements ReadingPlanContract.View {

    private ReadingPlanPresenter presenter;
    private LinearLayout ll;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reading_plan,container,false);
        initViews(v);
        presenter = new ReadingPlanPresenter(this);

        NestedScrollView scrollView = v.findViewById(R.id.scrollView);
        ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setId(View.generateViewId());
        redraw();
        scrollView.addView(ll);
        return v;
    }

    private void initViews(View v) {
        ImageView ivBack = v.findViewById(R.id.imageViewBack);
        ivBack.setOnClickListener(v1 -> getParentFragmentManager().popBackStack());
    }

    public void redraw() {
        ll.removeAllViews();
        List<Item> list = presenter.getList();
        for (int i = 0; i < list.size(); i++) {
            getParentFragmentManager().beginTransaction().add(ll.getId(), ReadingPlanContainerFragment.newInstance(list.get(i).getId())).commit();
        }
    }

}
