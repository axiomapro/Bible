package ru.niv.bible.basic.adapter;

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
import ru.niv.bible.basic.item.Item;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private Click listener;
    private String screen, query;
    private List<Item> list;
    private List<Item> listFilter;
    private boolean checkBox;
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
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int item = 0;
        if (screen.equals(Static.sidebar)) item = R.layout.item_sidebar;
        if (screen.equals(Static.bottomSheet)) item = R.layout.item_bottom_sheet;
        if (screen.equals(Static.list)) item = R.layout.item_list;
        if (screen.equals(Static.content)) item = R.layout.item_content;
        if (screen.equals(Static.favoritesFolder)) item = R.layout.item_favorites_folder;
        if (screen.equals(Static.favoritesBook)) item = R.layout.item_favorites_book;
        if (screen.equals(Static.folder)) item = R.layout.item_folder;
        if (screen.equals(Static.main)) item = R.layout.item_main;
        if (screen.equals(Static.search)) item = R.layout.item_search;
        View v = LayoutInflater.from(parent.getContext()).inflate(item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Item item = list.get(position);
        if (screen.equals(Static.sidebar)) {
            holder.ivIcon.setImageResource(item.getIcon());
            holder.tvName.setText(item.getName());
            if (position == list.size() - 1) holder.tvDivider.setVisibility(View.GONE);
        }
        if (screen.equals(Static.bottomSheet)) {
            holder.tvName.setText(item.getName());
            holder.tvDivider.setVisibility(item.isDivider()?View.VISIBLE:View.GONE);
        }
        if (screen.equals(Static.list)) {
            if (tab == 1) {
                int color = Color.BLACK;
                if (item.getType() == 2) color = Color.parseColor("#E0704D");
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

            String text = item.getText().replace("<br>","\n");
            String color = getColor(item.getColor());
            int len = position >= 9?2:1;
            Spannable span = new SpannableString(text);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#E0714E")), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (Static.selection == 2) {
                holder.tvClick.setBackgroundColor(Color.parseColor(item.isClick()?"#E0714E":"#00FFFFFF"));
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
            if (color != null) span.setSpan(new BackgroundColorSpan(Color.parseColor(color)), len+1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (item.isUnderline()) span.setSpan(new UnderlineSpan(), len+1, text.length(), 0);
            holder.tvText.setText(span);
            holder.tvFolderName.setText(item.getFolderName());
        }
        if (screen.equals(Static.folder)) {
            holder.tvName.setText(item.getName());
            holder.tvDate.setText(convertDate(item.getDate()));
            holder.tvFolderName.setText(item.getFolderName());
            holder.ivFavorite.setVisibility(item.isFavorite()?View.VISIBLE:View.GONE);
            holder.glFolder.setVisibility(item.getFolderName() == null?View.GONE:View.VISIBLE);

            String text = item.getText().replace("<br>","\n");
            String color = getColor(item.getColor());
            Spannable span = new SpannableString(text);
            if (color != null) span.setSpan(new BackgroundColorSpan(Color.parseColor(color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (item.isUnderline()) span.setSpan(new UnderlineSpan(), 0, text.length(), 0);
            holder.tvText.setText(span);
        }
        if (screen.equals(Static.search)) {
            holder.tvName.setText(item.getName());
            holder.tvText.setText(Html.fromHtml(item.getText().replaceAll("(?i)"+query,"<font color=\"#E0714E\">$0</font>")));
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(position);
            return false;
        });
    }

    private String getColor(int color) {
        String result = null;
        if (color == 1) result = "#45A2D07E";
        if (color == 2) result = "#4561F23D";
        if (color == 3) result = "#456CBCEA";
        if (color == 4) result = "#459141D0";
        if (color == 5) result = "#45E535DE";
        if (color == 6) result = "#45DF179B";
        if (color == 7) result = "#45FD8A04";
        if (color == 8) result = "#45F61B1B";
        if (color == 9) result = "#4529C8FA";
        if (color == 10) result = "#458A703C";
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
                    } else {
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

    public List<Item> getList() {
        return list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName, tvText, tvTotal, tvNumber, tvFolderName, tvDate, tvClick, tvDivider;
        private final ImageView ivIcon, ivFavorite;
        private final CheckBox checkBox;
        private final GridLayout glFolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemName);
            tvText = itemView.findViewById(R.id.itemText);
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
        }
    }
}
