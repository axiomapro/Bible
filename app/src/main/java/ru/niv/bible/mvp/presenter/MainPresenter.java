package ru.niv.bible.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;

import ru.niv.bible.R;
import ru.niv.bible.basic.component.Dialog;
import ru.niv.bible.basic.component.Go;
import ru.niv.bible.mvp.contract.MainContract;
import ru.niv.bible.mvp.model.MainModel;
import ru.niv.bible.mvp.view.MainFragment;

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;
    private final MainModel model;
    private final Dialog dialog;
    private final Go go;

    public MainPresenter(MainContract.View view) {
        model = new MainModel(((MainFragment) view).getContext());
        dialog = new Dialog(((MainFragment) view).getContext());
        go = new Go(((MainFragment) view).getContext());
        this.view = view;
    }

    public void shareDialog(Context context) {
        String app = "https://play.google.com/store/apps/details?id="+context.getPackageName();
        dialog.shareApp(new Dialog.Share() {
            @Override
            public void onTwitter() {
                try {
                    String text = URLEncoder.encode(context.getString(R.string.twitter_text)+":\n"+app+"\n" + context.getString(R.string.twitter_hashtag));
                    String url = "http://www.twitter.com/intent/tweet?text="+text;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } catch (Exception e) {
                    view.message(context.getString(R.string.twitter_not_installed));
                }
            }

            @Override
            public void onShare() {
                go.share("",context.getString(R.string.dialog_share_text)+":\n"+app,context.getString(R.string.share_popup_dialog));
            }
        });
    }

    public int getPositionByChapterAndPage(int chapter,int page) {
        return model.getPositionByChapterAndPage(chapter,page);
    }

    public String getChapterName(int chapter) {
        return model.getChapterName(chapter);
    }

}
