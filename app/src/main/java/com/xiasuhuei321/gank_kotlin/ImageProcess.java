package com.xiasuhuei321.gank_kotlin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

/**
 * Created by xiasuhuei321 on 2017/10/17.
 * author:luo
 * e-mail:xiasuhuei321@163.com
 */

public class ImageProcess {
    public static final String TAG = "ImageProcess";

    static {
        System.loadLibrary("image-lib");
    }

    /**
     * 图片缩放比例
     */
    private static final float BITMAP_SCALE = 0.4f;
    /**
     * 最大模糊度(在0.0到25.0之间)
     */
    private static final float BLUR_RADIUS = 25f;

    public static Bitmap blur(Context context, Bitmap image) {
        // 计算图片缩小后的长宽
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        // 将缩小后的图片作为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // 创建一张渲染后的输出图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建 RenderScript 内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的 RenderScript 的工具对象
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        // 设置渲染的模糊程度, 25f是最大模糊度
        blur.setRadius(BLUR_RADIUS);
        // 设置blurScript对象的输入内存
        blur.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blur.forEach(tmpOut);
        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static Bitmap blur(Bitmap bmp, int radius) {
        long current = System.currentTimeMillis();
        Bitmap temp = ratio(bmp, bmp.getWidth() * BITMAP_SCALE, bmp.getHeight() * BITMAP_SCALE);
        Log.e(TAG, "压缩图片花费时间：" + (System.currentTimeMillis() - current) + " ms");
        // 回收原图像
        bmp.recycle();

        int width = temp.getWidth();
        int height = temp.getHeight();

//        int width = (int) (temp.getWidth() * BITMAP_SCALE);
//        int height = (int) (temp.getHeight() * BITMAP_SCALE);

        // 申请用于存放像素的内存
        int[] pixels = new int[width * height];
        // 填充数组
        temp.getPixels(pixels, 0, width, 0, 0, width, height);
        current = System.currentTimeMillis();
        blur(pixels, width, height, radius);
        Log.e(TAG, "模糊图片花费时间：" + (System.currentTimeMillis() - current) + " ms");
//        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        temp.setPixels(pixels, 0, width, 0, 0, width, height);
        return temp;
    }

    public static Bitmap ratio(Bitmap bmp, float pixelW, float pixelH) {
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
//        newOpts.inJustDecodeBounds = true;
//        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        // Get bitmap info, but notice that bitmap is null now
//        Bitmap bitmap = bmp;
//
//        newOpts.inJustDecodeBounds = false;
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
//        // 想要缩放的目标尺寸
//        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
//        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
//        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//        int be = 1;//be=1表示不缩放
//        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//            be = (int) (newOpts.outWidth / ww);
//        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//            be = (int) (newOpts.outHeight / hh);
//        }
//        if (be <= 0) be = 1;
//        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        return ThumbnailUtils.extractThumbnail(bmp, (int) pixelW, (int) pixelH);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
    }

    private static native void blur(int[] img, int width, int height, int radius);
}
