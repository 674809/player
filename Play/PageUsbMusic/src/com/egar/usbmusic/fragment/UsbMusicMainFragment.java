package com.egar.usbmusic.fragment;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.FrameLayout;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.adapter.MainFragAdapter;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.lib.NoScrollViewPager;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.present.MusicPresent;

/**
 * PAGE - Usb Music
 */
public class UsbMusicMainFragment extends BaseUsbScrollLimitFragment implements  ViewPager.OnPageChangeListener, IFinishActivity,
        MediaBoardcast.IMediaReceiver{
    // TAG
    private static final String TAG = "UsbMusicMainFrag";

    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;
    private BaseUsbFragment fragment;
    private FrameLayout frameleft, frameright;
    private NoScrollViewPager viewPager;
    private MusicPresent musicPresent; //音乐操作类
   // public boolean isScroll = true;
    private boolean isFrist = true;
    private int position = 0;

    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_IDX_USB_MUSIC;
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
        LogUtil.i(TAG,"onAttach");
    }

    @Override
    public void initView() {
        super.initView();
        fragment = (BaseUsbFragment) MainPresent.getInstatnce().getCurrenFragmen(Configs.PAGE_INDX_USB);

    }

    @Override
    protected int getLayouId() {
        super.getLayouId();
        isFrist = true;
        return R.layout.usb_music_frag_main;
    }


    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        LogUtil.i("onPageLoadStart");
        initPlayerAndFolderFragment();
        initMusicClient();
        viewPager.setCurrentItem(0,false);
        mAttachedActivity.setFinishActivitListener(this);
        MediaBoardcast.registerNotify("usbPlayer", this);
        musicPresent.bindPlayService(true);
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

    /**
     * 初始化Fragment
     */
    private void initPlayerAndFolderFragment() {
        if (isFrist) {
            //   LogUtil.w("init music Main");
            // 将myTouchListener注册到分发列表
            viewPager = findViewById(R.id.viewpager_music);
            MainFragAdapter adapter = new MainFragAdapter<BaseLazyLoadFragment>(mAttachedActivity.getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(this);
            adapter.refresh(MainPresent.getInstatnce().getUsbMusicFragmentList());
            isFrist = false;
        } else { //第一次默认会调用lazyLoad
            reLoadeLazyChild();
        }
    }

    /**
     * 初始化音乐播放器句柄
     */
    private void initMusicClient() {
        if (musicPresent == null) {
            musicPresent = MusicPresent.getInstance();
            musicPresent.creatMuiscService(App.getContext());
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
           ((UsbMusicPlayerFragment) MainPresent.getInstatnce().getUsbMuiscChiledFragment(Configs.PAGE_USB_MUSIC_PLAY)).onPageLoadStart();
        } else if (position == 1) {
            ((UsbMuiscFolderFragment) MainPresent.getInstatnce().getUsbMuiscChiledFragment(Configs.PAGE_USB_MUSIC_FOLDER)).onPageLoadStart();
        }
    }


    /**
     * 设置页面切换
     *
     * @param position
     */
    public void setUsbPlayerCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");

    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        // LogUtil.w("onPageLoadStop() Music Main");
        ((UsbMusicPlayerFragment) MainPresent.getInstatnce().getUsbMuiscChiledFragment(Configs.PAGE_USB_MUSIC_PLAY)).onPageLoadStop();
        ((UsbMuiscFolderFragment) MainPresent.getInstatnce().getUsbMuiscChiledFragment(Configs.PAGE_USB_MUSIC_FOLDER)).onPageLoadStop();
        //退出MusicMain 时停止播放歌曲
        ((UsbMusicPlayerFragment) MainPresent.getInstatnce().getUsbMuiscChiledFragment(Configs.PAGE_USB_MUSIC_PLAY)).stopPlayer();
        musicPresent.bindPlayService(false);
        MediaBoardcast.removeNotify("usbPlayer");
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        LogUtil.i(TAG, "position ="+position);
        if (position == 0) { //folder 页面禁止滑动，并且隐藏Indicator
            MainPresent.getInstatnce().setInditeHide(true);
            isScroll = true;
        } else {
            isScroll = false;
            MainPresent.getInstatnce().setInditeHide(false);
        }
        LogUtil.i(TAG,"isScroll ="+isScroll);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFinishActivity() {
        if(musicPresent.isPlaying()){
            musicPresent.playOrPauseByUser();
        }
    }

    @Override
    public void onUdiskStateChange(int state) {
        position = viewPager.getCurrentItem();
        if (position == 0) {
            ((UsbMusicPlayerFragment) MainPresent.getInstatnce().getUsbMuiscChiledFragment(Configs.PAGE_USB_MUSIC_PLAY)).onUdiskStateChange(state);
        }
    }
}