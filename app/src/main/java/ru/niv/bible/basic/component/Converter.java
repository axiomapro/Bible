package ru.niv.bible.basic.component;

public class Converter {

    public String getNameFirstCap(String name) {
        String result = name;
        int len = result.length();
        if (len > 1) {
            result = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
        }
        if (len == 1) {
            result = name.substring(0,1).toUpperCase();
        }
        return result;
    }

    public String getCountry(int language) {
        String result = "US";
        if (language == 2) result = "GB";
        if (language == 3) result = "AU";
        return result;
    }

}
