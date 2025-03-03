package ru.ampstudy.bible.fragment.child;

import static android.content.Context.POWER_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.ampstudy.bible.MainActivity;
import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Content;
import ru.ampstudy.bible.component.immutable.box.Convert;
import ru.ampstudy.bible.fragment.SettingsFragment;
import ru.ampstudy.bible.mediator.contract.FragmentContract;
import ru.ampstudy.bible.mediator.contract.RecyclerViewContract;
import ru.ampstudy.bible.mediator.contract.ResultContract;
import ru.ampstudy.bible.mediator.core.Mediator;
import ru.ampstudy.bible.mediator.list.item.Item;
import ru.ampstudy.bible.mediator.view.Rview;
import ru.ampstudy.bible.mediator.view.Sheet;

public class MainChildFragment extends Fragment {

    private FragmentContract.MainChild listener;
    private Mediator mediator;
    private Rview rview;
    private Sheet sheet;
    private Convert convert;
    private Content content;
    private PowerManager.WakeLock wakeLock;
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

        mediator.get().data().getChapterAndPageMain(position, (id, page, chapter) -> {
            chapterId = id;
            chapterPage = page;
            chapterName = chapter;
        });

        initRecyclerView();
        readButton(mediator.get().data().getStateReadButtonMainChild(position));
        initBottomSheet(v);
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
        mediator = new Mediator(getContext());
        rview = mediator.view().rview();
        sheet = mediator.show().sheet();
        convert = new Convert();
        content = new Content(getContext());

