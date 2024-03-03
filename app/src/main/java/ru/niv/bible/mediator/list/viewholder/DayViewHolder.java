package ru.niv.bible.mediator.list.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import ru.niv.bible.R;

public class DayViewHolder extends ChildViewHolder {

    public final CheckBox checkBox;
    public final TextView tvLinks, tvDate;
    public final View vDivider;

    public DayViewHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.itemCheckBox);
        tvLinks = itemView.findViewById(R.id.itemLinks);
        tvDate = itemView.findViewById(R.id.itemDate);
        vDivider = itemView.findViewById(R.id.viewDivider);
    }

}