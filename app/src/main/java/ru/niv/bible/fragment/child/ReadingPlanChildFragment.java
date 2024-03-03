package ru.niv.bible.fragment.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Convert;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.fragment.MainFragment;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.view.Rview;

public class ReadingPlanChildFragment extends Fragment {

    private Mediator mediator;
    private Rview rview;
    private Convert convert;
    private int plan, type, day;

    public static ReadingPlanChildFragment newInstance(String start,int plan,int type,int position) {
        ReadingPlanChildFragment fragment = new ReadingPlanChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString("start",start);
        bundle.putInt("plan",plan);
        bundle.putInt("type",type);
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reading_plan_child,container,false);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        String start = getArguments().getString("start");
        plan = getArguments().getInt("plan");
        type = getArguments().getInt("type");
        day = getArguments().getInt("position") + 1;
        initClasses();
        initRecyclerView(v);
        tvTitle.setText(convert.getDateFormat(start,"d MMMM, EEEE",day - 1,false,true)+" / Day "+day);
        return v;
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
        convert = new Convert();
    }

    private void initRecyclerView(View v) {
        rview.setRecyclerView(v.findViewById(R.id.recyclerView));
        rview.initialize(Config.recyclerView().readingPlanChild(), mediator.get().list().readingPlanChild(plan, type, day), new GridLayoutManager(getContext(), 2), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                mediator.update().updateViewReadingPlanChild(rview.getItem(position).getId());
                mediator.transition(getActivity().getSupportFragmentManager(),MainFragment.newInstance(rview.getItem(position).getChapter(),rview.getItem(position).getPage(),1),Config.screen().main(), Static.DOWN_ANIMATION,true,true);
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, boolean isChecked) {

            }
        });
    }

    public void setActive(boolean status) {
        mediator.update().updateItemsByDayReadingPlanChild(plan,type,day,status);
        for (int i = 0; i < rview.getTotal(); i++) {
            rview.getItem(i).setActive(status);
        }
        rview.update();
    }
}