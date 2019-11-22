package com.egar.usbmusic.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egar.mediaui.App;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.util.date.DateFormatUtil;
import com.egar.mediaui.view.MyButton;
import com.egar.usbmusic.interfaces.IPlayerState;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.utils.AudioUtils;
import com.egar.usbmusic.utils.FragUtil;
import com.egar.usbmusic.utils.UdiskUtil;
import com.egar.usbvideo.view.SeekBarImpl;

import java.util.List;

import juns.lib.android.utils.Logs;
import juns.lib.java.utils.EmptyUtil;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.PlayMode;
import juns.lib.media.flags.PlayState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/11 10:20
 * 音乐播放页面
 * @see {@link }
 */
public class UsbMusicPlayerFragment extends BaseUsbScrollLimitFragment implements View.OnClickListener,
        IPlayerState {
    private String TAG = getClass().getSimpleName();
    private UsbMusicMainFragment fragment;
    private MainActivity mAttachedActivity;
    private MusicPresent mMuiscPresent;
    private ImageView mbt_prev, mbt_palyOrPause, mbt_next;

    private MyButton mbt_loop, mbt_folder;
    private TextView mtv_name, mtv_songer, tv_start_time, tv_end_time;
    private SeekBarImpl seekbar;
    private ImageView img_phone;
    private SeekBarOnChange mSeekBarOnChange;
    private LinearLayout lay_udisk, lay_play;
    private final static int UPDATE_SEEKBAR = 1;
    private final static int PLAY_MODE = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_SEEKBAR:
                    int id = msg.arg1;
                    int progress = msg.arg2;
                    int duration = (int) msg.obj;
                    LogUtil.i(TAG, "progress =" + progress);
                    LogUtil.i(TAG, "duration =" + duration);
                    seekbar.setMax(duration);
                    refreshFrameInfo(id, progress, duration);
                    break;
                case PLAY_MODE:
                    updateLoopUI();
                    break;
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachedActivity = (MainActivity) context;
    }

    @Override
    public int getPageIdx() {
        return Configs.PAGE_USB_MUSIC_PLAY;
    }

    @Override
    public void onWindowChangeFull() {
        img_phone.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWindowChangeHalf() {
        img_phone.setVisibility(View.GONE);
    }

    @Override
    public void initView() {
        super.initView();
        fragment = (UsbMusicMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_USB_MUSIC_PLAY);
        findView();

    }

    private void findView() {
        mbt_prev = findViewById(R.id.bt_prev);
        mbt_folder = findViewById(R.id.bt_folder);
        mbt_palyOrPause = findViewById(R.id.bt_palyOrPause);
        mbt_next = findViewById(R.id.bt_next);
        mbt_loop = findViewById(R.id.bt_loop);
        mtv_name = findViewById(R.id.tv_name);
        mtv_songer = findViewById(R.id.tv_songer);
        seekbar = findViewById(R.id.seekbar);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        img_phone = findViewById(R.id.img_phone);
        lay_udisk = findViewById(R.id.lay_udisk);
        lay_play = findViewById(R.id.lay_play);

        mbt_prev.setOnClickListener(this);
        mbt_palyOrPause.setOnClickListener(this);
        mbt_next.setOnClickListener(this);
        mbt_loop.setOnClickListener(this);
        mbt_folder.setOnClickListener(this);
        mSeekBarOnChange = new SeekBarOnChange();
        seekbar.setOnSeekBarChangeListener(mSeekBarOnChange);

        updataUdisPage(UdiskUtil.isHasSupperUDisk(App.getContext()));
    }

    /**
     * 更新U盘未挂载页面是否显示
     */
    public void updataUdisPage(boolean ismount) {
        if (!ismount) {
            lay_udisk.setVisibility(View.VISIBLE);
            lay_play.setVisibility(View.GONE);
        } else {
            lay_udisk.setVisibility(View.GONE);
            lay_play.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayouId() {
        super.getLayouId();
        return R.layout.usb_music_frag_player;
    }

    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        LogUtil.w("init  player");
        if(UdiskUtil.isHasSupperUDisk(App.getContext())){
            initMusicplayer();
            refreshFrameInfo(0, 0, 0);
        }


    }

    @Override
    public void onPageResume() {
        super.onPageResume();
        updataUdisPage(UdiskUtil.isHasSupperUDisk(App.getContext()));
    }

    /**
     * 初始化化歌曲操作数据
     */
    private void initMusicplayer() {
        if (mMuiscPresent == null) {
            mMuiscPresent = MusicPresent.getInstance();
            mMuiscPresent.setIPlayerStateLinsteren(this);
            mMuiscPresent.focusPlayer();
            mMuiscPresent.bindPlayService(true);

        }
        if (mMuiscPresent.isPlayServiceConnected() && !mMuiscPresent.isPlaying()) {
            mMuiscPresent.playOrPauseByUser();
        }
        setMuiscInfo();
    }


    /**
     * 设置歌曲信息
     */
    public void setMuiscInfo() {
        ProAudio media = mMuiscPresent.getCurrMedia();
        if (media != null) {
            String phonePath = media.getCoverUrl();
            seekbar.setMax((int) media.getDuration());
            LogUtil.d(TAG, "phonePath =");
            if (phonePath != null) {
                Bitmap bitmap = FragUtil.getLoacalBitmap(phonePath);
                img_phone.setImageBitmap(bitmap);
            } else {
                img_phone.setImageResource(R.drawable.album_bg_em);
            }
        }
        if (media != null) {
            StringBuilder sbInfo = new StringBuilder();
            String strTitle = AudioUtils.getMediaTitle(mAttachedActivity, -1, media, true);
            if (ProAudio.UNKNOWN.equals(strTitle)) {
                sbInfo.append(getString(R.string.unknown_title));
            } else {
                sbInfo.append(strTitle);
            }
            //Artist
            String artist = media.getArtist();
            if (ProAudio.UNKNOWN.equals(artist) || EmptyUtil.isEmpty(artist)) {
                sbInfo.append("::").append(getString(R.string.unknow));
            } else {
                sbInfo.append("::").append(artist);
            }
            //Album
            String strAlbum = media.getAlbum();
            if (ProAudio.UNKNOWN.equals(strAlbum) || EmptyUtil.isEmpty(strAlbum)) {
                sbInfo.append("::").append(getString(R.string.unknow));
            } else {
                sbInfo.append("::").append(strAlbum);
            }
            mtv_name.setText(sbInfo);
        } else {
            LogUtil.i(TAG, "media =" + media);
        }
        updateLoopUI();
        updatePlayOrPause();
    }

    public void updateLoopUI() {
        ProAudio media = mMuiscPresent.getCurrMedia();
        if (media != null) {
            int playmodel = mMuiscPresent.getPlayMode();
            switch (playmodel) {
                case PlayMode.LOOP:
                    mbt_loop.setImageResource(R.drawable.usb_loop);
                    break;
                case PlayMode.SINGLE:
                    mbt_loop.setImageResource(R.drawable.repeat_ico);
                    break;
                case PlayMode.RANDOM:
                    mbt_loop.setImageResource(R.drawable.random_ico);
                    break;
            }
        }
    }

    public void updatePlayOrPause() {
        if (mMuiscPresent.isPlaying()) {
            mbt_palyOrPause.setImageResource(R.drawable.pause_ico);
        } else {
            mbt_palyOrPause.setImageResource(R.drawable.play_ico);
        }
    }


    public void onUdiskStateChange(int state) {
        boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
        LogUtil.d(TAG, "onUdiskStateChange =" + ismount);
        Toast.makeText(App.getContext(), "state =" + state, Toast.LENGTH_SHORT).show();

        if (ismount) {//挂载
            updataUdisPage(ismount);
            initMusicplayer();
        } else { //卸载
            updataUdisPage(ismount);
            mMuiscPresent.release();
        }
    }

    private final class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress;
        boolean mmIsTracking = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //     LogUtil.i(TAG, "SeekBarOnChange - onStartTrackingTouch");
            mmIsTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //      LogUtil.i(TAG, "SeekBarOnChange - onStopTrackingTouch");
            if (mmIsTracking) {
                mmIsTracking = false;
                mMuiscPresent.seekTo(mmProgress);

                //Refresh UI
                tv_start_time.setText(DateFormatUtil.getFormatHHmmss(mmProgress));
                tv_end_time.setText(DateFormatUtil.getFormatHHmmss(seekBar.getMax()));
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //     LogUtil.d(TAG, "SeekBarOnChange - onProgressChanged(SeekBar," + progress + "," + fromUser + ")");
            if (fromUser) {
                mmProgress = progress;
            }
        }

        boolean isTrackingTouch() {
            return mmIsTracking;
        }

    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        ((BaseUsbFragment) MainPresent.getInstatnce().getCurrenFragmen(Configs.PAGE_INDX_USB)).removeMediaBtnClick();
        LogUtil.w("onPageLoadStop player");

    }


    /**
     * 从USBMuisc切出去时停止播放
     */
    public void stopPlayer() {
     /*   if (mMuiscPresent !=null){
            if (mMuiscPresent.isPlaying()) {
                mMuiscPresent.playOrPauseByUser();
            }
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_folder:
                fragment.setUsbPlayerCurrentItem(1);
                break;
            case R.id.bt_prev:
                MusicPresent.getInstance().playPrevByUser();
                break;
            case R.id.bt_next:
                MusicPresent.getInstance().playNextByUser();
                break;
            case R.id.bt_palyOrPause:
                MusicPresent.getInstance().playOrPauseByUser();
                break;
            case R.id.bt_loop:
                MusicPresent.getInstance().switchPlayMode();
                break;
        }
    }

    @Override
    public void playStateChange(int state) {
        LogUtil.i(TAG, "playStateChange=" + state);
        // refreshFrameInfo(0, 0, duration);
        if (isAdded()) {
            setMuiscInfo();
            updateLoopUI();
        }
        if (state == PlayState.PLAY) {
            mbt_palyOrPause.setImageResource(R.drawable.pause_ico);
        } else {
            mbt_palyOrPause.setImageResource(R.drawable.play_ico);
        }
        if (PlayState.ERROR == state) {

        }
    }

    @Override
    public void playProgressChanged(String path, int progress, int duration) {
        handler.sendMessage(handler.obtainMessage(UPDATE_SEEKBAR, 2, progress, duration));
    }

    @Override
    public void playModeChange(int mode) {
        LogUtil.i(TAG, "play mode =" + mode);
        handler.sendEmptyMessage(PLAY_MODE);

    }

    @Override
    public void scanStateChanged(int i) {

    }

    @Override
    public void MountStateChanged(List list) {

    }

    @Override
    public void onBack() {
        super.onBack();
        mAttachedActivity.exitApp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMuiscPresent != null) {
            mMuiscPresent.destory();
        }

    }

    private void refreshFrameInfo(int flag, int paramProgress, int paramDuration) {
        switch (flag) {
            //Set on player prepared
            case 0:
                LogUtil.i(TAG, "refreshFrameInfo[0] - Reset.");
                //Duration
                int duration = (int) mMuiscPresent.getDuration();
                String endtime = DateFormatUtil.getFormatHHmmss(duration);
                LogUtil.i(TAG, "EndTime=" + endtime);
                tv_end_time.setText(endtime);
                seekbar.setMax(duration);

                //Progress
                seekbar.setProgress(0);
                tv_start_time.setText(DateFormatUtil.getFormatHHmmss(0));
                break;

            //Refresh current progress/duration
            case 1:
                boolean isPlaying = mMuiscPresent.isPlaying();
                if (isPlaying) {
                    Log.i(TAG, "refreshSeekBar[1] - audio is playing now.");
                    //Duration
                    // duration = (int) mMuiscPresent.getDuration();
                    ProAudio mediaWith = mMuiscPresent.getCurrMedia();
                    long durations = mediaWith.getDuration();
                    seekbar.setMax((int) durations);
                    tv_end_time.setText(DateFormatUtil.getFormatHHmmss(durations));
                    //Progress
                    int currProgress = (int) mMuiscPresent.getProgress();
                    if (currProgress <= durations) {
                        seekbar.setProgress(currProgress);
                        tv_start_time.setText(DateFormatUtil.getFormatHHmmss(currProgress));
                    } else {
                        seekbar.setProgress((int) durations);
                        tv_start_time.setText(DateFormatUtil.getFormatHHmmss(durations));
                    }
                } else {
                    Log.i(TAG, "refreshSeekBar[1] - audio is paused now.");
                    ProAudio currMedia = mMuiscPresent.getCurrMedia();
                    if (currMedia == null) {
                        //Duration
                        seekbar.setMax(0);
                        tv_end_time.setText(DateFormatUtil.getFormatHHmmss(0));
                        //Progress
                        seekbar.setProgress(0);
                        tv_start_time.setText(DateFormatUtil.getFormatHHmmss(0));
                    } else {
                        //Duration
                        duration = (int) (currMedia.getDuration() > 0 ? currMedia.getDuration() : mMuiscPresent.getDuration());
                        seekbar.setMax(duration);
                        tv_end_time.setText(DateFormatUtil.getFormatHHmmss(duration));
                        //Progress
                        int currProgress = seekbar.getProgress();
                        if (currProgress <= duration) {
                            seekbar.setProgress(currProgress);
                            tv_start_time.setText(DateFormatUtil.getFormatHHmmss(currProgress));
                        } else {
                            seekbar.setProgress(duration);
                            tv_start_time.setText(DateFormatUtil.getFormatHHmmss(duration));
                        }
                    }
                }
                break;

            case 2:
                //Set SeekBar-Progress
                //   if (!mSeekBarOnChange.isTrackingTouch()) {
                Logs.debugI(TAG, "refreshSeekBar[2] - update progress.");
                //Duration
                if (paramProgress > seekbar.getMax()) {
                    paramDuration = paramProgress;
                }
                seekbar.setMax(paramDuration);
                tv_end_time.setText(DateFormatUtil.getFormatHHmmss(paramDuration));
                //Progress
                seekbar.setProgress(paramProgress);
                tv_start_time.setText(DateFormatUtil.getFormatHHmmss(paramProgress));
                //   }
                break;
        }
    }


}
