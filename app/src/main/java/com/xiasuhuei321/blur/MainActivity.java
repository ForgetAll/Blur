package com.xiasuhuei321.blur;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_rs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BlurImageActivity.class);
                intent.putExtra(BlurImageActivity.TYPE, BlurImageActivity.TYPE_RENDER);
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_dynamic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DynamicBlurActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_java).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BlurImageActivity.class);
                intent.putExtra(BlurImageActivity.TYPE, BlurImageActivity.TYPE_JAVA);
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_jni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BlurImageActivity.class);
                intent.putExtra(BlurImageActivity.TYPE, BlurImageActivity.TYPE_JNI);
                startActivity(intent);
//                view.setTranslationY(400f);
            }
        });
    }

}
