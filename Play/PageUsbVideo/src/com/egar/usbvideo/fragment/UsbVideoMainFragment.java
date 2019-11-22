package com.egar.usbvideo.fragment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.adapter.MainFragAdapter;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.lib.NoScrollViewPager;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.utils.PreferenceHelper;


/**
 * PAGE - Usb Video
 */
public class UsbVideoMainFragment extends BaseUsbScrollLimitFragment implements ViewPager.OnPageChangeListener,
        MediaBoardcast.IMediaReceiver, IFinishActivity {
    // TAG
    private static final String TAG = "UsbVideoMainFrag";

    //==========Widgets in this Fragment==========
    private View contentV;

    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;
    private NoScrollViewPager viewPager;
    private boolean isFrist = true;
    private int position = 0;
    public boolean isScroll = true;
    private UsbVideoPlayFragment fragment;

    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_IDX_USB_VIDEO;
    }

    @Override
    public void onWindowChangeFull() {
        LogUtil.i("onWindowChangeFull");
    }

    @Override
    public void onWindowChangeHalf() {
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
        PreferenceHelper.init(mAttachedActivity);

    }

    @Override
    protected int getLayouId() {
        super.getLayouId();
        isFrist = true;
        return R.layout.usb_video_frag_main;
    }

    @Override
    public void onPageResume() {
        super.onPageResume();
        LogUtil.i("onPageResume");
        position = viewPager.getCurrentItem();
        if (position == 0) {
            ((UsbVideoPlayFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_PLAY)).onPageResume();
        } else if (position == 1) {
            ((UsbVideoFolderFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_FOLDER)).onPageResume();
        }
    }

    @Override
    public void onPageStop() {
        super.onPageStop();
        LogUtil.i("onPageStop");
        position = viewPager.getCurrentItem();
        if (position == 0) {
            ((UsbVideoPlayFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_PLAY)).onPageStop();
        } else if (position == 1) {
            ((UsbVideoFolderFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_FOLDER)).onPageStop();
        }
    }

    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        initPlayerAndFolderFragment();
        viewPager.setCurrentItem(0,false);
        MediaBoardcast.registerNotify("UsbVideoMainFragment",this);
        mAttachedActivity.setFinishActivitListener(this);
        init();
        fragment = ((UsbVideoPlayFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_PLAY));
        LogUtil.i("onPageLoadStart");

    }
    /**
     * 初始化Fragment
     */
    private void initPlayerAndFolderFragment() {
        if (isFrist) {
            //   LogUtil.w("init music Main");
            // 将myTouchListener注册到分发列表
            viewPager = findViewById(R.id.viewpager_video);
            MainFragAdapter adapter = new MainFragAdapter<BaseLazyLoadFragment>(mAttachedActivity.getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(this);
            adapter.refresh(MainPresent.getInstatnce().getUsbVideoFragmentList());
            isFrist = false;
        } else { //第一次默认会调用lazyLoad
            reLoadeLazyChild();
        }
    }

    /**
     * 页面在Paly页面时，切换到usbVideo 回来后不会再调用paly 页面的loadLazy()函数
     * 页面可见时，调用当前页面的可见函数
     */
    private void reLoadeLazyChild() {
        //  LogUtil.i("Music init");
        position = viewPager.getCurrentItem();
        if (position == 0) {
            ((UsbVideoPlayFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_PLAY)).onPageLoadStart();
        } else if (position == 1) {
            ((UsbVideoFolderFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_FOLDER)).onPageLoadStart();
        }
    }
    private void init() {

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(fragment !=null){
            fragment.stopPlay();
        }
    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        LogUtil.i("onPageLoadStop video");
        ((UsbVideoPlayFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_PLAY)).onPageLoadStop();
        ((UsbVideoFolderFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_FOLDER)).onPageLoadStop();
        MediaBoardcast.removeNotify("UsbVideoMainFragment");

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.i(TAG, "position ="+position);
        if (position == 0) { //folder 页面禁止滑动，并且隐藏Indicator
            isScroll = true;
            MainPresent.getInstatnce().setInditeHide(true);
        } else {
            isScroll = false;
            MainPresent.getInstatnce().setInditeHide(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setVideoPager(int pager){
        viewPager.setCurrentItem(pager,false);
    }

    @Override
    public void onUdiskStateChange(int state) {

    }


    @Override
    public void onFinishActivity() {
        LogUtil.i(TAG,"onFinishActivity");
        ((UsbVideoPlayFragment) MainPresent.getInstatnce().getUsbVideoChiledFragment(Configs.PAGE_USB_VIDEO_PLAY)).stopPlay();
    }


}