package ru.niv.bible.mvp.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.adapter.ExpandableRviewAdapter;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.list.item.Day;
import ru.niv.bible.basic.list.item.Month;
import ru.niv.bible.mvp.contract.ReadingPlanMaterialContract;
import ru.niv.bible.mvp.presenter.ReadingPlanMaterialPresenter;

public class ReadingPlanMaterialFragment extends Fragment implements ReadingPlanMaterialContract.View {

    private ReadingPlanMaterialPresenter presenter;
    private Converter converter;
    private RecyclerView recyclerView;
    private TextView tvTitle, tvPeriod;
    private String startPlan, finishPlan;
    private int id, typePlan;

    public static ReadingPlanMaterialFragment newInstance(int id) {
        ReadingPlanMaterialFragment fragment = new ReadingPlanMaterialFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reading_plan_material,container,false);
        id = getArguments().getInt("id");
        initViews(v);
        initClasses();
        setParams();
        initExpandableRecyclerView();
        return v;
    }

    private void initViews(View v) {
        ImageView ivBack = v.findViewById(R.id.imageViewBack);
        tvTitle = v.findViewById(R.id.textViewTitle);
        tvPeriod = v.findViewById(R.id.textViewPeriod);
        recyclerView = v.findViewById(R.id.recyclerView);
        ivBack.setOnClickListener(v1 -> getParentFragmentManager().popBackStack());
    }

    private void initClasses() {
        presenter = new ReadingPlanMaterialPresenter(this);
        converter = new Converter();
    }

    private void setParams() {
        presenter.getData(id, (name, start, finish, type) -> {
            typePlan = type;
            startPlan = start;
            finishPlan = finish;
            tvTitle.setText(name);
            tvPeriod.setText(converter.getDateFormat(start,"dd/MM/yyyy",0,false,true)+" - "+converter.getDateFormat(finish,"dd/MM/yyyy",0,false,true));
        });
    }

    private void initExpandableRecyclerView() {
        String[] namesOfMonths = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        String[] cutStart = startPlan.split("-");
        ArrayList<Month> months = new ArrayList<>();
        int currentYear = Integer.parseInt(cutStart[0]);
        int currentMonth = Integer.parseInt(cutStart[1]);
        int total = converter.getDayTotal(typePlan);
        ArrayList<Day> days = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            String date = converter.getDateFormat(startPlan,"d-MM-yyyy",i,false,true);
            String[] cutDate = date.split("-");
            int parseMonth = Integer.parseInt(cutDate[1]);
            int nowDay = i + 1;

            if (i == total - 1) {
                int day = Integer.parseInt(cutDate[0]);
                days.add(new Day(presenter.getLinks(id,typePlan,nowDay),day+" "+namesOfMonths[currentMonth - 1].substring(0,3),nowDay,false,presenter.isCheckBox(id,typePlan,nowDay)));
                Month month = new Month(namesOfMonths[currentMonth - 1]+", "+currentYear,days);
                months.add(month);
                break;
            }
            else if (currentMonth != parseMonth) {
                // previous month
                Month month = new Month(namesOfMonths[currentMonth - 1]+", "+currentYear,days);
                months.add(month);
                // next month
                days = new ArrayList<>();
                currentMonth = parseMonth;
                currentYear = Integer.parseInt(cutDate[2]);
            }

            int year = Integer.parseInt(cutDate[2]);
            int month = Integer.parseInt(cutDate[1]);
            int day = Integer.parseInt(cutDate[0]);
            boolean isFinishMonth = converter.isFinishMonth(year,month,day);
            days.add(new Day(presenter.getLinks(id,typePlan,nowDay),day+" "+namesOfMonths[currentMonth - 1].substring(0,3),nowDay, !isFinishMonth,presenter.isCheckBox(id,typePlan,nowDay)));
        }

        ExpandableRviewAdapter adapter = new ExpandableRviewAdapter(months);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new ExpandableRviewAdapter.Material() {
            @Override
            public void onLink(String link) {
                String[] cutLink = link.split(":");
                int chapter = Integer.parseInt(cutLink[0]);
                int page = Integer.parseInt(cutLink[1]);
                getParentFragmentManager().beginTransaction().replace(R.id.container,MainFragment.newInstance(chapter,page,1), Static.main).addToBackStack(Static.main).commit();
            }

            @Override
            public void onCheckBox(int position,int day,boolean status) {
                presenter.updateItemsByDay(id,typePlan,day,status);
                adapter.notifyItemChanged(position);
            }
        });
    }

}
