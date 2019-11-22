package com.egar.btmusic.fragment;

import android.content.Context;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;

/**
 * PAGE - BT Music
 */
public class BtMusicMainFragment extends BaseLazyLoadFragment  {
    // TAG
    private static final String TAG = "BtMusicMainFrag";


    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;

    @Override
    public int getPageIdx() {
        return Configs.PAGE_IDX_BT_MUSIC;
    }

    @Override
    public void onWindowChangeFull() {
        LogUtil.i("onWindowChangeFull");

    }

    @Override
    public void onWindowChangeHalf() {
        LogUtil.i("onWindowChangeHalf");

    }
    public void onBackPressed(){

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachedActivity = (MainActivity) context;

    }

    @Override
    public void initView() {

    }


    @Override
    protected int getLayouId() {
        return R.layout.bt_frag_main;
    }

    @Override
    public void onPageResume() {
        LogUtil.i("onPageResume");
    }

    @Override
    public void onPageStop() {
        LogUtil.i("onPageStop");
    }

    @Override
    public void onPageLoadStart() {
    LogUtil.i("onPageLoadStart");
    }



    @Override
    public void onPageLoadStop() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}