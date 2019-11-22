package com.egar.mediaui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.egar.mediaui.Icallback.IKeyBack;
import com.egar.mediaui.Icallback.IMediaBtuClick;
import com.egar.mediaui.Icallback.ITouchListener;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.adapter.UsbFragAdapter;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.util.SharedPreferencesUtils;
import com.egar.mediaui.view.CustomViewPager;
import com.egar.mediaui.view.NiceViewPagerIndicator;
import com.egar.usbimage.fragment.UsbImageMainFragment;
import com.egar.usbmusic.fragment.UsbMusicMainFragment;
import com.egar.usbvideo.fragment.UsbVideoMainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/25 14:07
 * @see {@link }
 */
public class BaseUsbFragment extends BaseLazyLoadFragment implements ViewPager.OnPageChangeListener {
    private String TAG = "BaseUsbFragment";
    //==========Widgets in this Fragment==========
    private View contentV;
    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;
    // Current page
    private NiceViewPagerIndicator mIndicator;
    private CustomViewPager mUsbViewPager;
    private UsbFragAdapter adapter;
    private BaseLazyLoadFragment mUsbCurrenfrag;
    private BaseUsbFragment baseUsbFragment;
    private LinearLayout usb_frag;
    private
    List<String> mTitles = new ArrayList<>();
    //回调touche事件
    private ArrayList<ITouchListener> touchListeners = new ArrayList<>();

    private ITouchListener iTouchListener;
    //back事件
    private IKeyBack iKeyBack;
    //点击事件
    private IMediaBtuClick iMediaBtuClick;
    private boolean isFrist = true;

    @Override
    public int getPageIdx() {
        return Configs.PAGE_INDX_USB;
    }

    @Override
    public void onWindowChangeFull() {
    }

    @Override
    public void onWindowChangeHalf() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachedActivity = (MainActivity) context;
    }

    @Override
    public void initView() {
   //     LogUtil.i("Usb init");

    }

    /**
     * 初始化页面
     */
    public void initPage() {
        int position = (int) SharedPreferencesUtils.getParam(mAttachedActivity, "currentUsbPage", 0);
        mUsbCurrenfrag = MainPresent.getInstatnce().getCurrentUsbFragmen(position);//usbmusic
        mUsbViewPager.setCurrentItem(position, false);
    }

    /**
     * 让Source按键切换使用
     */
    public void setCurrentMusicPage() {
        LogUtil.d(TAG, "setCurrentMusicPage");
        mUsbViewPager.setCurrentItem(0, false);
    }

    @Override
    protected int getLayouId() {
        return R.layout.fragment_usb;
    }


    @Override
    public void onPageLoadStart() {
        if (isFrist) {
            mTitles.add("UsbMusic");
            mTitles.add("UsbVideo");
            mTitles.add("UsbImage");
            mUsbViewPager = findViewById(R.id.usbViepager);
            usb_frag = findViewById(R.id.usb_frag);
            adapter = new UsbFragAdapter(getChildFragmentManager());
            mIndicator = (NiceViewPagerIndicator) findViewById(R.id.niceIndicator2);
            mIndicator.setIndicatorLengthType(NiceViewPagerIndicator.IndicatorType.EQUAL_TEXT)
                    .setIndicatorShapeType(NiceViewPagerIndicator.IndicatorShape.LINEAR)
                    .setIndicatorColor(Color.BLUE);
            adapter.refresh(MainPresent.getInstatnce().getUsbFragmentList(), mTitles);
            mUsbViewPager.setAdapter(adapter);
            mUsbViewPager.setOffscreenPageLimit(3);
            mUsbViewPager.addOnPageChangeListener(this);
            mIndicator.setUpViewPager(mUsbViewPager);
            initPage();
            isFrist = false;
        } else {
            if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                    getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                    getUsbCurrenfrag() instanceof UsbImageMainFragment) {
                getUsbCurrenfrag().onPageLoadStart();
            }
        }


       // LogUtil.i("Usb onPageLoadStart");
    }

    @Override
    public void onPageResume() {
       // LogUtil.i("onPageResume");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onPageResume();
        }
    }

    @Override
    public void onPageStop() {
       // LogUtil.i("onPageStop");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onPageStop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    /**
     * 设置Indicator状态栏是否可
     * 见
     *
     * @param visable
     */
    public void setIndicatorVisib(boolean visable) {
        if (visable) {
            mIndicator.setVisibility(View.VISIBLE);
        } else {
            mIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //可以同步按钮状态
        SharedPreferencesUtils.setParam(mAttachedActivity, "currentUsbPage", position);
        Log.i(TAG, "currentUsbPage(" + position + ")");
        try {
            mUsbCurrenfrag = MainPresent.getInstatnce().getCurrentUsbFragmen(position);
            mAttachedActivity.setItemBackage(mUsbCurrenfrag);
        } catch (Exception e) {
            Log.i(TAG, "ViewPagerOnChange >> onPageSelected() >> [e: " + e.getMessage());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void dispatchTouchEvent(MotionEvent ev) {
        for (ITouchListener listener : touchListeners) {
            if (listener != null &&mUsbViewPager !=null) {
                //  LogUtil.i("dispatchTouchEvent ="+listener.onTouchEvent(ev));
                mUsbViewPager.setIsScanScroll(listener.onTouchEvent(ev));
            }
        }
    }

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     */
    public void registerMyTouchListener(ITouchListener listener) {
        // LogUtil.i("listener =>"+touchListeners.size());
        touchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     */
    public void unRegisterMyTouchListener(ITouchListener listener) {
        touchListeners.remove(listener);
    }

    // key back事件
    public void onBackPressed() {
        LogUtil.i(TAG, "onBackPressed");
        if (iKeyBack == null) {
            mAttachedActivity.exitApp();
        } else {
            iKeyBack.onBack();
        }
    }


    public void registBackEvent(IKeyBack iKeyBack) {
        this.iKeyBack = iKeyBack;
    }

    public void unRegistBackEvent() {
        this.iKeyBack = null;
    }

    public BaseLazyLoadFragment getUsbCurrenfrag() {
        return mUsbCurrenfrag;
    }

    @Override
    public void onPageLoadStop() {
        LogUtil.i("Usb onPageLoadStop");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onPageLoadStop();
        }
    }


    public void onNextClick() {
        iMediaBtuClick.onNextClick();
    }

    public void onNextLongClick() {
        iMediaBtuClick.onNextLongClick();
    }

    public void onPrevClick() {
        iMediaBtuClick.onPrevClick();
    }

    public void onPrevLongClick() {
        iMediaBtuClick.onNextLongClick();
    }

    public void setMediaBtnClickListener(IMediaBtuClick iMediaBtuClick) {
        this.iMediaBtuClick = iMediaBtuClick;
    }

    public void removeMediaBtnClick() {
        iMediaBtuClick = null;
    }
}
