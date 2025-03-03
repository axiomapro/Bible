package ru.ampstudy.bible;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import ru.ampstudy.bible.component.immutable.box.Config;
import ru.ampstudy.bible.component.immutable.box.Param;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView textView = findViewById(R.id.textView);
        textView.setTextColor(getColorFromAttr(R.attr.splashTextGradientStart));
        Shader textShader = new LinearGradient(0, 0, textView.getPaint().measureText(textView.getText().toString()), textView.getTextSize(),
                new int[]{getColorFromAttr(R.attr.splashTextGradientStart), getColorFromAttr(R.attr.splashTextGradientEnd)},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);

        Animation splash = AnimationUtils.loadAnimation(this,R.anim.splash);
        splash.setFillAfter(true);
        textView.startAnimation(splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        },1000);
    }

    private void checkTheme() {
        Param param = new Param(this);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) setTheme(R.style.Dark);
        else setTheme(param.getBoolean(Config.param().theme()) ? R.style.Theme_Bible : R.style.Dark);
    }

    private int getColorFromAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

}