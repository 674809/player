package com.egar.mediaui;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.utils.VideoPreferUtils;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/6 13:47
 * @see {@link }
 */
public class MyVideoView extends SurfaceView {
    private static final String TAG = "MiGuAdVideoView";
    private boolean isReady = false;
    private int position = 0;//续播时间
    private String url = "";
    private MediaPlayer player;
    private boolean isDestory = false;
    // 进度计时器
    private Thread thread;

    private int currentPosition;

    public MyVideoView(Context context) {
        super(context);

    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        player = new MediaPlayer();

        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                LogUtil.d(TAG, "surfaceCreated");
                isReady = true;
                if(currentPosition >0){
                    LogUtil.d(TAG, "currentPosition >0");
                    player.setDisplay(surfaceHolder);
                    player.seekTo(currentPosition);
                  //  player.setDisplay(null);
//                  /  player.setDisplay(surfaceHolder);
                   // player.start();
                }else {
                    if (!"".equals(url) && !player.isPlaying()) {
                        try {
                            player.reset();
                            player.setDataSource(url);
                            player.prepare();
                            String[] mediaInfo = VideoPreferUtils.getLastPlayedMediaInfo();
                            int currentPosition = Integer.parseInt(mediaInfo[1]);
                            LogUtil.d(TAG, "续播时间 1：currentPosition" + currentPosition);
                            player.seekTo(currentPosition);
                            player.setDisplay(surfaceHolder);
                            player.start();
                            getCurrenProgress();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LogUtil.d(TAG, "续播时间 2：currentPosition" + currentPosition);

                    }
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                LogUtil.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                isReady = false;
                surfaceHolder = null;
                LogUtil.d(TAG, "surfaceDestroyed");
                if (!isDestory) {
                     player.setDisplay(null);
                    //player.setPlaybackParams();
                    // player.stop();
                }
            }
        });


    }


    public void getCurrenProgress() {
        LogUtil.i(TAG, "start getCurrenProgress");
       if(thread ==null){
           thread = new Thread(runnable);
           thread.start();
       }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (!isDestory) {
                    if (player != null) {
                        currentPosition = player.getCurrentPosition();
                        LogUtil.i(TAG, "保存时间 =" + currentPosition);
                       VideoPreferUtils.saveLastPlayedMediaInfo("", currentPosition);

                    }

                    sleep(500);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVideoPath(String url) {
        this.url = url;
        if (isReady) {
            try {
                player.reset();
                player.setDataSource(url);
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }


    public void start() {
        if (player != null && !player.isPlaying()) {
            player.start();

        }
    }

    public void seekTo(int startTime) {
        if (player != null && player.isPlaying()) {
            player.seekTo(startTime);
        }
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        if (player != null) {
            player.setOnPreparedListener(listener);
        }
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        if (player != null) {
            player.setOnCompletionListener(listener);
        }
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        if (player != null) {
            player.setOnErrorListener(listener);
        }
    }

    public void release() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            isDestory = true;
            thread = null;
            VideoPreferUtils.saveLastPlayedMediaInfo("", 0);
        }
    }

}
