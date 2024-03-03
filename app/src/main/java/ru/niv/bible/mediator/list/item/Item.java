package ru.niv.bible.mediator.list.item;

public class Item {

    private String name, text, note, folderName, date, chapterName, chapters, notification;
    private boolean checkBox, favorite, underline, click, divider, active, head, visible;
    private int id, icon, chapter, page, position, type, total, number, color, folder;

    public Item sidebar(String name, int icon, boolean visible) {
        this.name = name;
        this.icon = icon;
        this.visible = visible;
        return this;
    }

    public Item main(int id, String text, String note, String folderName, int folder, boolean favorite, boolean underline, int color, boolean head, boolean click) {
        this.id = id;
        this.text = text;
        this.note = note;
        this.folderName = folderName;
        this.folder = folder;
        this.favorite = favorite;
        this.underline = underline;
        this.color = color;
        this.head = head;
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

    public Item folder(int id, String name, String text, String folderName, String note, String date, int chapter, int page, int position, boolean favorite, boolean underline, int color) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.folderName = folderName;
        this.note = note;
        this.date = date;
        this.chapter = chapter;
        this.page = page;
        this.position = position;
        this.favorite = favorite;
        this.underline = underline;
        this.color = color;
        return this;
    }

    public Item folderBottomSheet(int id, String name, boolean divider) {
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

    public Item commonNotes(int id, String name, String text, String date) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.date = date;
        return this;
    }

    public Item readingPlan(int id) {
        this.id = id;
        return this;
    }

    public Item readingPlanDialog(String name) {
        this.name = name;
        return this;
    }

    public Item readingPlanChild(int id, String name, int chapter, int page, int position, boolean active) {
        this.id = id;
        this.name = name;
        this.chapter = chapter;
        this.page = page;
        this.position = position;
        this.active = active;
        return this;
    }

    public Item dailyVerse(int id, String name, String text, String chapterName, String chapters, String notification, int chapter, int page, int position) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.chapterName = chapterName;
        this.chapters = chapters;
        this.notification = notification;
        this.chapter = chapter;
        this.page = page;
        this.position = position;
        return this;
    }

    public Item dailyVerseEditor(int id, String name, boolean checkBox) {
        this.id = id;
        this.name = name;
        this.checkBox = checkBox;
        return this;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
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

    public void setNote(String note) {
        this.note = note;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public void setPage(int page) {
        this.page = page;
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

    public String getNote() {
        return note;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getChapters() {
        return chapters;
    }

    public String getNotification() {
        return notification;
    }

    public boolean isHead() {
        return head;
    }

    public boolean isVisible() {
        return visible;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setChapters(String chapters) {
        this.chapters = chapters;
    }

    public void setNotification(String notification) {
        this.notification = notification;
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

    public boolean isActive() {
        return active;
    }
}