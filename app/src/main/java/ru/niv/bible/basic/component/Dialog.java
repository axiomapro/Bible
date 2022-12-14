package ru.niv.bible.basic.component;

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

import ru.niv.bible.R;

public class Dialog {

    private final Context context;

    public Dialog(Context context) {
        this.context = context;
    }

    public interface Folder {
        void onResult(String name,AlertDialog dialog, Message listener);
        void onDelete();
    }

    public interface Note {
        void onResult(String name,String text,AlertDialog dialog, Message listener);
        void onDelete();
    }

    public interface Share {
        void onTwitter();
        void onShare();
    }

    public interface Message {
        void onMessage(String message);
    }

    public interface Rate{
        void onRate(String type);
    }

    public interface ReadingPlan {
        void onNumber(int number,GetList listener);
        void onResult(int number);
    }

    public interface Notification {
        void onResult(String time,boolean status);
    }

    public interface GetList {
        void onList(String listUpdate);
    }

    public interface Delete {
        void onAgree();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void shareApp(Share listener) {
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
            listener.onTwitter();
        });
        llShare.setOnClickListener(v12 -> {
            dialog.dismiss();
            listener.onShare();
        });
        ivClose.setOnClickListener(v1 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void rate(Rate listener) {
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
            listener.onRate("never");
        });
        tvLater.setOnClickListener(v12 -> dialog.dismiss());
        tvRateUs.setOnClickListener(v1 -> {
            dialog.dismiss();
            listener.onRate("rate");
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void folder(boolean statusAdd, String name, Folder listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_folder,null);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        TextView tvMessage = v.findViewById(R.id.textViewMessage);
        ImageView ivClose = v.findViewById(R.id.imageViewClose);
        EditText etName = v.findViewById(R.id.editTextName);
        AppCompatButton buttonLeft = v.findViewById(R.id.buttonLeft);
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        tvTitle.setText(statusAdd?context.getString(R.string.create_folder):context.getString(R.string.edit_folder));
        etName.setText(name);
        ivClose.setVisibility(statusAdd?View.GONE:View.VISIBLE);
        buttonLeft.setText(statusAdd?context.getString(R.string.add):context.getString(R.string.save));
        buttonRight.setText(statusAdd?context.getString(R.string.cancel):context.getString(R.string.delete));
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

        buttonLeft.setOnClickListener(v12 -> listener.onResult(etName.getText().toString().trim(), dialog, message -> {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(message);
        }));
        buttonRight.setOnClickListener(v13 -> {
            dialog.dismiss();
            if (!statusAdd) listener.onDelete();
        });
        ivClose.setOnClickListener(v1 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void commonNotes(boolean statusAdd, String name, String text, Note listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_common_notes,null);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        TextView tvMessage = v.findViewById(R.id.textViewMessage);
        ImageView ivClose = v.findViewById(R.id.imageViewClose);
        EditText etName = v.findViewById(R.id.editTextName);
        EditText etText = v.findViewById(R.id.editText);
        AppCompatButton buttonLeft = v.findViewById(R.id.buttonLeft);
        AppCompatButton buttonRight = v.findViewById(R.id.buttonRight);
        tvTitle.setText(statusAdd?context.getString(R.string.create_note):context.getString(R.string.edit_note));
        etName.setText(name);
        etText.setText(text);
        ivClose.setVisibility(statusAdd?View.GONE:View.VISIBLE);
        buttonLeft.setText(statusAdd?context.getString(R.string.add):context.getString(R.string.save));
        buttonRight.setText(statusAdd?context.getString(R.string.cancel):context.getString(R.string.delete));
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

        buttonLeft.setOnClickListener(v12 -> listener.onResult(etName.getText().toString().trim(),etText.getText().toString().trim(), dialog, message -> {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(message);
        }));
        buttonRight.setOnClickListener(v13 -> {
            dialog.dismiss();
            if (!statusAdd) listener.onDelete();
        });
        ivClose.setOnClickListener(v1 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void readingPlan(String list,int total, ReadingPlan listener) {
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

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> listener.onNumber(newVal, tvList::setText));

        buttonLeft.setOnClickListener(v12 -> {
            dialog.dismiss();
            listener.onResult(numberPicker.getValue());
        });
        buttonRight.setOnClickListener(v13 -> dialog.dismiss());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void notification(String notification,Notification listener) {
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
            listener.onResult(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute(),switchCompat.isChecked());
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void delete(String title,Delete listener) {
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
            listener.onAgree();
        });
        buttonRight.setOnClickListener(v1 -> dialog.dismiss());
    }

}
