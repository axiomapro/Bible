package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.niv.bible.R;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.mvp.contract.ReadingPlanChildContract;
import ru.niv.bible.mvp.presenter.ReadingPlanChildPresenter;

public class ReadingPlanChildFragment extends Fragment implements ReadingPlanChildContract.View {

    private ReadingPlanChildPresenter presenter;
    private RecyclerViewAdapter adapter;
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
        Converter converter = new Converter();
        tvTitle.setText(converter.getDateFormat(start,"d MMMM, EEEE",day - 1,false,true)+" / Day "+day);
        presenter = new ReadingPlanChildPresenter(getContext());
        initRecyclerView(v);
        return v;
    }

    private void initRecyclerView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(Static.readingPlanChild,presenter.getList(plan,type,day));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                presenter.updateView(adapter.getItem(position).getId());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(adapter.getItem(position).getChapter(),adapter.getItem(position).getPage(),1),Static.main).addToBackStack(Static.main).commit();
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position, boolean isChecked) {

            }
        });
    }

    public void setActive(boolean status) {
        presenter.updateItemsByDay(plan,type,day,status);
        for (int i = 0; i < adapter.getItemCount(); i++) {
            adapter.getItem(i).setActive(status);
        }
        adapter.notifyDataSetChanged();
    }

}
