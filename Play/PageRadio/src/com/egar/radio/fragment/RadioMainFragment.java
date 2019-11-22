package com.egar.radio.fragment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;

/**
 * PAGE - Radio
 */
public class RadioMainFragment extends BaseLazyLoadFragment {
    // TAG
    private static final String TAG = "RadioMainFragment";

    //==========Widgets in this Fragment==========
    private View contentV;

    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;

    @Override
    public int getPageIdx() {
        return Configs.PAGE_IDX_RADIO;
    }

    @Override
    public void onWindowChangeFull() {
        LogUtil.i(TAG,"onWindowChangeFull");
    }

    @Override
    public void onWindowChangeHalf() {
        LogUtil.i(TAG,"onWindowChangeHalf");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(TAG,"onAttach");
        mAttachedActivity = (MainActivity) context;
    }


    @Override
    public void initView() {
        LogUtil.i(TAG,"initView");
        TextView tv = findViewById(R.id.tv_radio);
        tv.setIncludeFontPadding(false);


    }

    public void onBackPressed() {
        mAttachedActivity.exitApp();
    }

    @Override
    protected int getLayouId() {
        return R.layout.radio_frag_main;
    }

    @Override
    public void onPageResume() {
        LogUtil.i(TAG,"onPageResume");
    }

    @Override
    public void onPageStop() {
        LogUtil.i("onPageStop");
    }

    @Override
    public void onPageLoadStart() {

        LogUtil.i(TAG,"onPageLoadStart");
    }

    @Override
    public void onPageLoadStop() {
        LogUtil.i(TAG,"onPageLoadStop");
    }


}