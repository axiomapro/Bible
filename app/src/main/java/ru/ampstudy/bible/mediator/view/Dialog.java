package ru.ampstudy.bible.mediator.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;

import ru.ampstudy.bible.R;
import ru.ampstudy.bible.component.immutable.box.Convert;
import ru.ampstudy.bible.component.mutable.contentvalue.SendCV;
import ru.ampstudy.bible.mediator.contract.DialogContract;

public class Dialog {

    private final Context context;
    private final Convert convert;

    public Dialog(Context context) {
        this.context = context;
        convert = new Convert();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void shareApp(DialogContract.Share listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_share,null);
        ImageView ivClose = v.findViewById(R.id.imageViewClose);
        LinearLayout llTwitter = v.findViewById(R.id.linearLayoutTwitter);
        LinearLayout llShare = v.findViewById(R.id.linearLayoutShare);
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        llTwitter.setOnTouchListener((v15, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                llTwitter.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                llTwitter.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        llShare.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                llShare.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                llShare.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        llTwitter.setOnClickListener(v13 -> {
            dialog.dismiss();
            listener.twitter();
        });
        llShare.setOnClickListener(v12 -> {
            dialog.dismiss();
            listener.share();
        });
        ivClose.setOnClickListener(v1 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void rate(DialogContract.Rate listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_rate,null);
        ImageView ivClose = v.findViewById(R.id.imageViewClose);
        TextView tvNoThanks = v.findViewById(R.id.textViewNoThanks);
        TextView tvLater = v.findViewById(R.id.textViewLater);
        TextView tvRateUs = v.findViewById(R.id.textViewRateUs);
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        tvNoThanks.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                tvNoThanks.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tvNoThanks.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        tvLater.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                tvLater.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tvLater.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        tvRateUs.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                tvRateUs.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                tvRateUs.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        ivClose.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                ivClose.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ivClose.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        ivClose.setOnClickListener(v14 -> dialog.dismiss());
        tvNoThanks.setOnClickListener(v13 -> {
            dialog.dismiss();
            listener.rate("never");
        });
        tvLater.setOnClickListener(v12 -> dialog.dismiss());
        tvRateUs.setOnClickListener(v1 -> {
            dialog.dismiss();
            listener.rate("rate");
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void folder(boolean add, SendCV cv, DialogContract.Action listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_folder,null);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        TextView tvMessage = v.findViewById(R.id.textViewMessage);
        ImageView ivClose = v.findViewById(R.id.imageViewClose);
        EditText etName = v.findViewById(R.id.editTextName);
        AppCompatButton buttonLeft = v.findViewById(R.id.buttonLeft);
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        tvTitle.setText(add?context.getString(R.string.create_folder):context.getString(R.string.edit_folder));
        etName.setText(cv.get().getAsString("name"));
        ivClose.setVisibility(add?View.GONE:View.VISIBLE);
        buttonLeft.setText(add?context.getString(R.string.add):context.getString(R.string.save));
        buttonRight.setText(add?context.getString(R.string.cancel):context.getString(R.string.delete));
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonLeft.setOnTouchListener((v15, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonRight.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonLeft.setOnClickListener(v12 -> {
            String name = convert.getNameUppercase(etName.getText().toString().trim(),false);
            listener.result(cv.folder(name).get(), dialog, message -> {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            });
        });
        buttonRight.setOnClickListener(v13 -> {
            dialog.dismiss();
            if (!add) listener.delete();
        });
        ivClose.setOnClickListener(v1 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void commonNotes(boolean add, SendCV cv,DialogContract.Action listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_common_notes,null);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        TextView tvMessage = v.findViewById(R.id.textViewMessage);
        ImageView ivClose = v.findViewById(R.id.imageViewClose);
        EditText etName = v.findViewById(R.id.editTextName);
        EditText etText = v.findViewById(R.id.editText);
        AppCompatButton buttonLeft = v.findViewById(R.id.buttonLeft);
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        tvTitle.setText(add?context.getString(R.string.create_note):context.getString(R.string.edit_note));
        etName.setText(cv.get().getAsString("name"));
        etText.setText(cv.get().getAsString("text"));
        ivClose.setVisibility(add?View.GONE:View.VISIBLE);
        buttonLeft.setText(add?context.getString(R.string.add):context.getString(R.string.save));
        buttonRight.setText(add?context.getString(R.string.cancel):context.getString(R.string.delete));
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonLeft.setOnTouchListener((v15, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonRight.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonLeft.setOnClickListener(v12 -> {
            String name = convert.getNameUppercase(etName.getText().toString().trim(),false);
            String text = convert.getTextUppercase(etText.getText().toString().trim());
            listener.result(cv.note(name,text).get(), dialog, message -> {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            });
        });

        buttonRight.setOnClickListener(v13 -> {
            dialog.dismiss();
            if (!add) listener.delete();
        });

        ivClose.setOnClickListener(v1 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void readingPlan(String list,int total, DialogContract.ReadingPlanContainer listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_reading_plan,null);
        NumberPicker numberPicker = v.findViewById(R.id.numberPicker);
        TextView tvList = v.findViewById(R.id.textViewList);
        AppCompatButton buttonLeft = v.findViewById(R.id.buttonLeft);
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        tvList.setText(list);
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonLeft.setOnTouchListener((v15, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonRight.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(total);

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> listener.number(newVal, tvList::setText));

        buttonLeft.setOnClickListener(v12 -> {
            dialog.dismiss();
            listener.result(numberPicker.getValue());
        });
        buttonRight.setOnClickListener(v13 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void notification(String notification,DialogContract.Notification listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_notification,null);
        TimePicker timePicker = v.findViewById(R.id.timePicker);
        SwitchCompat switchCompat = v.findViewById(R.id.switchCompat);
        AppCompatButton buttonCancel = v.findViewById(R.id.buttonCancel);
        AppCompatButton buttonSave = v.findViewById(R.id.buttonSave);

        Calendar calendar = Calendar.getInstance();
        int hour = notification != null?Integer.parseInt(notification.split(":")[0]):calendar.get(Calendar.HOUR_OF_DAY);
        int minute = notification != null?Integer.parseInt(notification.split(":")[1]):calendar.get(Calendar.MINUTE);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonCancel.setOnTouchListener((v15, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonCancel.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonCancel.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonSave.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonSave.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonSave.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonCancel.setOnClickListener(v1 -> dialog.dismiss());
        buttonSave.setOnClickListener(v12 -> {
            dialog.dismiss();
            listener.result(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute(),switchCompat.isChecked());
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void delete(String title, DialogContract.Confirm listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_delete,null);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        AppCompatButton buttonLeft = v.findViewById(R.id.buttonLeft);
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        tvTitle.setText(title);
        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonLeft.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonLeft.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonRight.setOnTouchListener((v13, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_up));
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buttonRight.startAnimation(AnimationUtils.loadAnimation(context,R.anim.click_down));
            }
            return false;
        });

        buttonLeft.setOnClickListener(v12 -> {
            dialog.dismiss();
            listener.agree();
        });
        buttonRight.setOnClickListener(v1 -> dialog.dismiss());
    }

}