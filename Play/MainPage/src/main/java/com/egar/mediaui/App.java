package com.egar.mediaui;

import android.app.Application;
import android.content.Context;

import com.egar.btmusic.fragment.BTMusicApp;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.fragment.RadioApp;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 10:09
 * @see {@link }
 */
public class App extends Application {
    private String TAG = "MyApplication";
    private BTMusicApp mBtMusicApp;
    private RadioApp mRadio;
    public static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        initClient();
        mContext = getApplicationContext();
    }

    private void initClient() {
        mBtMusicApp = new BTMusicApp();
        mBtMusicApp.AppOnCreate();
        mRadio = new RadioApp();
        mRadio.AppOnCreate();
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.i(TAG,"onTerminate");
    }

}
