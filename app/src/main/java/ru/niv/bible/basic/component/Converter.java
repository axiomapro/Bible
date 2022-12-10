package ru.niv.bible.basic.component;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Converter {

    public String getDateFormat(String date,String resultFormat,int number,boolean datetime,boolean increase) {
        SimpleDateFormat sdf = new SimpleDateFormat(datetime?"yyyy-MM-dd HH:mm:ss":"yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, increase?+number:-number);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat result = new SimpleDateFormat(resultFormat);
        return result.format(new Date(c.getTimeInMillis()));
    }

    public String getNameUppercase(String name,boolean strict) {
        String result = name;
        if (TextUtils.isEmpty(name)) return result;
        if (name.length() == 1) result = name.substring(0,1).toUpperCase();
        else {
            if (strict) result = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
            else result = name.substring(0,1).toUpperCase()+name.substring(1);
        }

        return result;
    }

    public String getTextUppercase(String text) {
        String result = text;
        int len = result.length();
        if (len == 1) result = text.substring(0,1).toUpperCase();
        else if (len > 1) result = text.substring(0,1).toUpperCase()+text.substring(1);
        return result;
    }

    public String getCountry(int language) {
        String result = "US";
        if (language == 2) result = "GB";
        if (language == 3) result = "AU";
        if (language == 4) result = "ES";
        return result;
    }

    public String getDatetime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }

    private boolean checkLeapYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return year % 4 == 0;
    }

    public int getDayTotal(int type) {
        int result = 0;
        if (type == 1) result = 365;
        if (type == 2) result = 180;
        if (type == 3) result = 90;
        return result;
    }

    public float getPercent(int progress, int total) {
        float one = (float) total / 100;
        float percent = progress / one;
        return Float.parseFloat(String.format("%.1f", percent).replace(",","."));
    }

    public String convertPercentToCeil(float percent) {
        String result = String.valueOf(percent);
        String[] cut = result.split("\\.");
        int len = cut.length;
        if (len == 1 || len == 2 && cut[1].equals("0")) result = cut[0];
        return result+"%";
    }

    public int getDayPassed(String start) {
        int result = 0;
        if (start != null) {
            String now = getDate();
            String[] cutNow = now.split("-");
            int nowYear = Integer.parseInt(cutNow[0]);
            int nowMonth = Integer.parseInt(cutNow[1]);
            int nowDay = Integer.parseInt(cutNow[2]);
            String[] cutStart = start.split("-");
            int startYear = Integer.parseInt(cutStart[0]);
            int startMonth = Integer.parseInt(cutStart[1]);
            int startDay = Integer.parseInt(cutStart[2]);
            int resultYear = nowYear - startYear;
            int resultMonth = nowMonth - startMonth;
            int resultDay = nowDay - startDay;

            int[] months = {31,checkLeapYear()?29:28,31,30,31,30,31,31,30,31,30,31};
            if (resultMonth < 0 && resultYear > 0) {
                resultYear--;
                resultMonth = 12 + resultMonth;
            }
            if (resultDay < 0 && resultMonth > 0) {
                resultMonth--;
                resultDay = months[nowMonth - 1] + resultDay;
            }
            result = (365 * resultYear) + (resultMonth * 30) + resultDay;
        }

        return result;
    }

    public boolean isFinishMonth(int year,int month,int day) {
        boolean leapYear = year/4 == 0;
        int[] months = {31,leapYear?29:28,31,30,31,30,31,31,30,31,30,31};
        int reachMonth= months[month - 1];
        return reachMonth == day;
    }
}
