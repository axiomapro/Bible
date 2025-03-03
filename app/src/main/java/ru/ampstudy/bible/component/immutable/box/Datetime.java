package ru.ampstudy.bible.component.immutable.box;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Datetime {

    public int days(String date) {
        if (date.length() > 10) date = date.substring(0,10);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String[] parseCurrentDate = currentDate.split("-");
        int currentYear = Integer.parseInt(parseCurrentDate[0]);
        int currentMonth = Integer.parseInt(parseCurrentDate[1]);
        int currentDay = Integer.parseInt(parseCurrentDate[2]);
        String[] parseDate = date.split("-");
        int year = Integer.parseInt(parseDate[0]);
        int month = Integer.parseInt(parseDate[1]);
        int day = Integer.parseInt(parseDate[2]);

        int[] months = {31,28,31,30,31,30,31,31,30,31,30,31};
        int resultYear = currentYear - year;
        int resultMonth = currentMonth - month;
        int resultDay = currentDay - day;
        if (resultMonth < 0 && resultYear > 0) {
            resultYear--;
            resultMonth = 12 + resultMonth;
        }
        if (resultDay < 0 && resultMonth > 0) {
            resultMonth--;
            resultDay = months[currentMonth - 1] + resultDay;
        }
        return (365 * resultYear) + (resultMonth * 30) + resultDay;
    }

    public boolean time(String time) {
        boolean result = false;
        String[] timeCut = time.split(":");
        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);
        int hour = Integer.parseInt(timeCut[0]);
        int minute = Integer.parseInt(timeCut[1]);
        if (hour > nowHour || hour == nowHour && minute > nowMinute) result = true;
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

    public String restoreDateTime(String datetime) {
        String result = datetime;
        String[] side = datetime.split(" ");
        String date = side[0];
        String[] cutDate = date.split("-");
        String year = cutDate[0];
        String month = addZero(cutDate[1]);
        String day = addZero(cutDate[2]);
        String time = side[1];
        String[] cutTime = time.split(":");
        String hour = addZero(cutTime[0]);
        String minute = addZero(cutTime[1]);
        result = year+"-"+month+"-"+day+" "+hour+":"+minute+":00";
        return result;
    }

    public String getRuDate(String date) {
        String result;
        String[] datePart = date.split("([- :])");
        String year = datePart[0];
        String month = datePart[1];
        String day = cutZero(datePart[2]);
        Calendar calendar = Calendar.getInstance();
        String nowYear = String.valueOf(calendar.get(Calendar.YEAR));
        String[] nameOfMonths = {"янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек"};
        if (nowYear.equals(year)) result = day+" "+nameOfMonths[Integer.parseInt(month) - 1];
        else result = day+" "+nameOfMonths[Integer.parseInt(month)-1]+" "+year;
        return result;
    }

    public String getRuDateFromDatetime(String datetime) {
        String result;
        String[] date = datetime.split("([- :])");
        String year = date[0];
        String month = date[1];
        String day = cutZero(date[2]);
        String hour = cutZero(date[3]);
        String minute = date[4];
        Calendar calendar = Calendar.getInstance();
        String nowYear = String.valueOf(calendar.get(Calendar.YEAR));
        String[] nameOfMonths = {"янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек"};

        if (nowYear.equals(year)) { // нынешний год
            if (hour.equals("")) result = day+" "+nameOfMonths[Integer.parseInt(month) - 1];
            else result = day+" "+nameOfMonths[Integer.parseInt(month)-1]+", "+hour+":"+minute;
        }
        else result = day+" "+nameOfMonths[Integer.parseInt(month)-1]+" "+year;

        return result;
    }

    private String cutZero(String hour) {
        String result;
        String[] cut = hour.split("(?!^)");
        if (cut[0].equals("0")) result = cut[1];
        else result = hour;
        return result;
    }

    private String addZero(String num) {
        String result = num;
        if (num.length() == 1) result = "0"+num;
        return result;
    }

}