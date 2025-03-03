package ru.ampstudy.bible.mediator.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Empty {

    private LinearLayout linearLayout;
    private ImageView imageView;
    private TextView textView;
    private String text;
    private int icon;

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void initialize(View v) {
//        linearLayout = v.findViewById(R.id.linearLayoutEmpty);
//        imageView = v.findViewById(R.id.imageViewEmpty);
//        textView = v.findViewById(R.id.textViewEmpty);
    }

    public void visible(boolean status) {
//        linearLayout.setVisibility(status?View.VISIBLE:View.GONE);
//        imageView.setImageResource(icon);
//        textView.setText(text);
    }
}