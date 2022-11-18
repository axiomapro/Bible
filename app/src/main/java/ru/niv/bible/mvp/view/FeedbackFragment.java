package ru.niv.bible.mvp.view;

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
import ru.niv.bible.basic.component.Checker;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.mvp.contract.FeedbackContract;
import ru.niv.bible.mvp.presenter.FeedbackPresenter;

public class FeedbackFragment extends Fragment implements View.OnClickListener, Go.Message, FeedbackContract.View {

    private FeedbackPresenter presenter;
    private Go go;
    private Checker checker;
    private GridLayout glRate, glShare, glContact;
    private ImageView ivBack;
    private TextView tvPolicy;

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
        presenter = new FeedbackPresenter(this);
        go = new Go(getContext());
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
    public void message(String message) {
        ((MainActivity) getActivity()).message(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.gridLayoutRate:
                go.browser("market://details?id="+getActivity().getPackageName(),getString(R.string.google_play_not_found),this);
                break;
            case R.id.gridLayoutShare:
                presenter.shareDialog(getContext());
                break;
            case R.id.gridLayoutContact:
                if (checker.gmail()) go.gmail(getString(R.string.email_subject),getString(R.string.email_message),getString(R.string.email));
                else go.mail(getString(R.string.email_subject),getString(R.string.email_message), getString(R.string.email),this);
                break;
            case R.id.textViewPolicy:
                go.browser(getString(R.string.policy),getString(R.string.browser_not_found),this);
                break;
        }
    }
}
