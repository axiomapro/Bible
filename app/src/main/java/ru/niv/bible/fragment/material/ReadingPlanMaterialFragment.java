package ru.niv.bible.fragment.material;

import android.os.Bundle;
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

import java.util.ArrayList;

import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Convert;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.mediator.contract.DataContract;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.core.Mediator;
import ru.niv.bible.mediator.list.adapter.ExpandableRviewAdapter;
import ru.niv.bible.mediator.list.item.Day;
import ru.niv.bible.mediator.list.item.Month;
import ru.niv.bible.fragment.MainFragment;

public class ReadingPlanMaterialFragment extends Fragment {

    private Mediator mediator;
    private Convert convert;
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
        mediator = new Mediator(getContext());
        convert = new Convert();
    }

    private void setParams() {
        mediator.get().data().getDataReadingPlanMaterial(id, (type, name, start, finish) -> {
            typePlan = type;
            startPlan = start;
            finishPlan = finish;
            tvTitle.setText(name);
            tvPeriod.setText(convert.getDateFormat(start,"dd/MM/yyyy",0,false,true)+" - "+convert.getDateFormat(finish,"dd/MM/yyyy",0,false,true));
        });
    }

    private void initExpandableRecyclerView() {
        String[] namesOfMonths = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        String[] cutStart = startPlan.split("-");
        ArrayList<Month> months = new ArrayList<>();
        int currentYear = Integer.parseInt(cutStart[0]);
        int currentMonth = Integer.parseInt(cutStart[1]);
        int total = convert.getDayTotal(typePlan);
        ArrayList<Day> days = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            String date = convert.getDateFormat(startPlan,"d-MM-yyyy",i,false,true);
            String[] cutDate = date.split("-");
            int parseMonth = Integer.parseInt(cutDate[1]);
            int nowDay = i + 1;

            if (i == total - 1) {
                int day = Integer.parseInt(cutDate[0]);
                days.add(new Day(mediator.get().data().getLinksReadingPlanMaterial(id,typePlan,nowDay),day+" "+namesOfMonths[currentMonth - 1].substring(0,3),nowDay,false,mediator.get().data().isCheckBoxReadingPlanMaterial(id,typePlan,nowDay)));
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
            boolean isFinishMonth = convert.isFinishMonth(year,month,day);

            days.add(new Day(mediator.get().data().getLinksReadingPlanMaterial(id,typePlan,nowDay),day+" "+namesOfMonths[currentMonth - 1].substring(0,3),nowDay, !isFinishMonth,mediator.get().data().isCheckBoxReadingPlanMaterial(id,typePlan,nowDay)));
        }

        ExpandableRviewAdapter adapter = new ExpandableRviewAdapter(months);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewContract.ReadingPlanMaterial() {
            @Override
            public void link(String link) {
                String[] cutLink = link.split(":");
                int chapter = Integer.parseInt(cutLink[0]);
                int page = Integer.parseInt(cutLink[1]);

                mediator.transition(getParentFragmentManager(),MainFragment.newInstance(chapter,page,1), Config.screen().main(), Static.DOWN_ANIMATION,true,true);
            }

            @Override
            public void checkBox(int position,int day,boolean status) {
                mediator.update().updateItemsByDayReadingPlanMaterial(id,typePlan,day,status);
                adapter.notifyItemChanged(position);
            }
        });
    }

}