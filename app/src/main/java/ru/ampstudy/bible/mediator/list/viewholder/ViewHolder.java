package ru.ampstudy.bible.mediator.list.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.ampstudy.bible.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public final RecyclerView recyclerView;
    public final TextView tvName, tvText, tvNote, tvTotal, tvNumber, tvFolderName, tvLinks, tvDate, tvClick, tvDivider, tvChapter;
    public final ImageView ivIcon, ivFavorite, ivToggle;
    public final CheckBox checkBox;
    public final GridLayout glFolder, glNote;
    public final LinearLayout llShare, llCopy, llEdit, llRefresh;
    public final View vDivider;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        recyclerView = itemView.findViewById(R.id.itemRecyclerView);
        tvName = itemView.findViewById(R.id.itemName);
        tvText = itemView.findViewById(R.id.itemText);
        tvChapter = itemView.findViewById(R.id.itemChapter);
        tvNote = itemView.findViewById(R.id.itemNoteText);
        tvTotal = itemView.findViewById(R.id.itemTotal);
        tvNumber = itemView.findViewById(R.id.itemNumber);
        tvFolderName = itemView.findViewById(R.id.itemFolderName);
        tvDate = itemView.findViewById(R.id.itemDate);
        tvLinks = itemView.findViewById(R.id.itemLinks);
        tvClick = itemView.findViewById(R.id.itemClick);
        tvDivider = itemView.findViewById(R.id.itemDivider);
        ivIcon = itemView.findViewById(R.id.itemIcon);
        ivFavorite = itemView.findViewById(R.id.itemFavorite);
        ivToggle = itemView.findViewById(R.id.itemToggle);
        checkBox = itemView.findViewById(R.id.itemCheckBox);
        glFolder = itemView.findViewById(R.id.itemFolder);
        glNote = itemView.findViewById(R.id.itemNote);
        llShare = itemView.findViewById(R.id.itemShare);
        llCopy = itemView.findViewById(R.id.itemCopy);
        llEdit = itemView.findViewById(R.id.itemEdit);
        llRefresh = itemView.findViewById(R.id.itemRefresh);
        vDivider = itemView.findViewById(R.id.viewDivider);
    }

}