        listAudio = new ArrayList<>();
        PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
    }

    private void initRecyclerView() {
        rview.setRecyclerView(recyclerView);
        rview.initialize(Config.recyclerView().main(), mediator.get().list().mainChild(chapterId, chapterPage), new LinearLayoutManager(getContext()), new RecyclerViewContract.Click() {
            @Override
            public void click(int position) {
                boolean click = rview.getItem(position).isClick();
                if (click) totalClick--;
                else totalClick++;

                if (totalClick == 0) {
                    isBottomSheet = false;
                    isEdit = false;
                    nestedScrollView.setVerticalFadingEdgeEnabled(true);
                    visibleBottomSheet(false);
                    setPaddingNestedScrollView(0);
                }
                if (totalClick == 1) {
                    if (!isBottomSheet) {
                        isBottomSheet = true;
                        listPrevious = getCloneList(rview.getList());
                        visibleBottomSheet(true);
                        nestedScrollView.setVerticalFadingEdgeEnabled(false);
                        setPaddingNestedScrollView(getHeightBottomSheet());
                    }
                }

                rview.getItem(position).setClick(!click);
                rview.updateItem(position);
                updateNote();

                if (isPlaying) stop();
            }

            @Override
            public void longClick(int position) {

            }

            @Override
            public void checkBox(int position, int day, boolean status) {

            }

            @Override
            public void link(String link) {

            }
        });
    }

    private void readButton(boolean status) {
        llRead.setSelected(status);
        ivRead.setVisibility(status?View.GONE:View.VISIBLE);
        tvRead.setTextColor(status? Color.parseColor("#756A8E"):Color.WHITE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClickListeners() {
        llRead.setOnTouchListener((v2, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) llRead.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_up));
            else if (event.getAction() == MotionEvent.ACTION_DOWN) llRead.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.click_down));
            return false;
        });

        llRead.setOnClickListener(v1 -> readButton(mediator.handler().toggle().toggleReadButtonPositionMainChild(position)));
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
            listener.getSpeech().speak(rview.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(), TextToSpeech.QUEUE_FLUSH);
        } else {
            audioPosition++;
            if (audioPosition < rview.getTotal()) {
                if (rview.getItem(audioPosition).isHead()) {
                    audioPosition++; // пропускаем
                    if (audioPosition >= rview.getTotal()) { // последним был заголовок
                        stop();
                        getActivity().runOnUiThread(() -> ((MainActivity) getActivity()).getMainFragment().nextPosition());
                        return;
                    }
                }

                getActivity().runOnUiThread(() -> toPosition(audioPosition));
                listener.getSpeech().speak(rview.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(),TextToSpeech.QUEUE_FLUSH);
            } else {
                stop();
                getActivity().runOnUiThread(() -> ((MainActivity) getActivity()).getMainFragment().nextPosition());
            }
        }
    }

    private void playAudio() {
        for (int i = 0; i < rview.getTotal(); i++) {
            if (rview.getItem(i).isClick()) {
                listAudio.add(i);
            }
        }
        isStop = false;
        isPlaying = true;
        audioPosition = listAudio.get(0);
        toPosition(audioPosition);
        listener.getSpeech().speak(rview.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(), TextToSpeech.QUEUE_FLUSH);
        wakeLock.acquire();
    }

    // From main
    public void startAudio() {
        isStop = false;
        isPlaying = true;
        audioPosition = 0;
        if (rview.getItem(audioPosition).isHead()) audioPosition++;
        toPosition(audioPosition);
        showAudio();
        wakeLock.acquire();
        listPrevious = getCloneList(rview.getList());
        setPaddingNestedScrollView(getHeightBottomSheet());

        new Handler(Looper.getMainLooper()).postDelayed(() -> listener.getSpeech().speak(rview.getItem(audioPosition).getText().replaceAll("^\\d+","").trim(),TextToSpeech.QUEUE_FLUSH),500);
    }

    public void stop() {
        if (isPlaying) {
            audioPosition = 0;
            isPlaying = false;
            isStop = true;
            listAudio.clear();
            listener.getSpeech().stop();
            if (wakeLock.isHeld()) wakeLock.release();
            getActivity().runOnUiThread(this::hideBottomSheet);
        }
    }

    /*
        List
    */

    private void updateNote() {
        String note = null;
        for (int i = rview.getTotal() - 1; i >= 0; i--) {
            Item item = rview.getItem(i);
            if (item.isClick() && item.getNote() != null && item.getNote().length() > 0) {
                note = rview.getItem(i).getNote();
                break;
            }
        }
        setNote(note);
    }

    private void saveChanges() {
        isBottomSheet = false;
        isEdit = false;
        setPaddingNestedScrollView(0);
        visibleBottomSheet(false);
        if (totalClick > 0) disableClick();

        for (int i = 0; i < rview.getTotal(); i++) {
            String prevFolderName = listPrevious.get(i).getFolderName();
            String folderName = rview.getItem(i).getFolderName();

            if (prevFolderName != null && folderName == null) {
                mediator.handler().toggle().deleteFavoriteMainChild(rview.getItem(i).getId());
            }
            if (prevFolderName == null && folderName != null || prevFolderName != null && folderName != null) {
                int folder = rview.getItem(i).getFolder();
                int textId = rview.getItem(i).getId();
                int favorite = rview.getItem(i).isFavorite()?1:0;
                int underline = rview.getItem(i).isUnderline()?1:0;
                int color = rview.getItem(i).getColor();
                String note = rview.getItem(i).getNote();
                mediator.update().setFavoriteMainChild(folder,textId,note,favorite,underline,color);
            }
        }
        listPrevious = getCloneList(rview.getList());
    }

    private void disableClick() {
        for (int i = 0; i < rview.getTotal(); i++) {
            rview.getItem(i).setClick(false);
        }
        totalClick = 0;
        rview.update();
    }

    private void toPosition(int position) {
        final float y = recyclerView.getChildAt(position).getY();
        nestedScrollView.post(() -> {
            nestedScrollView.fling(0);
            nestedScrollView.smoothScrollTo(0, (int) y);
        });
    }

    public void toPositionWithDelay(int position) {
        if (recyclerView == null) return;
        recyclerView.post(() -> {
            int correctPosition = mediator.get().data().getCorrectPositionMainChild(chapterId,chapterPage,position);
            if (recyclerView == null || recyclerView.getChildAt(correctPosition) == null) return;;
            final float y = recyclerView.getChildAt(correctPosition).getY();
            nestedScrollView.post(() -> {
                nestedScrollView.fling(0);
                nestedScrollView.smoothScrollTo(0, (int) y);
            });
        });
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
        visibleBottomSheet(false);
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
        for (int i = 0; i < rview.getTotal(); i++) {
            if (rview.getItem(i).isClick()) {
                String text = rview.getItem(i).getText()+"\n\n";
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
                visibleEdit(false);
            } else {
                restorePreviousList();
            }
        }
        return !result;
    }

    public void restorePreviousList() {
        stop();
        hideBottomSheet();
        rview.redraw(getCloneList(listPrevious));
    }

    public void initBottomSheet(View v) {
        sheet.setListFolder(mediator.get().list().getListFolderMainChild());
        sheet.initViews(v);
        sheet.setListener(new Sheet.BottomSheet() {
            @Override
            public void onSetItem(String folderName, String type, String note, int folder, int value) {
                if (totalClick == 0) return;

                boolean foundFist = false;
                for (int i = 0; i < rview.getTotal(); i++) {
                    if (rview.getItem(i).isClick()) {
                        if (!foundFist) {
                            foundFist = true;
                            toPosition(i);
                        }

                        if (type.equals("clear")) {
                            rview.getItem(i).setFolderName(null);
                            rview.getItem(i).setFolder(0);
                            rview.getItem(i).setNote(null);
                            rview.getItem(i).setFavorite(false);
                            rview.getItem(i).setUnderline(false);
                            rview.getItem(i).setColor(0);
                        }
                        if (type.equals("favorite")) {
                            rview.getItem(i).setFolderName(folderName);
                            rview.getItem(i).setFolder(folder);
                            rview.getItem(i).setNote(note);
                            rview.getItem(i).setFavorite(value == 1);
                        }
                        if (type.equals("underline")) {
                            rview.getItem(i).setFolderName(folderName);
                            rview.getItem(i).setFolder(folder);
                            rview.getItem(i).setNote(note);
                            rview.getItem(i).setUnderline(value == 1);
                        }
                        if (type.equals("color")) {
                            rview.getItem(i).setFolderName(folderName);
                            rview.getItem(i).setFolder(folder);
                            rview.getItem(i).setNote(note);
                            rview.getItem(i).setColor(value);
                        }
                        if (type.equals("save-note")) {
                            if (!rview.getItem(i).isFavorite() && !rview.getItem(i).isUnderline() && rview.getItem(i).getColor() == 0 && note.length() == 0) {
                                rview.getItem(i).setFolderName(null);
                                rview.getItem(i).setFolder(0);
                            } else {
                                rview.getItem(i).setFolderName(folderName);
                                rview.getItem(i).setFolder(folder);
                            }
                            rview.getItem(i).setNote(note);
                        }
                    }
                }
                rview.update();
            }

            @Override
            public void onAction(String type) {
                if (type.equals("save")) {
                    nestedScrollView.setVerticalFadingEdgeEnabled(true);
                    saveChanges();
                }
                else if (type.equals("cancel")) {
                    nestedScrollView.setVerticalFadingEdgeEnabled(true);
                    restorePreviousList();
                }
                else if (type.equals("stop")) {
                    nestedScrollView.setVerticalFadingEdgeEnabled(true);
                    stop();
                    hideBottomSheet();
                }
                else if (type.equals("settings")) {
                    if (isPlaying) listener.getSpeech().stop();
                    nestedScrollView.setVerticalFadingEdgeEnabled(true);
                    setPaddingNestedScrollView(0);
                    getParentFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment(),Config.screen().settings()).addToBackStack(Config.screen().settings()).commit();
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
                        } else content.share(getString(R.string.share_main_title),copiedText,getString(R.string.share_popup_main));
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((MainActivity) getActivity()).message(getString(R.string.app_could_not_copy_text));
                    }
                    nestedScrollView.setVerticalFadingEdgeEnabled(true);
                    hideBottomSheet();
                }
                else if (type.equals("audio")) {
                    nestedScrollView.setVerticalFadingEdgeEnabled(false);
                    setPaddingNestedScrollView(getHeightBottomSheet());
                    playAudio();
                    disableClick();
                }
                else if (type.equals("edit")) {
                    isEdit = !isEdit;
                    nestedScrollView.setVerticalFadingEdgeEnabled(false);
                    setPaddingNestedScrollView(getHeightBottomSheet());
                    visibleEdit(isEdit);
                }
            }

            @Override
            public void onScroll(int height) {
                if (isBottomSheet) setPaddingNestedScrollView(height);
            }

            @Override
            public void onHidden() {
                restorePreviousList();
            }

            @Override
            public void onSend(String name) {
                String correctName = convert.getNameUppercase(name,true);
                if (TextUtils.isEmpty(correctName)) sheet.message(v.getContext().getString(R.string.write_the_name));
                else if (correctName.equals(getString(R.string.default_folder))) sheet.message(getString(R.string.name_is_not_available));
                else {
                    mediator.handler().add().folder(mediator.form().send().folder(correctName).get(), new ResultContract() {
                        @Override
                        public void duplicate() {
                            sheet.message(getString(R.string.duplicate_folder));
                        }

                        @Override
                        public void extra(ContentValues cv) {
                            sheet.addFolder(cv.getAsInteger("id"),correctName,cv.getAsInteger("position"));
                        }
                    });
                }
            }
        });
    }

    public void showAudio() {
        sheet.visibleBottomSheet(true);
        sheet.showAudio();
    }

    public void visibleBottomSheet(boolean status) {
        sheet.visibleBottomSheet(status);
    }

    public void visibleEdit(boolean status) {
        sheet.visibleEdit(status);
    }

    public void setNote(String note) {
        sheet.setNote(note);
    }

    public int getHeightBottomSheet() {
        return sheet.getHeightBottomSheet();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentContract.MainChild) listener = (FragmentContract.MainChild) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener.getSpeech() != null) listener.getSpeech().stop();
    }
}