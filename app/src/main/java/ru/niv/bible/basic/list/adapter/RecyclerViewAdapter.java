package ru.niv.bible.basic.list.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.item.Item;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private Click listener;
    private String screen, query;
    private List<Item> list;
    private List<Item> listFilter;
    private List<Integer> listNumber;
    private boolean checkBox;
    private final int ITEM = 0;
    private final int ITEM_HEAD = 1;
    private int itemPosition = 1;
    private int tab;

    public interface Click {
        void onClick(int position);
        void onLongClick(int position);
        void onCheckBox(int position,boolean isChecked);
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setListener(Click listener) {
        this.listener = listener;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public void setListFiler(List<Item> list) {
        listFilter = new ArrayList<>(list);
    }

    public RecyclerViewAdapter(String screen, List<Item> list) {
        this.screen = screen;
        this.list = list;
        listFilter = new ArrayList<>(list);
        listNumber = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int item = 0;
        switch (viewType) {
            case ITEM_HEAD:
                item = R.layout.item_main_head;
                break;
            default:
                if (screen.equals(Static.sidebar)) item = R.layout.item_sidebar;
                if (screen.equals(Static.bottomSheet)) item = R.layout.item_bottom_sheet;
                if (screen.equals(Static.list)) item = R.layout.item_list;
                if (screen.equals(Static.content)) item = R.layout.item_content;
                if (screen.equals(Static.favoritesFolder)) item = R.layout.item_favorites_folder;
                if (screen.equals(Static.favoritesBook)) item = R.layout.item_favorites_book;
                if (screen.equals(Static.folder)) item = R.layout.item_folder;
                if (screen.equals(Static.main)) item = R.layout.item_main;
                if (screen.equals(Static.search)) item = R.layout.item_search;
                if (screen.equals(Static.commonNotesList)) item = R.layout.item_common_notes;
                if (screen.equals(Static.commonNotesGrid)) item = R.layout.item_common_notes_grid;
                if (screen.equals(Static.readingPlanChild)) item = R.layout.item_reading_plan_child;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
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
                if (screen.equals(Static.sidebar)) {
                    holder.ivIcon.setImageResource(item.getIcon());
                    holder.tvName.setText(item.getName());
                    holder.tvDivider.setVisibility(position == list.size() - 1?View.GONE:View.VISIBLE);
                    holder.itemView.setVisibility(item.isVisible()?View.VISIBLE:View.GONE);
                }
                if (screen.equals(Static.bottomSheet)) {
                    holder.tvName.setText(item.getName());
                    holder.tvDivider.setVisibility(item.isDivider()?View.VISIBLE:View.GONE);
                }
                if (screen.equals(Static.list)) {
                    if (tab == 1) {
                        int color = Color.BLACK;
                        if (item.getType() == 2) color = Color.parseColor(Static.lightTheme?"#6C5898":"#887E9F");
                        else if (!Static.lightTheme) color = Color.WHITE;
                        holder.tvName.setTextColor(color);
                    }
                    else holder.tvName.setTextColor(Static.lightTheme?Color.BLACK:Color.WHITE);
                    holder.tvName.setText(item.getName());
                }
                if (screen.equals(Static.content)) {
                    holder.tvNumber.setText(String.valueOf(item.getNumber()));
                }
                if (screen.equals(Static.favoritesFolder)) {
                    holder.tvName.setText(item.getName());
                    holder.tvTotal.setText(String.valueOf(item.getTotal()));
                }
                if (screen.equals(Static.favoritesBook)) {
                    holder.tvName.setText(item.getName());
                    holder.tvTotal.setText(String.valueOf(item.getTotal()));
                    if (position == list.size() - 1) holder.tvDivider.setVisibility(View.GONE);
                }
                if (screen.equals(Static.main)) {
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
                if (screen.equals(Static.folder)) {
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
                if (screen.equals(Static.search)) {
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
                        if (listener != null) listener.onCheckBox(position,holder.checkBox.isChecked());
                    });
                }
                if (screen.equals(Static.commonNotesList)) {
                    holder.tvName.setText(item.getName());
                    holder.tvDate.setText(item.getDate().substring(0,10).replace("-","/"));
                }
                if (screen.equals(Static.commonNotesGrid)) {
                    holder.tvName.setText(item.getName());
                    holder.tvNote.setText(item.getNote());
                }
                if (screen.equals(Static.readingPlanChild)) {
                    holder.tvName.setText(item.getName());

                    if (Static.lightTheme) {
                        if (item.isActive()) holder.tvName.setTextColor(Color.GRAY);
                        else holder.tvName.setTextColor(Color.BLACK);
                    } else {
                        if (item.isActive()) holder.tvName.setTextColor(Color.GRAY);
                        else holder.tvName.setTextColor(Color.WHITE);
                    }

                }

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onClick(position);
                });

                holder.itemView.setOnLongClickListener(v -> {
                    if (listener != null) listener.onLongClick(position);
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

    @Override
    public Filter getFilter() {
        return filter;
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
                    if (screen.equals(Static.folder)) {
                        if (item.getName().toLowerCase().contains(filterPattern) || item.getText().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    else if (screen.equals(Static.commonNotesList) || screen.equals(Static.commonNotesGrid)) {
                        if (item.getName().toLowerCase().contains(filterPattern) || item.getNote().toLowerCase().contains(filterPattern)) {
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

    @Override
    public int getItemCount() {
        return list.size();
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
    public int getItemViewType(int position) {
        return list.get(position).isHead()?ITEM_HEAD:ITEM;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName, tvText, tvNote, tvTotal, tvNumber, tvFolderName, tvDate, tvClick, tvDivider;
        private final ImageView ivIcon, ivFavorite;
        private final CheckBox checkBox;
        private final GridLayout glFolder, glNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemName);
            tvText = itemView.findViewById(R.id.itemText);
            tvNote = itemView.findViewById(R.id.itemNoteText);
            tvTotal = itemView.findViewById(R.id.itemTotal);
            tvNumber = itemView.findViewById(R.id.itemNumber);
            tvFolderName = itemView.findViewById(R.id.itemFolderName);
            tvDate = itemView.findViewById(R.id.itemDate);
            tvClick = itemView.findViewById(R.id.itemClick);
            tvDivider = itemView.findViewById(R.id.itemDivider);
            ivIcon = itemView.findViewById(R.id.itemIcon);
            ivFavorite = itemView.findViewById(R.id.itemFavorite);
            checkBox = itemView.findViewById(R.id.itemCheckBox);
            glFolder = itemView.findViewById(R.id.itemFolder);
            glNote = itemView.findViewById(R.id.itemNote);
        }
    }
}
