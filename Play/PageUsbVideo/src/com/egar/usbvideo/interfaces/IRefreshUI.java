package com.egar.usbvideo.interfaces;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/14 16:52
 * @see {@link }
 */
public interface IRefreshUI {

    /**
     * 重置seekbar
     */
    void onResetSeekBar();

    /**
     * 更新播放状态
     *
     * @param state
     */
    void updatePlayStatus(int state);

    void onPlayStateChanged$Play();
    void  onPlayStateChanged$Prepared();

    void updateSeekTime(int progress, int duration);

    void onVideoProgressChanged(String s, int i, int i1);


    void onPlayModeChange();
}
