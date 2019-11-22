package com.egar.mediaui.fragment;

import android.view.MotionEvent;

import com.egar.mediaui.Icallback.IKeyBack;
import com.egar.mediaui.Icallback.ITouchListener;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/1 09:56
 * @see {@link }
 */
public   class BaseUsbScrollLimitFragment extends BaseLazyLoadFragment   implements ITouchListener , IKeyBack {
    public boolean isScroll = true;
    private BaseUsbFragment fragment;
    @Override
    public int getPageIdx() {
        return 0;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    public void initView() {

    }

    @Override
    protected int getLayouId() {
        return 0;
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onPageStop() {

    }

    @Override
    public void onPageLoadStart() {
        fragment = (BaseUsbFragment) MainPresent.getInstatnce().getCurrenFragmen(Configs.PAGE_INDX_USB);
        fragment.registerMyTouchListener(this);
        fragment.registBackEvent(this);

    }

    @Override
    public void onPageLoadStop() {
        if(fragment != null ){
            fragment.unRegisterMyTouchListener(this);
            fragment.unRegistBackEvent();
        }

    }
    private float mDownX, mTargetX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                //     Log.i(TAG, "mDownX : " + mDownX);
                break;
            case MotionEvent.ACTION_MOVE:
                mTargetX = event.getX();
                //    Log.i(TAG, "ACTION_MOVE >> mDownX - mUpX = " + (mDownX - mTargetX));
                break;
            case MotionEvent.ACTION_UP:
                mTargetX = event.getX();
                // Log.i(TAG, "ACTION_UP >> mDownX - mUpX = " + (mDownX - mTargetX));

                break;
        }
        //  LogUtil.i(TAG,"windowidth"+mAttachedActivity.getWindowsWidth());
        if (mDownX >= 860 || mDownX <= 100) {
            return isScroll;
        } else {
            return false;
        }
    }

    @Override
    public void onBack() {
        LogUtil.i(TAG,"onBack");
    }
}
