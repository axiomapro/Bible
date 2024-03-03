package ru.niv.bible.mediator.backend;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.SearchView;

import ru.niv.bible.component.immutable.box.Config;

public class ToolbarManager {

    private MenuItem item;
    private SearchView searchView;

    public void parse(Menu menu) {
        item = menu.findItem(0);
        searchView = (SearchView) item.getActionView();
        searchView.setFocusable(true);
        searchView.requestFocus();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    public void collapseSearchView() {
        if (!searchView.isIconified() && item != null) item.collapseActionView();
    }

    public void hide() {
        if (item == null) return;
        item.setVisible(false);
        if (!searchView.isIconified()) item.collapseActionView();
    }

    public void set(String screen) {
        if (screen.equals(Config.screen().main())) {
            hide();
            item.setVisible(true);
        }
    }

    public void setSearchListener(SearchView.OnQueryTextListener listener) {
        if (searchView == null) return;
        searchView.setOnQueryTextListener(listener);
    }

    public void visible(boolean status) {
        if (item == null) return;
        item.setVisible(status);
    }

}