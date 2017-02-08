package com.colorful.mqq.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by colorful on 2017/1/21.
 */

public class BitmapUtil {
    /**
     * 获取缩放图片
     * @param filePath
     * @param width
     * @return
     */
    public static Bitmap createBitmapWithFile(String filePath,int width){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);
        options.inSampleSize = options.outWidth/width;
        if(options.outWidth==0){
            options.outWidth = width;
            options.outHeight = width * 4 / 3;
        }else {
            options.outHeight = options.outHeight *width / options.outWidth;
            options.outWidth = width;
        }
        options.inDither = false; /*不进行图片抖动处理*/
        options.inJustDecodeBounds = false;
        options.inPreferredConfig= Bitmap.Config.ARGB_4444;  /*设置让解码器以最佳方式解码*/
        /* 下面两个字段需要组合使用 */

        options.inPurgeable = true;

        options.inInputShareable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath,options);
        return bitmap;

    }

    /**
     * 通过bitmap获取图片
     * @param filePath
     * @param bitmap
     */
    public static void createPictureWithBitmap(String filePath,Bitmap bitmap){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
        }
    }

    /**
     * 通过bitmap获取图片
     * @param filePath
     * @param bitmap
     * @param percent  图片的压缩比，100是0%压缩
     */
    public static void createPictureWithBitmap(String filePath,Bitmap bitmap,int percent){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,percent,outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
        }
    }
}
