package com.egar.usbmusic.present;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.egar.mediaui.App;
import com.egar.mediaui.util.LogUtil;
import com.egar.music.api.AudioPlayRespFactory;
import com.egar.music.api.EgarApiMusic;
import com.egar.music.api.IApiAudioActions;
import com.egar.usbmusic.interfaces.IPlayerState;
import com.egar.usbmusic.utils.UdiskUtil;

import java.util.List;
import java.util.Map;

import juns.lib.android.utils.Logs;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.FilterType;
import juns.lib.media.flags.MediaCollectState;
import juns.lib.media.play.IAudioPlayListener;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/12 17:02
 * @see {@link }
 */
public class MusicPresent{
    private String TAG = "MuiscPresent";

    //音乐操作 api
    private EgarApiMusic musicApi;
    private IAudioPlayListener mAudioPlayListener;
    private Context mContext;
    private IPlayerState iPlayerState;
    private static MusicPresent mMusicPresent;
    private ApiAudioActions mApiAudioActions;
    private AllSongsLoadingTask mFilterLoadingTask;
    private static final int PLAY_STATE_CHANGE = 0;
    private static final int PLAY_PROGRESS_CHANGE = 1;
    private static final int PLAY_MODEL = 2;
    private int playMode = 0;
    ;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PLAY_STATE_CHANGE:
                    iPlayerState.playStateChange(msg.arg1);
                    LogUtil.i(TAG, "playStateChange=" + msg.arg1);
                    break;
                case PLAY_PROGRESS_CHANGE:
                    iPlayerState.playProgressChanged((String) msg.obj, msg.arg1, msg.arg2);
                    break;
                case PLAY_MODEL:
                    iPlayerState.playModeChange(msg.arg1);
                    break;
            }
        }
    };

    private MusicPresent() {

    }

    ;

    public static MusicPresent getInstance() {
        if (mMusicPresent == null) {
            mMusicPresent = new MusicPresent();
        }
        return mMusicPresent;
    }

    /**
     * 初始化EgarAPiMuisc
     *
     * @param context
     */
    public void creatMuiscService(Context context) {
        mContext = context;
        if (musicApi == null) {
            mApiAudioActions = new ApiAudioActions();
            musicApi = new EgarApiMusic(context, mApiAudioActions);
        }
    }



    public void focusPlayer() {
        if (musicApi != null) {
            try {
                musicApi.focusPlayer();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 判断是否播放
     *
     * @return
     */
    public boolean isPlaying() {
        return musicApi != null && musicApi.isPlaying();
    }


    /**
     * 绑定播放服务
     *
     * @param isConnected
     */
    public void bindPlayService(boolean isConnected) {
        if (isConnected) {
            musicApi.bindPlayService();
        } else {
            musicApi.unbindPlayService();
        }
    }

    public void addPlayListener(boolean isRespDelta, String tag, IAudioPlayListener l) {
        if (musicApi != null) {
            musicApi.addPlayListener(isRespDelta, tag, l);
        }
    }

    /**
     * 判断服务是否连接
     *
     * @return
     */
    public boolean isPlayServiceConnected() {
        if (musicApi != null) {
            return musicApi.isPlayServiceConnected();
        }
        return false;
    }

    /**
     * 获取当前歌曲
     *
     * @return
     */
    public ProAudio getCurrMedia() {
        if (musicApi != null) {
            return musicApi.getCurrMedia();
        }
        return null;
    }

    /**
     * 是否在扫描
     *
     * @return
     */
    public boolean isScanning() {
        return musicApi != null && musicApi.isScanning();
    }

    /**
     * 获取播放模式
     *
     * @return
     */
    public int getPlayMode() {
        if (musicApi != null) {
            return musicApi.getPlayMode();
        }
        return 0;
    }

    public void release(){
        if(musicApi !=null){
            musicApi.playOrPauseByUser();
            musicApi.release();
            mMusicPresent = null;

        }
    }

    /**
     * mode SINGLE(1);RANDOM(2);LOOP(3);ORDER(4)
     */
    private void setPlayMode(int model) {
        if (musicApi != null) {
            musicApi.setPlayMode(model);
        }
    }

    public void switchPlayMode() {
        if (playMode == 0) {
            setPlayMode(1);
            playMode += 1;
        } else if (playMode == 1) {
            setPlayMode(2);
            playMode += 1;
        } else if (playMode == 2) {
            setPlayMode(3);
            playMode = 0;
        }
    }

    /**
     * 播放上一首
     */
    public void playPrevByUser() {
        if (musicApi != null) {
            musicApi.playPrevByUser();
        }
    }

    /**
     * 播放下一首
     */
    public void playNextByUser() {
        if (musicApi != null) {
            musicApi.playNextByUser();
        }
    }

    /**
     * 播放暂停
     */
    public void playOrPauseByUser() {
        if (musicApi != null) {
            musicApi.playOrPauseByUser();
        }
    }

    /**
     * 获取存储列表设备
     *
     * @return
     */
    public List getStorageDevices() {
        if (musicApi != null) {
            return musicApi.getStorageDevices();
        }
        return null;
    }

    /**
     * playByUrlByUser
     * 指定播放
     */
    public void playByUrlByUser(String path) {
        if (musicApi != null) {
            musicApi.playByUrlByUser(path);
        }

    }

    /**
     * 自动播放
     *
     * @return
     */
    public void autoPlay() {
        if (musicApi != null) {
            musicApi.autoPlay();
        }
    }

    /**
     * 获取所有媒体信息
     *
     * @param mapColumns
     * @param sortOrder
     * @return
     */
    public List getMediasByColumns(Map<String, String> mapColumns, String sortOrder) {
        if (musicApi != null) {
            return musicApi.getMediasByColumns(mapColumns, sortOrder);
        }
        return null;
    }

    /**
     * 设置状态监听
     *
     * @param linsteren
     */
    public void setIPlayerStateLinsteren(IPlayerState linsteren) {
        this.iPlayerState = linsteren;
    }

    /**
     * 获取歌曲信息
     *
     * @param sortBy
     * @param params
     * @return
     */
    public List getAllMedias(int sortBy, String[] params) {
        if (musicApi != null) {
            return musicApi.getAllMedias(sortBy, params);
        }
        return null;
    }

    /**
     * Update play list.
     *
     * @param params
     */
    public void applyPlayList(String[] params) {
        musicApi.applyPlayList(params);
    }

    /**
     * Update play information.
     *
     * @param mediaUrl
     * @param pos
     */
    public void applyPlayInfo(String mediaUrl, int pos) {
        if (musicApi != null) {
            musicApi.applyPlayInfo(mediaUrl, pos);
        }
    }

    /**
     * Get current media path who is playing or ready to play.
     *
     * @return
     */
    public String getCurrMediaPath() {
        if (musicApi != null) {
            return musicApi.getCurrMediaPath();
        }
        return null;
    }

    /**
     * Get current position in play list
     * 收藏 取消
     */

    public void collect(ProAudio media) {
        if (media != null) {
            switch (media.getCollected()) {
                case MediaCollectState.COLLECTED:
                    media.setCollected(0);
                    break;
                case MediaCollectState.UN_COLLECTED:
                    media.setCollected(1);
                    break;
            }
            int posToCollect = getCurrPos();
            int res = updateMediaCollect(posToCollect, media);
        }
    }

    /**
     * 播放指定路径歌曲
     *
     * @param mediaUrl
     * @param position
     */
    public void playMusic(String mediaUrl, int position) {
        Log.i(TAG, "playAndOpenPlayerActivity(" + mediaUrl + "," + position + ")");
        // Apply play information.
        applyPlayInfo(mediaUrl, position);
        // Check if already playing.
        if (isPlayingSameMedia(mediaUrl)) {
            Log.i(TAG, "### The media to play is already playing now. ###");
        } else {
            Logs.i("TIME_COL", "-3-" + System.currentTimeMillis());
            playByUrlByUser(mediaUrl);
        }
    }

    /**
     * Get current position in play list
     *
     * @return
     */
    public int getCurrPos() {
        if (musicApi != null) {
            return musicApi.getCurrPos();
        }
        return 0;
    }

    /**
     * Update collect state.
     *
     * @param position
     * @param media
     * @return
     */
    public int updateMediaCollect(int position, ProAudio media) {
        if (musicApi != null) {
            return musicApi.updateMediaCollect(position, media);
        }
        return 0;
    }

    /**
     * 获取文件
     *
     * @return
     */
    public List getFilterFolders() {
        if (musicApi != null) {
            LogUtil.i(TAG, "getFilterFolders");
            return musicApi.getFilterFolders();
        }
        LogUtil.i(TAG, "getFilterFolders  is null");
        return null;
    }

    /**
     * 判断要播放的歌曲，和当前播放的歌曲是否是同一首
     *
     * @param mediaUrl
     * @return
     */
    public boolean isPlayingSameMedia(String mediaUrl) {
        boolean isPlayingSameMedia = musicApi.isPlaying() && TextUtils.equals(getCurrMediaPath(), mediaUrl);
        Log.i(TAG, "isPlayingSameMedia : " + isPlayingSameMedia);
        return isPlayingSameMedia;
    }

    /**
     * Get current duration.
     *
     * @return
     */
    public long getDuration() {
        if (musicApi != null) {
            return musicApi.getDuration();
        }
        return 0;
    }

    public void seekTo(int time) {
        if (musicApi != null) {
            musicApi.seekTo(time);
        }
    }

    /**
     * Get current progress.
     *
     * @return
     */
    public long getProgress() {
        if (musicApi != null) {
            return musicApi.getProgress();
        }
        return 0;
    }

    //获取歌手文件夹
    public List getFilterArtists() {
        if (musicApi != null) {
            return musicApi.getFilterArtists();
        }
        return null;
    }

    public List getFilterAlbums() {
        if (musicApi != null) {
            return musicApi.getFilterAlbums();
        }
        return null;
    }

    public int clearHistoryCollect() {
        if (musicApi != null) {
            return musicApi.clearHistoryCollect();
        }
        return 0;
    }

    //--------------------------------------ApiAudioActions-clasee-----------------------------------//
    public class ApiAudioActions implements IApiAudioActions {

        @Override
        public IBinder asBinder() {
            return null;
        }
        /**
         * 当MusicPlayService绑定成功
         */
        @Override
        public void onAudioPlayServiceConnected() {
            Log.i(TAG, "onAudioPlayServiceConnected()");
            if (musicApi != null) {
                mAudioPlayListener = new AudioPlayRespFactory(new ApiAudioActions()).getRespCallback();
                addPlayListener(true, "MUSIC_PLAYER", mAudioPlayListener);
            }
            if (isScanning()) {
                LogUtil.i(TAG, "isScanning =" + isScanning());
            }
            focusPlayer();
            int playMode = getPlayMode();
            Log.i(TAG, "getPlayMode =" + getPlayMode());
            ProAudio media = getCurrMedia();
            if (media != null) {
                if (!isPlaying()) {
                    playOrPauseByUser();
                    Log.i(TAG, "playOrPauseByUser");
                }
            } else {
                loadAllSongs();
            }


        }

        @Override
        public void onAudioPlayServiceDisconnected() {
            musicApi.removePlayListener("MUSIC_PLAYER", mAudioPlayListener);
        }

        /**
         * 上报挂载状态发生改变的设备
         */
        @Override
        public void onMountStateChanged(List list) throws RemoteException {
            LogUtil.i(TAG,"onMountStateChanged");

        }

        /**
         * MediaScanService - 扫描状态
         * <p>1 START</p>
         * <p>2 REFRESHING</p>
         * <p>3 END</p>
         */
        @Override
        public void onScanStateChanged(int i) throws RemoteException {

        }

        @Override
        public void onGotDeltaMedias(List list) throws RemoteException {

        }

        /**
         * playStateValue - 通知播放器状态
         *
         * @param
         */
        @Override
        public void onPlayStateChanged(int i) {
            LogUtil.i(TAG, "onPlayStateChanged =" + i);
            if (UdiskUtil.isHasSupperUDisk(App.getContext())) {
                handler.sendMessage(handler.obtainMessage(PLAY_STATE_CHANGE, i, 0));
            }
        }

        /**
         * PlayProgress - 进度改变回调
         *
         * @param mediaPath 正在播放的媒体路径
         * @param progress  当前进度
         * @param duration  总时长
         */
        @Override
        public void onPlayProgressChanged(String mediaPath, int progress, int duration) throws RemoteException {

            handler.sendMessage(handler.obtainMessage(PLAY_PROGRESS_CHANGE, progress, duration, mediaPath));

        }

        //循环模式
        @Override
        public void onPlayModeChanged(int mode) {
            // LogUtil.i(TAG, "循环模式 = " +mode);
            handler.sendMessage(handler.obtainMessage(PLAY_MODEL, mode, 0, 0));
        }
    }
    public void destory(){

    }

    public void loadAllSongs() {
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new AllSongsLoadingTask();
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 第一次获取歌曲数据
     */

    private class AllSongsLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {


        AllSongsLoadingTask() {
            LogUtil.i(TAG, "AllSongsLoadingTask");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {
            try {
                // Get play list from MusicPlayService.
                List<ProAudio> list = getAllMedias(FilterType.MEDIA_NAME, null);
                // Update play list of MusicPlayService.
                applyPlayList(null);
                return list;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            try {
                LogUtil.i(TAG, "postExecute= " + audios.get(0).getTitle());
                autoPlay();
            } catch (Exception e) {
                Log.i(TAG, "");
            }
        }
    }
}
