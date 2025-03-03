package ru.ampstudy.bible.mediator.view;

import android.os.Parcelable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.ampstudy.bible.mediator.contract.RecyclerViewContract;
import ru.ampstudy.bible.mediator.list.adapter.RecyclerViewAdapter;
import ru.ampstudy.bible.mediator.list.item.Item;

public class Rview {

    private RecyclerViewContract.Empty listener;
    private RecyclerViewContract.DailyVerse listenerDailyVerse;
    private Parcelable recyclerViewState;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<Item> listSaved;
    private int tab;

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public void setDailyVerseListener(RecyclerViewContract.DailyVerse listenerDailyVerse) {
        this.listenerDailyVerse = listenerDailyVerse;
    }

    public void setListener(RecyclerViewContract.Empty listener) {
        this.listener = listener;
    }

    public void initialize(String screen, List<Item> list,RecyclerView.LayoutManager layoutManager, RecyclerViewContract.Click clickListener) {
        adapter = new RecyclerViewAdapter(screen,list);
        adapter.setListenerDailyVerse(listenerDailyVerse);
        adapter.setTab(tab);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListener(clickListener);
    }

    public void saveListAndState() {
        listSaved = adapter.getList();
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    public void restoreListAndState() {
        adapter.setList(listSaved);
        adapter.setListFilter(adapter.getList());
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    public void redraw(List<Item> newList) {
        adapter.getList().clear();
        adapter.getList().addAll(newList);
        adapter.setListFilter(newList);
        adapter.notifyDataSetChanged();
        visibleEmpty(newList.size() <= 0);
    }

    public void filter(String query) {
        adapter.getFilter().filter(query, count -> visibleEmpty(count == 0));
    }

    private void visibleEmpty(boolean status) {
//        if (listener == null) return;
//        listener.visible(status);
    }

    public void updateTab(int tab) {
        adapter.setTab(tab);
    }

    public void updateQuery(String query) {
        adapter.setQuery(query);
    }

    public void updateCheckBox(boolean status) {
        adapter.setCheckBox(status);
    }

    public void update() {
        adapter.notifyDataSetChanged();
    }

    public void addItem(int position,Item item) {
        if (adapter.getItemCount() == 0) visibleEmpty(false);
        adapter.getList().add(position,item);
        adapter.getListFilter().add(position,item);
        adapter.notifyDataSetChanged();
    }

    public void updateItem(int position) {
        adapter.notifyItemChanged(position);
    }

    public void removeItem(int position) {
        adapter.getList().remove(position);
        adapter.getListFilter().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,adapter.getItemCount());
        if (adapter.getItemCount() == 0) visibleEmpty(true);
    }

    public void clear() {
        adapter.getList().clear();
        adapter.getListFilter().clear();
        adapter.notifyDataSetChanged();
        visibleEmpty(true);
    }

    public int getTotal() {
        return adapter.getItemCount();
    }

    public List<Item> getList() {
        return adapter.getList();
    }

    public Item getItem(int position) {
        return adapter.getList().get(position);
    }

}