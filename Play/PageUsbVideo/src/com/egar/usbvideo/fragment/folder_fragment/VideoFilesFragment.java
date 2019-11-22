package com.egar.usbvideo.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.adapter.UsbVideoFileAdapter;
import com.egar.usbvideo.fragment.UsbVideoMainFragment;
import com.egar.usbvideo.fragment.UsbVideoPlayFragment;
import com.egar.usbvideo.model.UsbVideoFilesModel;
import com.egar.usbvideo.present.VideoPlayPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/28 10:26
 * @see {@link }
 */
public class VideoFilesFragment extends BaseLazyLoadFragment implements UsbVideoFilesModel.IVideoFilesDataListener, ListView.OnItemClickListener {

    private String TAG = "VideoFilesFragment";
    private int page = 0;
    private ListView video_lv;
    private UsbVideoFileAdapter mUsbVideoFileAdapter;
    private MainActivity activity;
    private UsbVideoPlayFragment videofragment;
    private VideoPlayPresent mvideoPresent;
    private UsbVideoFilesModel usbVideoFilesModel;
    private boolean mIsClicking = false;
    private List<ProVideo> mListMedias = new ArrayList<>();
    private Handler mmHandler = new Handler();
    private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

        @Override
        public void run() {
            mIsClicking = false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public int getPageIdx() {
        return page;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    public void initView() {
        mvideoPresent = VideoPlayPresent.getInstance();

        video_lv = findViewById(R.id.video_lv);
        mUsbVideoFileAdapter = new UsbVideoFileAdapter(activity);
        video_lv.setAdapter(mUsbVideoFileAdapter);
        usbVideoFilesModel = new UsbVideoFilesModel();
        usbVideoFilesModel.setVideoFilsDataListener(this);
        usbVideoFilesModel.LoadData();
        video_lv.setOnItemClickListener(this);
        video_lv.setOnScrollListener(new LvOnScroll());
    }


    @Override
    protected int getLayouId() {
        return R.layout.usb_video_files_fragment;
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onPageStop() {

    }

    @Override
    public void onPageLoadStart() {

    }

    @Override
    public void onPageLoadStop() {

    }


    @Override
    public void VideoFilesDataChange(List<ProVideo> video) {
        mListMedias = video;
        LogUtil.i(TAG, "mListMedias size =" + mListMedias.size());
        mUsbVideoFileAdapter.refreshData(video, mvideoPresent.getLastTargetMediaPath());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        execItemClick(position);
        ((UsbVideoMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_IDX_USB_VIDEO)).setVideoPager(Configs.PAGE_USB_VIDEO_PLAY);

    }

    private void execItemClick(int position) {
        if (mListMedias.size() <0) {
            return;
        }

        if (mIsClicking) {
            mIsClicking = false;
            LogUtil.d(TAG, "##### ---Forbidden click because of frequency !!!--- #####");
            return;
        } else {
            mIsClicking = true;
            mmHandler.removeCallbacksAndMessages(null);
            mmHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);
        }

        //
       // ProVideo item = (ProVideo) objItem;

        VideoPlayPresent.getInstance().execPlay(position);

        LogUtil.d(TAG, "LvItemClick -> onItemClick ----Just Play----");

    }

    private void destroy() {
        mmHandler.removeCallbacksAndMessages(null);
    }

    /**
     * ListView scroll event.
     */
    private class LvOnScroll implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_TOUCH_SCROLL:
                    mUsbVideoFileAdapter.setScrollState(true);
                    //  Log.i(TAG, "LvOnScroll -SCROLL_STATE_TOUCH_SCROLL-");
                    break;
                case SCROLL_STATE_IDLE:
                    mUsbVideoFileAdapter.setScrollState(false);
                    //   Log.i(TAG, "LvOnScroll -SCROLL_STATE_IDLE-");
                    break;
                case SCROLL_STATE_FLING:
                    mUsbVideoFileAdapter.setScrollState(true);
                    //  Log.i(TAG, "LvOnScroll -SCROLL_STATE_FLING-");
                    break;

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }
}
