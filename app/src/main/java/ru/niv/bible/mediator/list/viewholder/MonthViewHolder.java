package ru.niv.bible.mediator.list.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import ru.niv.bible.R;

public class MonthViewHolder extends GroupViewHolder {

    public final TextView tvName;
    private final ImageView ivToggle;

    public MonthViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.itemName);
        ivToggle = itemView.findViewById(R.id.itemToggle);
    }

    @Override
    public void expand() {
        itemView.setSelected(true);
        ivToggle.setImageResource(R.drawable.ic_up_settings);
    }

    @Override
    public void collapse() {
        itemView.setSelected(false);
        ivToggle.setImageResource(R.drawable.ic_down_settings);
    }

}