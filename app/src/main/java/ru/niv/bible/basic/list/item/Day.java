package ru.niv.bible.basic.list.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Day implements Parcelable {

    private final String links, date;
    private int day;
    private boolean checkBox, divider;

    public Day(String links,String date,int day,boolean divider,boolean checkBox) {
        this.links = links;
        this.date = date;
        this.day = day;
        this.divider = divider;
        this.checkBox = checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public int getDay() {
        return day;
    }

    public String getLinks() {
        return links;
    }

    public String getDate() {
        return date;
    }

    public boolean isDivider() {
        return divider;
    }

    protected Day(Parcel in) {
        links = in.readString();
        date = in.readString();
    }

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(links);
        dest.writeString(date);
    }

}
