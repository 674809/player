package com.egar.usbvideo.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.Icallback.IMediaBtuClick;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.view.MyButton;
import com.egar.usbmusic.utils.UdiskUtil;
import com.egar.usbvideo.engine.AudioPresent;
import com.egar.usbvideo.engine.MediaLightModeController;
import com.egar.usbvideo.interfaces.IAudioFocusListener;
import com.egar.usbvideo.interfaces.IRefreshUI;
import com.egar.usbvideo.present.VideoPlayPresent;
import com.egar.usbvideo.utils.VideoPreferUtils;
import com.egar.usbvideo.view.PanelTouchImpl;
import com.egar.usbvideo.view.SeekBarImpl;
import com.egar.usbvideo.view.VideoTextureView;

import java.io.File;

import juns.lib.java.utils.date.DateFormatUtil;
import juns.lib.media.bean.ProVideo;
import juns.lib.media.flags.PlayMode;
import juns.lib.media.flags.PlayState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/25 15:17
 * @see {@link }
 */
public class UsbVideoPlayFragment extends BaseUsbScrollLimitFragment implements View.OnClickListener,
        IMediaBtuClick, IRefreshUI, IAudioFocusListener, IFinishActivity, MediaBoardcast.IMediaReceiver {
    private String TAG = "UsbVideoPlayFragment";
    private View layoutRoot;
    private View layoutTop;
    private View vCoverPanel;
    private ImageView vArrowRight, vArrowLeft;
    private MainActivity activity;
    private UsbVideoMainFragment fragment;
    //
    private RelativeLayout rlVvPlayerBorder;
    private RelativeLayout layout_play,layout_udisk;

    private View vControlPanel;
    private TextView tvFolder, tvPosition, tvName, tvStartTime, tvEndTime;
    private SeekBarImpl seekBar;
    private ImageView ivPlayPre, ivPlay, ivPlayNext;
    private MyButton ivPlayModeSet, ivList;
    private View layoutWarning;
    public VideoTextureView vvPlayer;
    private AudioPresent audioPresent;
    private PanelTouchResp mPanelTouchResp;
    private MediaLightModeController mLightModeController;
    private VideoPlayPresent mVideoPlayPresent;


    private SeekBarOnChange mSeekBarOnChange;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_USB_VIDEO_PLAY;
    }

    @Override
    public void onWindowChangeFull() {
        LogUtil.i(TAG,"onWindowChangeFull");
        if(mVideoPlayPresent !=null){
            ProVideo video = mVideoPlayPresent.getCurrProVideo();
            scaleScreen(video);
        }

    }

    @Override
    public void onWindowChangeHalf() {
        LogUtil.i(TAG,"onWindowChangeHalf");
        if(mVideoPlayPresent!=null){
            ProVideo video = mVideoPlayPresent.getCurrProVideo();
            scaleScreen(video);
        }

    }

    @Override
    public void initView() {
        super.initView();
        fragment = (UsbVideoMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_IDX_USB_VIDEO);

        // ----Widgets----
        layoutRoot = findViewById(R.id.v_root);
        layoutTop = findViewById(R.id.layout_top);
        vArrowLeft = (ImageView) findViewById(R.id.v_arrow_left);
        vArrowRight = (ImageView) findViewById(R.id.v_arrow_right);

        // Cover panel
        PanelTouchImpl mPanelTouchImpl = new PanelTouchImpl();
        mPanelTouchImpl.init(activity);
        mPanelTouchImpl.addCallback((mPanelTouchResp = new PanelTouchResp()));

        vCoverPanel = findViewById(R.id.vv_cover);
        vCoverPanel.setOnTouchListener(mPanelTouchImpl);

        //
        vControlPanel = findViewById(R.id.v_control_panel);
        tvFolder = (TextView) findViewById(R.id.v_folder_name);
        tvFolder.setText("");

        tvPosition = (TextView) findViewById(R.id.v_sort);
        tvPosition.setText("");

        tvName = findViewById(R.id.v_name);
        tvName.setText("");

        rlVvPlayerBorder = (RelativeLayout) findViewById(R.id.rl_vv_border);
        vvPlayer = findViewById(R.id.vv_player);
        vvPlayer.setKeepScreenOn(true);
        vvPlayer.setDrawingCacheEnabled(false);


        tvStartTime = findViewById(R.id.tv_play_start_time);
        tvEndTime = findViewById(R.id.tv_play_end_time);
        seekBar = findViewById(R.id.seekbar);
        mSeekBarOnChange = new SeekBarOnChange();
        seekBar.setOnSeekBarChangeListener(mSeekBarOnChange);
        seekBar.setOnTouchListener(new SeekOnTouch());

        ivPlayPre = findViewById(R.id.iv_play_pre);
        ivPlayPre.setOnClickListener(this);

        ivPlay = findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(this);

        ivPlayNext = findViewById(R.id.iv_play_next);
        ivPlayNext.setOnClickListener(this);

        ivPlayModeSet = (MyButton) findViewById(R.id.iv_play_mode_set);
        ivPlayModeSet.setOnClickListener(this);


        ivList = (MyButton) findViewById(R.id.v_list);

        ivList.setOnClickListener(this);

        layout_udisk = findViewById(R.id.layout_udisk);
        layout_play = findViewById(R.id.layout_play);

        //Warning
//        mGpsImpl = new GpsImpl(this);//Register GPS
//        mGpsImpl.setGpsImplListener((mGpsOnChange = new GpsOnChange()));

        layoutWarning = findViewById(R.id.layout_warning);
        layoutWarning.setVisibility(View.GONE);
        //   vControlPanel.setOnTouchListener();
//       layoutWarning.setOnTouchListener(new WarningPageOnTouch());

        initData();
        updateUI();
        updataUdiskPage();
    }

    private void updateUI() {
        int playMode = VideoPreferUtils.getPlayMode();
        if (playMode == PlayMode.SINGLE) {
            ivPlayModeSet.setImageResource(R.drawable.repeat_ico);
        } else if (playMode == PlayMode.LOOP) {
            ivPlayModeSet.setImageResource(R.drawable.usb_loop);
        }

    }

    /**
     * 更新U盘页面
     */
    public void updataUdiskPage(){
        boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
        if (ismount) {
            layout_udisk.setVisibility(View.GONE);
            layout_play.setVisibility(View.VISIBLE);
        } else {
            layout_udisk.setVisibility(View.VISIBLE);
            layout_play.setVisibility(View.GONE);
        }
    }


    private void initData() {
        mVideoPlayPresent = VideoPlayPresent.getInstance();
        mVideoPlayPresent.init(vvPlayer);
        audioPresent = new AudioPresent(this, activity);
        mLightModeController = new MediaLightModeController();
        mLightModeController.addModeListener(new MediaLightModeOnChange());
        activity.setFinishActivitListener(this);
        mVideoPlayPresent.setRefreshUI(this);
        synchronized (this) {
        }
    }


    //重置进度条
    private void resetSeekBar() {
        try {
            LogUtil.d(TAG, "resetSeekBar()");
            if (seekBar != null) {
                seekBar.setEnabled(true);
                //seekBar.setMax((int) mListPrograms.get(mPlayPos).duration);
                seekBar.setProgress(0);
            }
        } catch (Exception e) {
            LogUtil.e(TAG + "_resetSeekBar", e.getMessage());
        }
    }


    @Override
    protected int getLayouId() {
        super.getLayouId();
        return R.layout.usb_video_frag_play;
    }

    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        LogUtil.i(TAG, "lazyload");
        if (!audioPresent.isAudioFocuseGained()) {
            audioPresent.registerAudioFocus(1);
        }
        ((BaseUsbFragment) MainPresent.getInstatnce().getCurrenFragmen(Configs.PAGE_INDX_USB)).setMediaBtnClickListener(this);
        mLightModeController.resetLightMode();
        MediaBoardcast.registerNotify("usbVideo", this);
        //  loadLocalMedias();
        updataUdiskPage();

    }


    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        //  vvPlayer.setPlayAtBgOnSufaceDestoryed(false);
        MediaBoardcast.removeNotify("usbVideo");
    }

    @Override
    public void onPageResume() {
        super.onPageResume();
        updataUdiskPage();
        // vvPlayer.setPlayAtBgOnSufaceDestoryed(true);
    }


    @Override
    public void onPageStop() {
        super.onPageStop();

    }

    public void stopPlay() {

    }

    @Override
    public void onClick(View v) {
        LogUtil.i(TAG, "click");
        switch (v.getId()) {
            case R.id.v_list:
                fragment.setVideoPager(Configs.PAGE_USB_VIDEO_FOLDER);
                break;
            case R.id.iv_play_pre:
                if (mVideoPlayPresent != null) {
                    mVideoPlayPresent.playPrev();
                }
                break;
            case R.id.iv_play:
                if (mVideoPlayPresent != null) {
                    if (mVideoPlayPresent.isPlaying()) {
                        mVideoPlayPresent.pause();
                    } else {
                        mVideoPlayPresent.resume();
                    }
                }
                break;
            case R.id.iv_play_next:
                if (mVideoPlayPresent != null) {
                    mVideoPlayPresent.playNext();
                }
                break;
            case R.id.iv_play_mode_set:
                if (mVideoPlayPresent != null) {
                    mVideoPlayPresent.switchPlayMode();
                }
                break;
        }
    }


    //=============================================================================
    @Override
    public void onNextLongClick() {
        LogUtil.i(TAG, "next long");
    }

    @Override
    public void onNextClick() {
        LogUtil.i(TAG, "next ");
    }

    @Override
    public void onPrevLongClick() {
        LogUtil.i(TAG, "prev long");
    }

    @Override
    public void onPrevClick() {
        LogUtil.i(TAG, "prev ");
    }

    @Override
    public void onResetSeekBar() {
        resetSeekBar();
    }


    @Override
    public void updatePlayStatus(int state) {
        LogUtil.i(TAG, "updatePlayStatus =" + state);
        switch (state) {
            case PlayState.PLAY:
                ivPlay.setImageResource(R.drawable.btn_op_pause_selector);
                break;
            case PlayState.PAUSE:
                ivPlay.setImageResource(R.drawable.btn_op_play_selector);
                break;
        }
        int playMode = mVideoPlayPresent.getPlayMode();
        switch (playMode) {
            case PlayMode.LOOP:
                ivPlayModeSet.setImageResource(R.drawable.usb_loop);
                break;
            case PlayMode.SINGLE:
                ivPlayModeSet.setImageResource(R.drawable.repeat_ico);
                break;
        }

        ProVideo video = mVideoPlayPresent.getCurrProVideo();
        scaleScreen(video);

    }

    @Override
    public void onPlayStateChanged$Play() {
        LogUtil.d(TAG, "onNotifyPlayState$Play()");
        ProVideo currProgram = mVideoPlayPresent.getCurrProVideo();
        if (currProgram == null) {
            return;
        }
        File file = new File(currProgram.getMediaUrl());
        if (file.exists()) {
            File folder = file.getParentFile();
            if (folder != null) {
                tvName.setText(folder.getName());
            }
            tvName.setText(currProgram.getTitle());

            // Position
            String formatStr = getString(R.string.video_pos_str);
            String currPosStr = String.valueOf((mVideoPlayPresent.getCurrIdx() + 1));
            String totalCountStr = String.valueOf(mVideoPlayPresent.getTotalCount());
            tvPosition.setText(String.format(formatStr, currPosStr, totalCountStr));
        }
    }

    @Override
    public void onPlayStateChanged$Prepared() {
        LogUtil.i(TAG, "onPlayStateChanged$Prepared ");
        seekBar.setMax((int) mVideoPlayPresent.getDuration());
        if (mVideoPlayPresent.mTargetAutoSeekProgress > 0 && mVideoPlayPresent.mTargetAutoSeekProgress < seekBar.getMax()) {
            mVideoPlayPresent.seekTo((int) mVideoPlayPresent.mTargetAutoSeekProgress);
            mVideoPlayPresent.mTargetAutoSeekProgress = -1;
        }
    }

    @Override
    public void updateSeekTime(final int progress, final int duration) {
        //此处出现了TextView无法setText赋值的问题
        if (tvStartTime != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStartTime.setText(DateFormatUtil.getFormatHHmmss(progress));
                }
            });

        }
        if (tvEndTime != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration));
                }
            });

        }
    }

    @Override
    public void onVideoProgressChanged(String mediaPath, final int progress, final int duration) {
        seekBar.setMax(duration);
        //  LogUtil.i(TAG, "onProgressChanged=" + progress);
        seekBar.setMax(duration);
        seekBar.setProgress(progress);//更新进度条
        updateSeekTime(progress, duration);
        mVideoPlayPresent.savePlayInfo();
    }

    @Override
    public void onPlayModeChange() {
        updateUI();
    }

    @Override
    public void onAudioFocusDuck() {

    }

    @Override
    public void onAudioFocusTransient() {

    }

    @Override
    public void onAudioFocusGain() {
        LogUtil.i(TAG, "onAudioFocusGain");
        if (mVideoPlayPresent != null) {
            mVideoPlayPresent.play();
        }

    }

    @Override
    public void onAudioFocusLoss() {
        LogUtil.i(TAG, "onAudioFocusLoss");
        if (mVideoPlayPresent != null) {
            mVideoPlayPresent.pause();
        }
    }

    @Override
    public void onAudioFocus(int flag) {
        LogUtil.i(TAG, "onAudioFocus=" + flag);
        if (flag == 1) {
            if (mVideoPlayPresent != null) {
                mVideoPlayPresent.resume();
            }
        } else {
            if (mVideoPlayPresent != null) {
                mVideoPlayPresent.pause();
            }
        }
    }

    @Override
    public void onFinishActivity() {
        if(mVideoPlayPresent !=null){
            mVideoPlayPresent.release();
        }
        activity.exitApp();
    }

    @Override
    public void onUdiskStateChange(int state) {
        LogUtil.i(TAG,"onUdiskStateChange");
        boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
        if (ismount) {
            layout_udisk.setVisibility(View.GONE);
            layout_play.setVisibility(View.VISIBLE);
            if (mVideoPlayPresent != null) {
                mVideoPlayPresent.resume();
            }
        } else {
            layout_udisk.setVisibility(View.VISIBLE);
            layout_play.setVisibility(View.GONE);
            if (mVideoPlayPresent != null) {
                mVideoPlayPresent.pause();
            }
        }
    }

    //==========================================================================
    private class MediaLightModeOnChange implements MediaLightModeController.MediaLightModeListener {
        @Override
        public void onLightOn() {
            LogUtil.d(TAG, "onLightOn()");
            if(UdiskUtil.isHasSupperUDisk(App.getContext())){
                vControlPanel.setVisibility(View.VISIBLE);
            }else {
                vControlPanel.setVisibility(View.GONE);
            }

            // CommonUtils.setNavigationBar(VideoPlayerActivity.this, 1);
        }

        @Override
        public void onLightOff() {
            LogUtil.d(TAG, "onLightOff()");
                vControlPanel.setVisibility(View.GONE);
            //  CommonUtils.setNavigationBar(VideoPlayerActivity.this, 0);
        }
    }


    //===========================================================================
    private class PanelTouchResp implements PanelTouchImpl.PanelTouchCallback {

        @Override
        public void onActionDown() {
            LogUtil.i(TAG, "onActionDown");
        }

        @Override
        public void onActionUp() {
            LogUtil.i(TAG, "onActionUp");
        }

        @Override
        public void onSingleTapUp() {
            LogUtil.i(TAG, "onSingleTapUp");
            if (mLightModeController != null) {
                mLightModeController.switchLightMode();
            }
        }

        @Override
        public void onPrepareAdjustBrightness() {
            LogUtil.i(TAG, "onPrepareAdjustBrightness");
        }

        @Override
        public void onAdjustBrightness(double rate) {
            LogUtil.i(TAG, "onAdjustBrightness");
        }

        @Override
        public void onPrepareAdjustVol() {

        }

        @Override
        public void onAdjustVol(int vol, int maxVol) {

        }

        @Override
        public void onPrepareAdjustProgress() {

        }

        @Override
        public void onAdjustProgress(int direction, int progressDelta) {

        }

        @Override
        public void seekProgress(int direction, int progressDelta) {

        }
    }

    //==========================================================================
    public class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress = 0;
        boolean mmIsTrackingTouch = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            LogUtil.d(TAG, "SeekBarOnChange - onStartTrackingTouch(SeekBar)");
            mmIsTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtil.d(TAG, "SeekBarOnChange - onStopTrackingTouch(SeekBar)");
            if (mmIsTrackingTouch) {
                mmIsTrackingTouch = false;
                mVideoPlayPresent.seekTo(mmProgress);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //  LogUtil.d(TAG, "SeekBarOnChange - onProgressChanged(SeekBar," + progress + "," + fromUser + ")");
            if (fromUser) {
                mmProgress = progress;
            }
        }

        boolean isTrackingTouch() {
            return mmIsTrackingTouch;
        }
    }

    //=========================================
    public class SeekOnTouch implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.d(TAG, "seekBar -> SeekOnTouch -> ACTION_DOWN =" );
                    mLightModeController.keepLightOn();
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.d(TAG, "seekBar -> SeekOnTouch -> ACTION_UP");
                    mLightModeController.resetLightMode();

                    break;
            }
            return false;
        }
    }


    //=======================================================================

    /**
     * 屏幕缩放
     *
     * @param media
     */
    private void scaleScreen(final ProVideo media) {
        if (media == null) {
            return;
        }
        if (media.getWidth() == 0 || media.getHeight() == 0) {
            ProVideo.parseMediaScaleInfo(activity, media);
        }
        LogUtil.d(TAG, "media :: resolution - " + media.getWidth() + "x" + media.getHeight());
        if (media.getWidth() == 0 || media.getHeight() == 0) {
            return;
        }
        if (layoutRoot == null) {
            return;
        }
        //
        layoutRoot.post(new Runnable() {
            @Override
            public void run() {
                int rootW = layoutRoot.getWidth();
                int rootH = layoutRoot.getHeight();
                //  LogUtil.d(TAG, "root :: resolution - " + rootW + "x" + rootH);

                //计算视频画面的大小，与视频原来的大小和屏幕的大小有关
                int targetW = 0, targetH = 0;
                double mediaRate = ((double) media.getWidth()) / media.getHeight();
                double rootRate = ((double) rootW) / rootH;
                if (mediaRate > rootRate) {
                    targetW = rootW;
                    targetH = (int) (targetW / mediaRate);
                    //  LogUtil.d(TAG, "target1 :: resolution - " + targetW + "x" + targetH);

                } else if (mediaRate < rootRate) {
                    targetH = rootH;
                    targetW = (int) (targetH * mediaRate);
                    //  LogUtil.d(TAG, "target1 :: resolution - " + targetW + "x" + targetH);
                }

                if (targetW > 0 && targetH > 0) {
                    ViewGroup.LayoutParams lps = rlVvPlayerBorder.getLayoutParams();
                    lps.width = targetW;
                    lps.height = targetH;
                    rlVvPlayerBorder.setLayoutParams(lps);
                }
            }
        });

    }

    @Override
    public void onBack() {
        super.onBack();
        LogUtil.i(TAG, "onBack");
        mVideoPlayPresent.release();
        activity.exitApp();
    }


}
