package ru.ampstudy.bible.fragment.interactive;

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

import ru.ampstudy.bible.R;
import ru.ampstudy.bible.mediator.core.Mediator;
import ru.ampstudy.bible.mediator.list.item.Item;

public class ReadingPlanFragment extends Fragment {

    private Mediator mediator;
    private NestedScrollView scrollView;
    private ImageView ivBack;
    private LinearLayout ll;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reading_plan,container,false);
        initViews(v);
        initClasses();
        redraw();
        scrollView.addView(ll);
        ivBack.setOnClickListener(v1 -> getParentFragmentManager().popBackStack());
        return v;
    }

    private void initViews(View v) {
        scrollView = v.findViewById(R.id.scrollView);
        ivBack = v.findViewById(R.id.imageViewBack);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setId(View.generateViewId());
    }

    public void redraw() {
        ll.removeAllViews();
        List<Item> list = mediator.get().list().readingPlan();
        for (int i = 0; i < list.size(); i++) {
            getParentFragmentManager().beginTransaction().add(ll.getId(), ReadingPlanContainerFragment.newInstance(list.get(i).getId())).commit();
        }
    }
}