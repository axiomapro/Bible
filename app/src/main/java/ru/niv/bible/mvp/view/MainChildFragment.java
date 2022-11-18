package ru.niv.bible.mvp.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.basic.component.Speech;
import ru.niv.bible.basic.component.Static;
import ru.niv.bible.basic.list.adapter.RecyclerViewAdapter;
import ru.niv.bible.basic.list.item.Item;
import ru.niv.bible.mvp.contract.MainChildContract;
import ru.niv.bible.mvp.presenter.MainChildPresenter;

import static android.content.Context.POWER_SERVICE;

public class MainChildFragment extends Fragment implements MainChildContract.View {

    private MainChildPresenter presenter;
    private Go go;
    private PowerManager.WakeLock wakeLock;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private NestedScrollView nestedScrollView;
    private LinearLayout llRead;
    private ImageView ivRead;
    private TextView tvRead;
    private List<Item> listPrevious;
    private List<Integer> listAudio;
    private String chapterName;
    private boolean isBottomSheet, isPlaying, isStop, isEdit;
    private int position, totalClick, audioPosition, chapterId, chapterPage;

    public static MainChildFragment newInstance(int position) {
        MainChildFragment fragment = new MainChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_child,container,false);
        position = getArguments().getInt("position");
        initViews(v);
        initClasses();

        JSONObject jsonObject = ((MainActivity) getActivity()).getJsonInfo(position);
        try {
            chapterId = jsonObject.getInt("id");
            chapterName = jsonObject.getString("name");
            chapterPage = jsonObject.getInt("page");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initRecyclerView();
        readButton(presenter.getStateReadButton(position));
        presenter.initBottomSheet(v);
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        nestedScrollView = v.findViewById(R.id.nestedScrollView);
        recyclerView = v.findViewById(R.id.recyclerView);
        llRead = v.findViewById(R.id.linearLayoutRead);
        ivRead = v.findViewById(R.id.imageViewRead);
        tvRead = v.findViewById(R.id.textViewRead);
    }

    private void initClasses() {
        listAudio = new ArrayList<>();
        presenter = new MainChildPresenter(this);
        PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        go = new Go(getContext());
    }

    private void initRecyclerView() {
        adapter = new RecyclerViewAdapter(Static.main,presenter.getList(chapterId,chapterPage));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new RecyclerViewAdapter.Click() {
            @Override
            public void onClick(int position) {
                boolean click = adapter.getItem(position).isClick();
                if (click) totalClick--;
                else totalClick++;

                if (totalClick == 0) {
                    isBottomSheet = false;
                    isEdit = false;
                    presenter.visibleBottomSheet(false);
                    setPaddingNestedScrollView(0);
                }
                if (totalClick == 1) {
                    if (!isBottomSheet) {
                        isBottomSheet = true;
                        listPrevious = getCloneList(adapter.getList());
                        presenter.visibleBottomSheet(true);
                        setPaddingNestedScrollView(presenter.getHeightBottomSheet());
                    }
                }

                adapter.getItem(position).setClick(!click);
                adapter.notifyItemChanged(position);
                updateNote();

                if (isPlaying) stop();
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onCheckBox(int position,boolean isChecked) {

            }
        });
    }

    private void readButton(boolean status) {
        llRead.setSelected(status);
        ivRead.setVisibility(status?View.GONE:View.VISIBLE);
        tvRead.setTextColor(status? Color.parseColor("#756A8E"):Color.WHITE);
    }

    private void setClickListeners() {
        llRead.setOnClickListener(v1 -> readButton(presenter.toggleReadButton(position)));
    }

    /*
        Audio
    */

    public void onDone() {
        if (isStop) {
            isStop = false;
            return;
        }
        if (listAudio.size() > 0) listAudio.remove(0);

        if (listAudio.size() > 0) {
            audioPosition = listAudio.get(0);
            toPosition(audioPosition);
            getSpeech().speak(adapter.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(), TextToSpeech.QUEUE_FLUSH);
        } else {
            audioPosition++;
            if (audioPosition < adapter.getItemCount()) {
                if (adapter.getItem(audioPosition).isHead()) {
                    audioPosition++; // пропускаем
                    if (audioPosition >= adapter.getItemCount()) { // последним был заголовок
                        stop();
                        getActivity().runOnUiThread(() -> ((MainActivity) getActivity()).getMainFragment().nextPosition());
                        return;
                    }
                }

                getActivity().runOnUiThread(() -> toPosition(audioPosition));
                getSpeech().speak(adapter.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(),TextToSpeech.QUEUE_FLUSH);
            } else {
                stop();
                getActivity().runOnUiThread(() -> ((MainActivity) getActivity()).getMainFragment().nextPosition());
            }
        }
    }

