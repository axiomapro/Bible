package ru.niv.bible.basic.list.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Day;
import ru.niv.bible.basic.list.viewholder.DayViewHolder;
import ru.niv.bible.basic.list.viewholder.MonthViewHolder;

public class ExpandableRviewAdapter extends ExpandableRecyclerViewAdapter<MonthViewHolder, DayViewHolder> {

    private Material listener;

    public interface Material {
        void onLink(String link);
        void onCheckBox(int position,int day,boolean status);
    }

    public void setListener(Material listener) {
        this.listener = listener;
    }

    public ExpandableRviewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public MonthViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading_material_head,parent,false);
        return new MonthViewHolder(v);
    }

    @Override
    public DayViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading_material,parent,false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(DayViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Day day = (Day) group.getItems().get(childIndex);
        setTextViewHTML(holder.tvLinks,day.getLinks());
        int color = Static.lightTheme?Color.parseColor("#80000000"):Color.parseColor("#808080");
        holder.tvLinks.setAlpha(day.isCheckBox()?0.6f:1.0f);
        holder.tvLinks.setLinkTextColor(color);
        holder.tvLinks.setTextColor(color);
        holder.tvDate.setText(day.getDate());
        holder.vDivider.setVisibility(day.isDivider()?View.VISIBLE:View.GONE);
        // block redraw
        holder.checkBox.setOnCheckedChangeListener(null);
        // checkable
        holder.checkBox.setChecked(day.isCheckBox());
        // listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            day.setCheckBox(isChecked);
            listener.onCheckBox(flatPosition,day.getDay(),isChecked);
        });
    }

    @Override
    public void onBindGroupViewHolder(MonthViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.tvName.setText(group.getTitle());
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                listener.onLink(span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
