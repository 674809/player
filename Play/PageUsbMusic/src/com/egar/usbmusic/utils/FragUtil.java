package com.egar.usbmusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.egar.mediaui.App;
import com.egar.mediaui.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/16 17:36
 * @see {@link }
 */
public class FragUtil {

    /**
     * Fragment 嵌套加载
     *
     * @param replaceId 替代View的ID
     * @param frag      目标Fragment
     * @param fm        FragmentManager
     */
    public static void loadV4ChildFragment(int replaceId,
                                           android.support.v4.app.Fragment frag,
                                           android.support.v4.app.FragmentManager fm) {
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(replaceId, frag);
        transaction.commit();
    }

    /**
     * Load V4 Fragment
     */
    public static void removeV4Fragment(android.support.v4.app.Fragment frag, android.support.v4.app.FragmentManager fm) {
        if (fm != null) {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.remove(frag);
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
          //  e.printStackTrace();
            Bitmap bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.album_bg_em);
            return bitmap;
        }
    }

}
