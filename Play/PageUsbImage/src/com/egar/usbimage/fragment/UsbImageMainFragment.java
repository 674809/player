package com.egar.usbimage.fragment;

import android.content.Context;
import android.view.View;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;

/**
 * PAGE - Usb Image
 */
public class UsbImageMainFragment extends BaseUsbScrollLimitFragment {
    // TAG
    private static final String TAG = "UsbImageMainFrag";

    //==========Widgets in this Fragment==========
    private View contentV;

    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;
    private BaseUsbFragment fragment;


    @Override
    public int getPageIdx() {
        return Configs.PAGE_IDX_USB_IMAGE;
    }

    @Override
    public void onWindowChangeFull() {
        super.onWindowChangeFull();
        LogUtil.i("onWindowChangeFull");
    }

    @Override
    public void onWindowChangeHalf() {
        super.onWindowChangeHalf();
        LogUtil.i("onWindowChangeHalf");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachedActivity = (MainActivity) context;

    }


    @Override
    public void initView() {
        super.initView();
        fragment = (BaseUsbFragment) MainPresent.getInstatnce().getCurrenFragmen(Configs.PAGE_INDX_USB);

    }

    @Override
    protected int getLayouId() {
        super.getLayouId();
        return R.layout.usb_image_frag_main;
    }

    @Override
    public void onPageResume() {
        super.onPageResume();
        LogUtil.i("onPageResume");
    }

    @Override
    public void onPageStop() {
        super.onPageStop();
        LogUtil.i("onPageStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        LogUtil.i("onResumePage()");
    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        LogUtil.i("onStopPage()");
    }


    @Override
    public void onBack() {
        super.onBack();
        mAttachedActivity.exitApp();
    }
}