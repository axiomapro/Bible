package ru.ampstudy.bible.component.immutable.box;

import ru.ampstudy.bible.component.mutable.config.AdConfig;
import ru.ampstudy.bible.component.mutable.config.ColumnConfig;
import ru.ampstudy.bible.component.mutable.config.LinkConfig;
import ru.ampstudy.bible.component.mutable.config.LogConfig;
import ru.ampstudy.bible.component.mutable.config.ParamConfig;
import ru.ampstudy.bible.component.mutable.config.RecyclerViewConfig;
import ru.ampstudy.bible.component.mutable.config.ScreenConfig;
import ru.ampstudy.bible.component.mutable.config.TableConfig;
import ru.ampstudy.bible.component.mutable.config.URLConfig;
import ru.ampstudy.bible.component.mutable.config.ViewPagerConfig;

public class Config {

    private static LinkConfig linkConfig;
    private static URLConfig urlConfig;
    private static ScreenConfig screenConfig;
    private static TableConfig tableConfig;
    private static ColumnConfig columnConfig;
    private static ParamConfig paramConfig;
    private static AdConfig adConfig;
    private static ViewPagerConfig viewPagerConfig;
    private static RecyclerViewConfig recyclerViewConfig;
    private static LogConfig logConfig;

    public static LinkConfig link() {
        if (linkConfig == null) linkConfig = new LinkConfig();
        return linkConfig;
    }

    public static URLConfig url() {
        if (urlConfig == null) urlConfig = new URLConfig();
        return urlConfig;
    }

    public static ScreenConfig screen() {
        if (screenConfig == null) screenConfig = new ScreenConfig();
        return screenConfig;
    }

    public static TableConfig table() {
        if (tableConfig == null) tableConfig = new TableConfig();
        return tableConfig;
    }

    public static ColumnConfig column() {
        if (columnConfig == null) columnConfig = new ColumnConfig();
        return columnConfig;
    }

    public static ParamConfig param() {
        if (paramConfig == null) paramConfig = new ParamConfig();
        return paramConfig;
    }

    public static ViewPagerConfig viewPager() {
        if (viewPagerConfig == null) viewPagerConfig = new ViewPagerConfig();
        return viewPagerConfig;
    }

    public static RecyclerViewConfig recyclerView() {
        if (recyclerViewConfig == null) recyclerViewConfig = new RecyclerViewConfig();
        return recyclerViewConfig;
    }

    public static AdConfig ad() {
        if (adConfig == null) adConfig = new AdConfig();
        return adConfig;
    }

    public static LogConfig log() {
        if (logConfig == null) logConfig = new LogConfig();
        return logConfig;
    }

}