    private void playAudio() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isClick()) {
                listAudio.add(i);
            }
        }
        isStop = false;
        isPlaying = true;
        audioPosition = listAudio.get(0);
        toPosition(audioPosition);
        getSpeech().speak(adapter.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(), TextToSpeech.QUEUE_FLUSH);
        wakeLock.acquire();
    }

    // From main
    public void startAudio() {
        isStop = false;
        isPlaying = true;
        audioPosition = 0;
        if (adapter.getItem(audioPosition).isHead()) audioPosition++;
        toPosition(audioPosition);
        presenter.showAudio();
        wakeLock.acquire();
        listPrevious = getCloneList(adapter.getList());
        setPaddingNestedScrollView(presenter.getHeightBottomSheet());

        new Handler(Looper.getMainLooper()).postDelayed(() -> getSpeech().speak(adapter.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(),TextToSpeech.QUEUE_FLUSH),500);
    }

    public void stop() {
        if (isPlaying) {
            audioPosition = 0;
            isPlaying = false;
            isStop = true;
            listAudio.clear();
            getSpeech().stop();
            if (wakeLock.isHeld()) wakeLock.release();
            getActivity().runOnUiThread(this::hideBottomSheet);
        }
    }

    private Speech getSpeech() {
        return ((MainActivity) getActivity()).getSpeech();
    }

    /*
        List
    */

    private void updateNote() {
        String note = null;
        for (int i = adapter.getItemCount() - 1; i >= 0; i--) {
            Item item = adapter.getItem(i);
            if (item.isClick() && item.getNote() != null && item.getNote().length() > 0) {
                note = adapter.getItem(i).getNote();
                break;
            }
        }
        presenter.setNote(note);
    }

    private void redraw(List<Item> list) {
        adapter.getList().clear();
        adapter.getList().addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void saveChanges() {
        isBottomSheet = false;
        isEdit = false;
        setPaddingNestedScrollView(0);
        presenter.visibleBottomSheet(false);
        if (totalClick > 0) disableClick();

        for (int i = 0; i < adapter.getItemCount(); i++) {
            String prevFolderName = listPrevious.get(i).getFolderName();
            String folderName = adapter.getItem(i).getFolderName();

            if (prevFolderName != null && folderName == null) {
                presenter.deleteFavorite(adapter.getItem(i).getId());
            }
            if (prevFolderName == null && folderName != null || prevFolderName != null && folderName != null) {
                int folder = adapter.getItem(i).getFolder();
                int textId = adapter.getItem(i).getId();
                int favorite = adapter.getItem(i).isFavorite()?1:0;
                int underline = adapter.getItem(i).isUnderline()?1:0;
                int color = adapter.getItem(i).getColor();
                String note = adapter.getItem(i).getNote();
                presenter.setFavorite(folder,textId,note,favorite,underline,color);
            }
        }
        listPrevious = getCloneList(adapter.getList());
    }

    private void disableClick() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            adapter.getItem(i).setClick(false);
        }
        totalClick = 0;
        adapter.notifyDataSetChanged();
    }

    private void toPosition(int position) {
        final float y = recyclerView.getChildAt(position).getY();
        nestedScrollView.post(() -> {
            nestedScrollView.fling(0);
            nestedScrollView.smoothScrollTo(0, (int) y);
        });
    }

    public void toPositionWithDelay(int position) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (presenter == null) return;
            int correctPosition = presenter.getCorrectPosition(chapterId,chapterPage,position);
            if (recyclerView == null || recyclerView.getChildAt(correctPosition) == null) return;;
            final float y = recyclerView.getChildAt(correctPosition).getY();
            nestedScrollView.post(() -> {
                nestedScrollView.fling(0);
                nestedScrollView.smoothScrollTo(0, (int) y);
            });
        },100);
    }

    public int getItemPosition() {
        return ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getChapterPage() {
        return chapterPage;
    }

    /*
        Bottom sheet
    */

    private void setPaddingNestedScrollView(int bottom) {
        nestedScrollView.setPadding(0,0,0,bottom);
    }

    public void hideBottomSheet() {
        isBottomSheet = false;
        isEdit = false;
        totalClick = 0;
        setPaddingNestedScrollView(0);
        presenter.visibleBottomSheet(false);
        disableClick();
    }

    /*
        Get
    */

    private List<Item> getCloneList(List<Item> list) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item s : list) {
            Item item = new Item().main(s.getId(), s.getText(), s.getNote(), s.getFolderName(), s.getFolder(), s.isFavorite(), s.isUnderline(), s.getColor(), s.isHead(), s.isClick());
            result.add(item);
        }
        return result;
    }

    private String getTextClick() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isClick()) {
                String text = adapter.getItem(i).getText()+"\n\n";
                result.append(text.replaceAll("^\\d+","$0."));
            }
        }
        return result.toString();
    }

    public boolean checkBack() {
        boolean result = isBottomSheet || isPlaying || isEdit;
        if (result) {
            if (isEdit) {
                isEdit = false;
                presenter.visibleEdit(false);
            } else {
                restorePreviousList();
            }
        }
        return !result;
    }

    @Override
    public void restorePreviousList() {
        stop();
        hideBottomSheet();
        redraw(getCloneList(listPrevious));
    }

    @Override
    public void onSetItem(String folderName, String type, String note, int folder, int value) {
        if (totalClick == 0) return;

        boolean foundFist = false;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isClick()) {
                if (!foundFist) {
                    foundFist = true;
                    toPosition(i);
                }

                if (type.equals("clear")) {
                    adapter.getItem(i).setFolderName(null);
                    adapter.getItem(i).setFolder(0);
                    adapter.getItem(i).setNote(null);
                    adapter.getItem(i).setFavorite(false);
                    adapter.getItem(i).setUnderline(false);
                    adapter.getItem(i).setColor(0);
                }
                if (type.equals("favorite")) {
                    adapter.getItem(i).setFolderName(folderName);
                    adapter.getItem(i).setFolder(folder);
                    adapter.getItem(i).setNote(note);
                    adapter.getItem(i).setFavorite(value == 1);
                }
                if (type.equals("underline")) {
                    adapter.getItem(i).setFolderName(folderName);
                    adapter.getItem(i).setFolder(folder);
                    adapter.getItem(i).setNote(note);
                    adapter.getItem(i).setUnderline(value == 1);
                }
                if (type.equals("color")) {
                    adapter.getItem(i).setFolderName(folderName);
                    adapter.getItem(i).setFolder(folder);
                    adapter.getItem(i).setNote(note);
                    adapter.getItem(i).setColor(value);
                }
                if (type.equals("save-note")) {
                    if (!adapter.getItem(i).isFavorite() && !adapter.getItem(i).isUnderline() && adapter.getItem(i).getColor() == 0 && note.length() == 0) {
                        adapter.getItem(i).setFolderName(null);
                        adapter.getItem(i).setFolder(0);
                    } else {
                        adapter.getItem(i).setFolderName(folderName);
                        adapter.getItem(i).setFolder(folder);
                    }
                    adapter.getItem(i).setNote(note);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAction(String type) {
        if (type.equals("save")) saveChanges();
        else if (type.equals("cancel")) {
            restorePreviousList();
        }
        else if (type.equals("stop")) {
            stop();
            hideBottomSheet();
        }
        else if (type.equals("settings")) {
            if (isPlaying) getSpeech().stop();
            setPaddingNestedScrollView(0);
            getParentFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment(),Static.settings).addToBackStack(Static.settings).commit();
        }
        else if (type.equals("copy") || type.equals("share")) {
            try {
                String text = chapterName+" "+chapterPage+"\n"+getTextClick();
                String copiedText = text.substring(0,text.lastIndexOf("\n\n"));
                if (type.equals("copy")) {
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getString(R.string.text_copied),copiedText);
                    clipboard.setPrimaryClip(clip);
                    ((MainActivity) getActivity()).message(getString(R.string.text_copied));
                } else go.share(getString(R.string.share_main_title),copiedText,getString(R.string.share_popup_main));
            } catch (Exception e) {
                e.printStackTrace();
                ((MainActivity) getActivity()).message(getString(R.string.app_could_not_copy_text));
            }
            hideBottomSheet();
        }
        else if (type.equals("audio")) {
            setPaddingNestedScrollView(presenter.getHeightBottomSheet());
            playAudio();
            disableClick();
        }
        else if (type.equals("edit")) {
            isEdit = !isEdit;
            setPaddingNestedScrollView(presenter.getHeightBottomSheet());
            presenter.visibleEdit(isEdit);
        }
    }

    @Override
    public void onScroll(int height) {
        if (isBottomSheet) setPaddingNestedScrollView(height);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getSpeech() != null) getSpeech().stop();
    }
}
