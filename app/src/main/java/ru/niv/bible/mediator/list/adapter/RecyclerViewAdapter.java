package ru.niv.bible.mediator.list.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Config;
import ru.niv.bible.component.immutable.box.Static;
import ru.niv.bible.mediator.contract.RecyclerViewContract;
import ru.niv.bible.mediator.list.item.Item;
import ru.niv.bible.mediator.list.viewholder.ViewHolder;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private RecyclerViewContract.Click listener;
    private RecyclerViewContract.DailyVerse listenerDailyVerse;
    private final String screen;
    private String query;
    private List<Item> list;
    private List<Item> listFilter;
    private List<Integer> listNumber;
    private boolean checkBox;
    private final int ITEM = 0;
    private final int ITEM_HEAD = 1;
    private int itemPosition = 1;
    private int tab;

    public void setTab(int tab) {
        this.tab = tab;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setListenerDailyVerse(RecyclerViewContract.DailyVerse listener) {
        this.listenerDailyVerse = listener;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public void setListener(RecyclerViewContract.Click listener) {
        this.listener = listener;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }

    public void setListFilter(List<Item> list) {
        listFilter = list;
    }

    public RecyclerViewAdapter(String screen, List<Item> list) {
        this.screen = screen;
        this.list = list;
        this.listFilter = new ArrayList<>(list);
        this.listNumber = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int item = 0;
        switch (viewType) {
            case ITEM_HEAD:
                item = R.layout.item_main_head;
                break;
            default:
                if (screen.equals(Config.recyclerView().sidebar())) item = R.layout.item_sidebar;
                if (screen.equals(Config.recyclerView().bottomSheet())) item = R.layout.item_bottom_sheet;
                if (screen.equals(Config.recyclerView().list())) item = R.layout.item_list;
                if (screen.equals(Config.recyclerView().content())) item = R.layout.item_content;
                if (screen.equals(Config.recyclerView().favoritesFolder())) item = R.layout.item_favorites_folder;
                if (screen.equals(Config.recyclerView().favoritesBook())) item = R.layout.item_favorites_book;
                if (screen.equals(Config.recyclerView().folder())) item = R.layout.item_folder;
                if (screen.equals(Config.recyclerView().main())) item = R.layout.item_main;
                if (screen.equals(Config.recyclerView().search())) item = R.layout.item_search;
                if (screen.equals(Config.recyclerView().commonNotesList())) item = R.layout.item_common_notes;
                if (screen.equals(Config.recyclerView().commonNotesGrid())) item = R.layout.item_common_notes_grid;
                if (screen.equals(Config.recyclerView().readingPlanChild())) item = R.layout.item_reading_plan_child;
                if (screen.equals(Config.recyclerView().readingPlanMaterial())) item = R.layout.item_reading_plan_material;
                if (screen.equals(Config.recyclerView().readingPlanChildMaterial())) item = R.layout.item_reading_plan_material_child;
                if (screen.equals(Config.recyclerView().dailyVerse())) item = R.layout.item_daily_verse;
                if (screen.equals(Config.recyclerView().dailyVerseEditor())) item = R.layout.item_daily_verse_editor;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = list.get(position);

        switch (getItemViewType(position)) {
            case ITEM_HEAD:
                try {
                    int number = listNumber.get(position);
                } catch (IndexOutOfBoundsException e) {
                    listNumber.add(itemPosition);
                }
                Typeface typefaceHead = Typeface.createFromAsset(holder.itemView.getContext().getAssets(),getFontPath(Static.font));
                holder.tvName.setText(item.getText().replace("\n",""));
                holder.tvName.setTypeface(typefaceHead, Typeface.BOLD);
                holder.tvName.setTextSize(Static.defaultFontSize + Static.fontSize);
                break;
            default:
                if (screen.equals(Config.recyclerView().sidebar())) {
                    holder.ivIcon.setImageResource(item.getIcon());
                    holder.tvName.setText(item.getName());
                    holder.tvDivider.setVisibility(position == list.size() - 1?View.GONE:View.VISIBLE);
                    holder.itemView.setVisibility(item.isVisible()?View.VISIBLE:View.GONE);
                }
                if (screen.equals(Config.recyclerView().bottomSheet())) {
                    holder.tvName.setText(item.getName());
                    holder.tvDivider.setVisibility(item.isDivider()?View.VISIBLE:View.GONE);
                }
                if (screen.equals(Config.recyclerView().list())) {
                    if (tab == 1) {
                        int color = Color.BLACK;
                        if (item.getType() == 2) color = Color.parseColor(Static.lightTheme?"#6C5898":"#887E9F");
                        else if (!Static.lightTheme) color = Color.WHITE;
                        holder.tvName.setTextColor(color);
                    }
                    else holder.tvName.setTextColor(Static.lightTheme?Color.BLACK:Color.WHITE);
                    holder.tvName.setText(item.getName());
                }
                if (screen.equals(Config.recyclerView().content())) {
                    holder.tvNumber.setText(String.valueOf(item.getNumber()));
                }
                if (screen.equals(Config.recyclerView().favoritesFolder())) {
                    holder.tvName.setText(item.getName());
                    holder.tvTotal.setText(String.valueOf(item.getTotal()));
                }
                if (screen.equals(Config.recyclerView().favoritesBook())) {
                    holder.tvName.setText(item.getName());
                    holder.tvTotal.setText(String.valueOf(item.getTotal()));
                    if (position == list.size() - 1) holder.tvDivider.setVisibility(View.GONE);
                }
                if (screen.equals(Config.recyclerView().main())) {
                    Typeface typeface = Typeface.createFromAsset(holder.itemView.getContext().getAssets(),getFontPath(Static.font));
                    holder.tvText.setTextSize(Static.defaultFontSize + Static.fontSize);
                    holder.tvText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3+Static.lineSpacing,  holder.itemView.getContext().getResources().getDisplayMetrics()), 1.0f);
                    holder.tvText.setTypeface(typeface);

                    int number;
                    try {
                        number = listNumber.get(position);
                    } catch (IndexOutOfBoundsException e) {
                        listNumber.add(itemPosition);
                        number = itemPosition;
                    }

                    String text = number+" "+item.getText();
                    String color = getColor(item.getColor());
                    int len = number > 9?2:1;
                    Spannable span = new SpannableString(text);
                    span.setSpan(new ForegroundColorSpan(Color.parseColor("#61567A")), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (Static.selection == 2) {
                        holder.tvClick.setBackgroundColor(Color.parseColor(item.isClick()?"#61567A":"#00FFFFFF"));
                    } else {
                        String defaultColor = "#FFFFFF";
                        String selectedColor = "#F0F0F0";
                        if (!Static.lightTheme) {
                            defaultColor = "#151515";
                            selectedColor = "#070707";
                        }
                        holder.itemView.setBackgroundColor(Color.parseColor(item.isClick()?selectedColor:defaultColor));
                    }

                    holder.ivFavorite.setVisibility(item.isFavorite()?View.VISIBLE:View.GONE);
                    holder.glFolder.setVisibility(item.getFolderName() != null?View.VISIBLE:View.GONE);
                    holder.glNote.setVisibility(item.getNote() != null && item.getNote().length() > 0?View.VISIBLE:View.GONE);

                    if (color != null) span.setSpan(new BackgroundColorSpan(Color.parseColor(color)), len+1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (item.isUnderline()) span.setSpan(new UnderlineSpan(), len+1, text.length(), 0);
                    holder.tvText.setText(span);
                    holder.tvNote.setText(item.getNote() == null?"":item.getNote().replace("\n"," "));
                    holder.tvFolderName.setText(item.getFolderName());
                    itemPosition++;
                }
                if (screen.equals(Config.recyclerView().folder())) {
                    holder.tvName.setText(item.getName());
                    holder.tvDate.setText(convertDate(item.getDate()));
                    holder.tvFolderName.setText(item.getFolderName());
                    holder.tvNote.setText(item.getNote() == null?"":item.getNote().replace("\n"," "));
                    holder.ivFavorite.setVisibility(item.isFavorite()?View.VISIBLE:View.GONE);
                    holder.glFolder.setVisibility(item.getFolderName() == null?View.GONE:View.VISIBLE);
                    holder.glNote.setVisibility(item.getNote() != null && item.getNote().length() > 0?View.VISIBLE:View.GONE);

                    String text = item.getText();
                    String color = getColor(item.getColor());
                    Spannable span = new SpannableString(text);
                    if (color != null) span.setSpan(new BackgroundColorSpan(Color.parseColor(color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (item.isUnderline()) span.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                    holder.tvText.setText(span);
                }
                if (screen.equals(Config.recyclerView().search())) {
                    String text = item.getText();
                    String color = Static.lightTheme?"#6C52A5":"#71668A";

                    holder.tvName.setText(item.getName());
                    holder.tvText.setText(Html.fromHtml(text.replaceAll("(?i)"+query,"<font color="+color+">$0</font>")));
                    // visible
                    if (checkBox) holder.checkBox.setVisibility(View.VISIBLE);
                    else holder.checkBox.setVisibility(View.GONE);
                    // block redraw
                    holder.checkBox.setOnCheckedChangeListener(null);
                    // checkable
                    holder.checkBox.setChecked(item.isCheckBox());
                    // listener
                    holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        item.setCheckBox(isChecked);
                        if (listener != null) listener.checkBox(position,0,holder.checkBox.isChecked());
                    });
                }
                if (screen.equals(Config.recyclerView().commonNotesList())) {
                    holder.tvName.setText(item.getName());
                    holder.tvDate.setText(item.getDate().substring(0,10).replace("-","/"));
                }
                if (screen.equals(Config.recyclerView().commonNotesGrid())) {
                    holder.tvName.setText(item.getName());
                    holder.tvNote.setText(item.getText());
                }
                if (screen.equals(Config.recyclerView().readingPlanChild())) {
                    holder.tvName.setText(item.getName());

                    if (Static.lightTheme) {
                        if (item.isActive()) holder.tvName.setTextColor(Color.GRAY);
                        else holder.tvName.setTextColor(Color.BLACK);
                    } else {
                        if (item.isActive()) holder.tvName.setTextColor(Color.GRAY);
                        else holder.tvName.setTextColor(Color.WHITE);
                    }

                }
                if (screen.equals(Config.recyclerView().readingPlanMaterial())) {
                    holder.tvName.setText(item.getName());
                    holder.ivToggle.setImageResource(item.isExpand()?R.drawable.ic_up_settings:R.drawable.ic_down_settings);
                    holder.recyclerView.setVisibility(item.isExpand()?View.VISIBLE:View.GONE);

                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(Config.recyclerView().readingPlanChildMaterial(),item.getList());
                    holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                    holder.recyclerView.setAdapter(adapter);
                    adapter.setListener(new RecyclerViewContract.Click() {
                        @Override
                        public void click(int position) {

                        }

                        @Override
                        public void longClick(int position) {

                        }

                        @Override
                        public void checkBox(int childPosition, int day, boolean status) {
                            if (listener != null) listener.checkBox(0,day,status);
                        }

                        @Override
                        public void link(String link) {
                            if (listener != null) listener.link(link);
                        }
                    });
                }
                if (screen.equals(Config.recyclerView().readingPlanChildMaterial())) {
                    setTextViewHTML(holder.tvLinks,item.getLinks());
                    int color = Static.lightTheme?Color.parseColor("#80000000"):Color.parseColor("#808080");
                    holder.tvLinks.setAlpha(item.isCheckBox()?0.6f:1.0f);
                    holder.tvLinks.setLinkTextColor(color);
                    holder.tvLinks.setTextColor(color);
                    holder.tvDate.setText(item.getDate());
                    holder.vDivider.setVisibility(item.isDivider()?View.VISIBLE:View.GONE);
                    // block redraw
                    holder.checkBox.setOnCheckedChangeListener(null);
                    // checkable
                    holder.checkBox.setChecked(item.isCheckBox());
                    // listener
                    holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        item.setCheckBox(isChecked);
                        notifyItemChanged(position);
                        listener.checkBox(0,item.getDay(),isChecked);
                    });
                }
                if (screen.equals(Config.recyclerView().dailyVerse())) {
                    holder.tvName.setText(item.getName());
                    holder.tvText.setText(item.getText().replaceAll("\\<[^>]*>",""));
                    holder.tvChapter.setText(item.getChapterName()+" "+item.getPage()+":"+item.getPosition());
                    holder.llShare.setOnClickListener(v -> listenerDailyVerse.share(position));
                    holder.llCopy.setOnClickListener(v -> listenerDailyVerse.copy(position));
                    holder.llEdit.setOnClickListener(v -> listenerDailyVerse.edit(position));
                    holder.llRefresh.setOnClickListener(v -> listenerDailyVerse.refresh(position));
                }
                if (screen.equals(Config.recyclerView().dailyVerseEditor())) {
                    holder.tvName.setText(item.getName());
                    // block redraw
                    holder.checkBox.setOnCheckedChangeListener(null);
                    // checkable
                    holder.checkBox.setChecked(item.isCheckBox());
                    // listener
                    holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        item.setCheckBox(isChecked);
                        if (listener != null) listener.checkBox(position,0,holder.checkBox.isChecked());
                    });
                }

                holder.itemView.setOnClickListener(v -> {
                    if (screen.equals(Config.recyclerView().dailyVerse()) && listenerDailyVerse != null) listenerDailyVerse.click(position);
                    if (listener != null) listener.click(position);
                });

                holder.itemView.setOnLongClickListener(v -> {
                    if (screen.equals(Config.recyclerView().dailyVerse()) && listenerDailyVerse != null) listenerDailyVerse.longClick(position);
                    if (listener != null) listener.longClick(position);
                    return false;
                });
        }
    }

    private String getColor(int color) {
        String result = null;
        if (color == 1) result = "#45A2CF7C";
        if (color == 2) result = "#4581B978";
        if (color == 3) result = "#456EB9E3";
        if (color == 4) result = "#452AC4FA";
        if (color == 5) result = "#457F63BA";
        if (color == 6) result = "#45BA61B6";
        if (color == 7) result = "#45ABC5D2";
        if (color == 8) result = "#45E28357";
        if (color == 9) result = "#45BA5C5C";
        if (color == 10) result = "#459073AB";
        return result;
    }

    private String getFontPath(int font) {
        String result = "fonts/trirong.ttf";
        if (font == 2) result = "fonts/sans.ttf";
        if (font == 3) result = "fonts/serif.ttf";
        if (font == 4) result = "fonts/monospace.ttf";
        if (font == 5) result = "fonts/lobster.ttf";
        if (font == 6) result = "fonts/sitka.ttf";
        if (font == 7) result = "fonts/poppins.ttf";
        return result;
    }

    private String convertDate(String datetime) {
        String result;
        String date = datetime.substring(0,10);
        String[] cut = date.split("-");
        result = cut[2]+"."+cut[1]+"."+cut[0];
        return result;
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                listener.link(span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFilter);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Item item: listFilter) {
                    if (screen.equals(Config.recyclerView().folder())) {
                        if (item.getName().toLowerCase().contains(filterPattern) || item.getText().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    else if (screen.equals(Config.recyclerView().commonNotesList()) || screen.equals(Config.recyclerView().commonNotesGrid())) {
                        if (item.getName().toLowerCase().contains(filterPattern) || item.getText().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    else {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Item getItem(int position) {
        return list.get(position);
    }

    public Item getFilterItem(int position) {
        return listFilter.get(position);
    }

    public List<Item> getList() {
        return list;
    }

    public List<Item> getListFilter() {
        return listFilter;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isHead()?ITEM_HEAD:ITEM;
    }

}