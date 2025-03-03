package ru.ampstudy.bible.mediator.core;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Static;
import ru.ampstudy.bible.model.action.Update;

public class Mediator {

    private final Context context;
    private Update update;
    private Handler handler;
    private Form form;
    private Show show;
    private View view;
    private Get get;

    public Mediator(Context context) {
        this.context = context;
    }

    public View view() {
        if (view == null) view = new View();
        return view;
    }

    public Get get() {
        if (get == null) get = new Get(context);
        return get;
    }

    public Show show() {
        if (show == null) show = new Show(context);
        return show;
    }

    public Form form() {
        if (form == null) form = new Form(context);
        return form;
    }

    public Handler handler() {
        if (handler == null) handler = new Handler(context);
        return handler;
    }

    public void transition(FragmentManager fm, Fragment fragment, String tag, int animation, boolean replace, boolean addToBackStack) {
        FragmentTransaction transaction = fm.beginTransaction();
        int enter = 0;
        int exit = 0;
        int pop_enter = 0;
        int pop_exit = 0;

        if (animation == Static.ALPHA_ANIMATION) {
            enter = R.anim.alpha_enter;
            exit = R.anim.alpha_exit;
            pop_enter = R.anim.alpha_enter;
            pop_exit = R.anim.alpha_exit;
        }
        if (animation == Static.SCALE_ANIMATION) {
            enter = R.anim.scale_enter;
            exit = R.anim.scale_exit;
            pop_enter = R.anim.scale_enter;
            pop_exit = R.anim.scale_exit;
        }
        if (animation == Static.UP_ANIMATION) {
            enter = R.anim.up_enter;
            exit = R.anim.up_exit;
            pop_enter = R.anim.up_pop_enter;
            pop_exit = R.anim.up_pop_exit;
        }
        if (animation == Static.DOWN_ANIMATION) {
            enter = R.anim.down_enter;
            exit = R.anim.down_exit;
            pop_enter = R.anim.down_pop_enter;
            pop_exit = R.anim.down_pop_exit;
        }
        if (animation == Static.RIGHT_ANIMATION) {
            enter = R.anim.right_enter;
            exit = R.anim.right_exit;
            pop_enter = R.anim.right_pop_enter;
            pop_exit = R.anim.right_pop_exit;
        }

        if (animation != 0) {
            transaction.setCustomAnimations(enter,exit,pop_enter,pop_exit);
        }

        if (replace) transaction.replace(R.id.container,fragment,tag);
        else transaction.add(R.id.container,fragment,tag);
        if (addToBackStack) transaction.addToBackStack(tag).commit();
        else transaction.commit();
    }

    public Update update() {
        if (update == null) update = new Update(context);
        return update;
    }

}