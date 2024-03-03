package ru.niv.bible.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.niv.bible.MainActivity;
import ru.niv.bible.R;
import ru.niv.bible.component.immutable.box.Checker;
import ru.niv.bible.component.immutable.box.Content;
import ru.niv.bible.component.immutable.box.Go;
import ru.niv.bible.mediator.contract.DialogContract;
import ru.niv.bible.mediator.core.Mediator;

public class FeedbackFragment extends Fragment implements View.OnClickListener {

    private Mediator mediator;
    private Go go;
    private Content content;
    private Checker checker;
    private ImageView ivBack;
    private TextView tvPolicy;
    private GridLayout glRate, glShare, glContact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback,container,false);
        initViews(v);
        initClasses();
        setClickListeners();
        return v;
    }

    private void initViews(View v) {
        ivBack = v.findViewById(R.id.imageViewBack);
        glRate = v.findViewById(R.id.gridLayoutRate);
        glShare = v.findViewById(R.id.gridLayoutShare);
        glContact = v.findViewById(R.id.gridLayoutContact);
        tvPolicy = v.findViewById(R.id.textViewPolicy);
    }

    private void initClasses() {
        mediator = new Mediator(getContext());
        go = new Go(getContext());
        content = new Content(getContext());
        checker = new Checker(getContext());
    }

    private void setClickListeners() {
        glRate.setOnClickListener(this);
        glShare.setOnClickListener(this);
        glContact.setOnClickListener(this);
        tvPolicy.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.gridLayoutRate:
                go.browser("market://details?id="+getActivity().getPackageName(),getString(R.string.google_play_not_found),((MainActivity) getActivity()));
                break;
            case R.id.gridLayoutShare:
                String app = "https://play.google.com/store/apps/details?id="+getActivity().getPackageName();
                mediator.show().dialog().shareApp(new DialogContract.Share() {
                    @Override
                    public void twitter() {
                        go.twitter(app,((MainActivity) getActivity()));
                    }

                    @Override
                    public void share() {
                        content.share("",getString(R.string.dialog_share_text)+":\n"+app,getString(R.string.share_popup_dialog));
                    }
                });
                break;
            case R.id.gridLayoutContact:
                if (checker.gmail()) go.gmail(getString(R.string.email_subject),getString(R.string.email_message),getString(R.string.email));
                else go.mail(getString(R.string.email_subject),getString(R.string.email_message), getString(R.string.email),((MainActivity) getActivity()));
                break;
            case R.id.textViewPolicy:
                go.browser(getString(R.string.policy),getString(R.string.browser_not_found),((MainActivity) getActivity()));
                break;
        }
    }
}