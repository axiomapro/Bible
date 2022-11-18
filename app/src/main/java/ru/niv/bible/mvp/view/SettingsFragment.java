package ru.niv.bible.mvp.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.basic.component.Converter;
import ru.niv.bible.basic.component.Param;
import ru.niv.bible.basic.component.Speech;
import ru.niv.bible.basic.component.Static;

public class SettingsFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Param param;
    private Converter converter;
    private GridLayout glFont, glAudio, glOther;
    private LinearLayout llFont, llAudio, llOther;
    private ImageView ivFontArrow, ivAudioArrow, ivOtherArrow, ivBack;
    private SeekBar seekBarFontSize, seekBarLineSpacing, seekBarReadingSpeed, seekBarSpeechPitch;
    private TextView tvExample, tvFontSize, tvLineSpacing, tvReadingSpeed, tvSpeechPitch;
    private FloatingActionButton fab;
    private Spinner spinnerFont, spinnerLanguage, spinnerSelection;
    private String exampleText;
    private boolean isFont, isAudio, isOther, isPlaying;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        initViews(v);
        initClasses();
        setClickListeners();
        setParams();
        return v;
    }

    private void initViews(View v) {
        tvExample = v.findViewById(R.id.textViewExample);
        fab = v.findViewById(R.id.fab);
        glFont = v.findViewById(R.id.gridLayoutFont);
        glAudio = v.findViewById(R.id.gridLayoutAudio);
        glOther = v.findViewById(R.id.gridLayoutOther);
        llFont = v.findViewById(R.id.linearLayoutFont);
        llAudio = v.findViewById(R.id.linearLayoutAudio);
        llOther = v.findViewById(R.id.linearLayoutOther);
        ivFontArrow = v.findViewById(R.id.imageViewFontArrow);
        ivAudioArrow = v.findViewById(R.id.imageViewAudioArrow);
        ivOtherArrow = v.findViewById(R.id.imageViewOtherArrow);
        seekBarFontSize = v.findViewById(R.id.seekbarFontSize);
        seekBarLineSpacing = v.findViewById(R.id.seekbarLineSpacing);
        seekBarReadingSpeed = v.findViewById(R.id.seekbarReadingSpeed);
        seekBarSpeechPitch = v.findViewById(R.id.seekbarSpeechPitch);
        tvFontSize = v.findViewById(R.id.textViewFontSize);
        tvLineSpacing = v.findViewById(R.id.textViewLineSpacing);
        tvReadingSpeed = v.findViewById(R.id.textViewReadingSpeed);
        tvSpeechPitch = v.findViewById(R.id.textViewSpeechPitch);
        spinnerFont = v.findViewById(R.id.spinnerFont);
        spinnerLanguage = v.findViewById(R.id.spinnerLanguage);
        spinnerSelection = v.findViewById(R.id.spinnerSelection);
        ivBack = v.findViewById(R.id.imageViewBack);
        exampleText = getString(R.string.settings_text);
        tvExample.setText(Html.fromHtml(exampleText.replaceAll("\\n","<br>").replaceAll("\\d+","<font color=\"#61567A\">$0</font>")));
    }

    private void initClasses() {
        param = new Param(getContext());
        converter = new Converter();
    }

    private void setClickListeners() {
        seekBarFontSize.setOnSeekBarChangeListener(this);
        seekBarLineSpacing.setOnSeekBarChangeListener(this);
        seekBarReadingSpeed.setOnSeekBarChangeListener(this);
        seekBarSpeechPitch.setOnSeekBarChangeListener(this);
        glFont.setOnClickListener(this);
        glAudio.setOnClickListener(this);
        glOther.setOnClickListener(this);
        fab.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        ArrayAdapter<?> adapterFont = ArrayAdapter.createFromResource(getContext(), R.array.font, android.R.layout.simple_spinner_item);
        adapterFont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFont.setAdapter(adapterFont);
        spinnerFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                param.setInt(Static.paramFont,position+1);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),getFontPath(position + 1));
                tvExample.setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<?> adapterLanguage = ArrayAdapter.createFromResource(getContext(), R.array.language, android.R.layout.simple_spinner_item);
        adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapterLanguage);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int voiceLanguage = position + 1;
                param.setInt(Static.paramLanguage,voiceLanguage);
                getSpeech().setLanguage(voiceLanguage == 4?"es":"en",converter.getCountry(voiceLanguage));
                audio(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<?> adapterSelection = ArrayAdapter.createFromResource(getContext(), R.array.selection, android.R.layout.simple_spinner_item);
        adapterSelection.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelection.setAdapter(adapterSelection);
        spinnerSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                param.setInt(Static.paramSelection,position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setParams() {
        int font = param.getInt(Static.paramFont);
        int fontSize = param.getInt(Static.paramFontSize);
        int lineSpacing = param.getInt(Static.paramLineSpacing);
        int language = param.getInt(Static.paramLanguage);
        int readingSpeed = param.getInt(Static.paramReadingSpeed);
        int speechPitch = param.getInt(Static.paramSpeechPitch);
        int selection = param.getInt(Static.paramSelection);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),getFontPath(font));

        spinnerFont.setSelection(font - 1,false);
        spinnerLanguage.setSelection(language - 1,false);
        spinnerSelection.setSelection(selection - 1,false);
        tvFontSize.setText(String.valueOf(Static.defaultFontSize + fontSize));
        tvLineSpacing.setText(String.valueOf(3 + lineSpacing));
        tvReadingSpeed.setText(String.valueOf((float) (1 + readingSpeed) / 10));
        tvSpeechPitch.setText(String.valueOf((float) (10 + speechPitch) / 10));
        tvExample.setTextSize(Static.defaultFontSize + fontSize);
        tvExample.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3+lineSpacing,  getResources().getDisplayMetrics()), 1.0f);
        tvExample.setTypeface(typeface);
        seekBarFontSize.setProgress(fontSize);
        seekBarLineSpacing.setProgress(lineSpacing);
        seekBarReadingSpeed.setProgress(readingSpeed);
        seekBarSpeechPitch.setProgress(speechPitch);
    }

    private void toggle(int block) {
        if (block == 1) {
            isFont = !isFont;
            llFont.setVisibility(isFont?View.VISIBLE:View.GONE);
            glFont.setSelected(isFont);
            ivFontArrow.setImageResource(isFont?R.drawable.ic_up_settings :R.drawable.ic_down_settings);
        }
        else if (block == 2) {
            isAudio = !isAudio;
            llAudio.setVisibility(isAudio?View.VISIBLE:View.GONE);
            glAudio.setSelected(isAudio);
            ivAudioArrow.setImageResource(isAudio?R.drawable.ic_up_settings :R.drawable.ic_down_settings);
        }
        else if (block == 3) {
            isOther = !isOther;
            llOther.setVisibility(isOther?View.VISIBLE:View.GONE);
            glOther.setSelected(isOther);
            ivOtherArrow.setImageResource(isOther?R.drawable.ic_up_settings :R.drawable.ic_down_settings);
        }
    }

    public void audio(boolean status) {
        isPlaying = status;
        if (status) {
            getSpeech().speak(exampleText.replaceAll("\\d+",""), TextToSpeech.QUEUE_FLUSH);
            fab.setImageResource(R.drawable.ic_pause);
        } else {
            getSpeech().stop();
            fab.setImageResource(R.drawable.ic_play);
        }
    }

    private String getFontPath(int font) {
        String result = "fonts/trirong.ttf";
        if (font == 2) result = "fonts/sans.ttf";
        if (font == 3) result = "fonts/serif.ttf";
        if (font == 4) result = "fonts/monospace.ttf";
        if (font == 5) result = "fonts/lobster.ttf";
        if (font == 6) result = "fonts/sitka.ttf";
        if (font == 7) result = "fonts/poppins.ttf";
        return result;
    }

    private Speech getSpeech() {
        return ((MainActivity) getActivity()).getSpeech();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gridLayoutFont:
                toggle(1);
                break;
            case R.id.gridLayoutAudio:
                toggle(2);
                break;
            case R.id.gridLayoutOther:
                toggle(3);
                break;
            case R.id.fab:
                audio(!isPlaying);
                break;
            case R.id.imageViewBack:
                getParentFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getTag().toString()) {
            case "fontSize":
                int fontSize = Static.defaultFontSize + progress;
                tvExample.setTextSize(fontSize);
                tvFontSize.setText(String.valueOf(fontSize));
                break;
            case "lineSpacing":
                int lineSpacing = 3 + progress;
                tvExample.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineSpacing,  getResources().getDisplayMetrics()), 1.0f);
                tvLineSpacing.setText(String.valueOf(lineSpacing));
                break;
            case "readingSpeed":
                float readingSpeed = (float) (1 + progress) / 10;
                tvReadingSpeed.setText(String.valueOf(readingSpeed));
                if (isPlaying) audio(false);
                break;
            case "speechPitch":
                float speechPitch = (float) (10 + progress) / 10;
                tvSpeechPitch.setText(String.valueOf(speechPitch));
                if (isPlaying) audio(false);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getTag().toString()) {
            case "fontSize":
                param.setInt(Static.paramFontSize,seekBar.getProgress());
                break;
            case "lineSpacing":
                param.setInt(Static.paramLineSpacing,seekBar.getProgress());
                break;
            case "readingSpeed":
                float readingSpeed = (float) (1 + seekBar.getProgress()) / 10;
                param.setInt(Static.paramReadingSpeed,seekBar.getProgress());
                getSpeech().setSpeed(readingSpeed);
                break;
            case "speechPitch":
                float speechPitch = (float) (10 + seekBar.getProgress()) / 10;
                param.setInt(Static.paramSpeechPitch,seekBar.getProgress());
                getSpeech().setPitch(speechPitch);
                break;
        }
    }
}
