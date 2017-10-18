package com.xiasuhuei321.blur;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xiasuhuei321.gank_kotlin.ImageProcess;

/**
 * Created by xiasuhuei321 on 2017/10/18.
 * author:luo
 * e-mail:xiasuhuei321@163.com
 */

public class DynamicBlurActivity extends AppCompatActivity {

    private ImageView blurImg;
    private int height;
    private View container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_dynamic_blur);
        initView();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getHeight();
    }

    private void initView() {
        blurImg = (ImageView) findViewById(R.id.iv_blur_img);
        blurImg.setImageBitmap(ImageProcess.blur(this,
                BitmapFactory.decodeResource(getResources(), R.drawable.test)));
        container = findViewById(R.id.rl_container);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                container.setTranslationY(height + 100);
            }
        }, 100);
    }

    float y;
    boolean scrollFlag = false;
    float sumY = 0;
    boolean isShow = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = event.getY();
                if (y > height * 0.9) {
                    scrollFlag = true;
                } else if (y < height * 0.1) {
                    if (!isShow) return false;
                    scrollFlag = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                sumY = event.getY() - y;
                if (scrollFlag) {
                    container.setTranslationY(event.getY());
                    if (!isShow) blur(sumY);
                    else reverseBlur(sumY);
                }
                Log.e("DynamicBlurActivity", "滚动sumY值：" + sumY + " scrollFlag：" + scrollFlag);

                break;

            case MotionEvent.ACTION_UP:
                if(scrollFlag) {
                    if (Math.abs(sumY) > height * 0.5) {
                        if (isShow) hide();
                        else show();
                    } else {
                        if (isShow) show();
                        else hide();
                    }
                    sumY = 0;
                }
                scrollFlag = false;
                break;
        }

        return true;
    }

    private void hide() {
        container.setTranslationY(height + 100);
        blur(0);
        isShow = false;
    }

    private void show() {
        container.setTranslationY(0);
        blur(1000);
        isShow = true;
    }

    private void blur(float sumY) {
        float absSum = Math.abs(sumY);
        float alpha = absSum / 1000;
        if (alpha > 1) alpha = 1;
        blurImg.setAlpha(alpha);
    }

    private void reverseBlur(float sumY) {
        float absSum = Math.abs(sumY);
        float alpha = absSum / 1000;
        if (alpha > 1) alpha = 1;
        blurImg.setAlpha(1 - alpha);
    }
}
