package com.xiasuhuei321.blur;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiasuhuei321.gank_kotlin.ImageProcess;

/**
 * Created by xiasuhuei321 on 2017/10/17.
 * author:luo
 * e-mail:xiasuhuei321@163.com
 */

public class BlurImageActivity extends AppCompatActivity {
    public static final int TYPE_JNI = 0;
    public static final int TYPE_JAVA = 1;
    public static final int TYPE_RENDER = 2;

    public static final String TYPE = "type";
    public final String TAG = getClass().getSimpleName();

    private ImageView image;
    Bitmap bmp;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_image);
        image = (ImageView) findViewById(R.id.image);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlurImageActivity.this.finish();
            }
        });
        // 处理图片
        Intent intent = getIntent();
        int type = intent.getIntExtra(TYPE, 2);
        switch (type) {
            case TYPE_JNI:
                showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        start();
                        Bitmap blur = ImageProcess.blur(bmp, 25);
                        final long lastTime = end();
                        Log.e(TAG, "处理花费时间：" + lastTime + " ms");
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BlurImageActivity.this, "处理花费时间：" + lastTime + " ms", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Message msg = Message.obtain();
                        msg.obj = blur;
                        msg.what = TYPE_JNI;
                        h.sendMessage(msg);
                    }
                }).start();
                break;

            case TYPE_JAVA:

                break;

            case TYPE_RENDER:
                showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        start();
                        Bitmap blur = ImageProcess.blur(BlurImageActivity.this, bmp);
                        final long lastTime = end();
                        Log.e(TAG, "处理花费时间：" + lastTime + " ms");
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BlurImageActivity.this, "处理花费时间：" + lastTime + " ms", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Message msg = Message.obtain();
                        msg.obj = blur;
                        msg.what = TYPE_JNI;
                        h.sendMessage(msg);
                    }
                }).start();
                break;

            default:

                break;
        }
//        image.setImageBitmap();
    }

    long current;

    private void start() {
        current = System.currentTimeMillis();
    }

    private long end() {
        return System.currentTimeMillis() - current;
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(BlurImageActivity.this);
        dialog.setMessage("图片处理中...");
        dialog.setTitle("请稍候");
        dialog.show();
    }

    private void dismissProgressDialog() {
        dialog.dismiss();
    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_JNI:
                    image.setImageBitmap((Bitmap) msg.obj);
                    dismissProgressDialog();
                    break;

                default:

                    break;
            }
        }
    };
}
