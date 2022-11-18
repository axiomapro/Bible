package ru.niv.bible.basic.item;

public class Item {

    private String name, text, folderName, date;
    private int id, icon, chapter, page, position, type, total, number, color, folder;
    private boolean checkBox, favorite, underline, click, divider;

    public Item sidebar(String name, int icon) {
        this.name = name;
        this.icon = icon;
        return this;
    }

    public Item main(int id, String text, String folderName, int folder, boolean favorite, boolean underline, int color, boolean click) {
        this.id = id;
        this.text = text;
        this.folderName = folderName;
        this.folder = folder;
        this.favorite = favorite;
        this.underline = underline;
        this.color = color;
        this.click = click;
        return this;
    }

    public Item favorites(int id, String name, int total) {
        this.id = id;
        this.name = name;
        this.total = total;
        return this;
    }

    public Item list(int id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
        return this;
    }

    public Item content(int number) {
        this.number = number;
        return this;
    }

    public Item folder(int id, String name, String text, String folderName, String date, int chapter, int page, int position, boolean favorite, boolean underline, int color) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.folderName = folderName;
        this.date = date;
        this.chapter = chapter;
        this.page = page;
        this.position = position;
        this.favorite = favorite;
        this.underline = underline;
        this.color = color;
        return this;
    }

    public Item folderBottomSheet(int id, String name,boolean divider) {
        this.id = id;
        this.name = name;
        this.divider = divider;
        return this;
    }

    public Item search(int id, String name, String text, int chapter, int page, int position, boolean checkBox) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.chapter = chapter;
        this.page = page;
        this.position = position;
        this.checkBox = checkBox;
        return this;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

    public int getChapter() {
        return chapter;
    }

    public int getPage() {
        return page;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }

    public int getTotal() {
        return total;
    }

    public int getNumber() {
        return number;
    }

    public int getColor() {
        return color;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getDate() {
        return date;
    }

    public int getFolder() {
        return folder;
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isUnderline() {
        return underline;
    }

    public boolean isClick() {
        return click;
    }

    public boolean isDivider() {
        return divider;
    }
}
