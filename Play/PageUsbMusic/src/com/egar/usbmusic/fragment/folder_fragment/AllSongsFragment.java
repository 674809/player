package com.egar.usbmusic.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.adapter.UsbAllsongsAdapter;
import com.egar.usbmusic.fragment.UsbMusicMainFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.AllSongsModel;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;

import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 20:21
 * @see {@link }
 * folder 二级页面
 */
public class AllSongsFragment extends BaseLazyLoadFragment implements IndexTitleScrollView.OnIndexListener,
        AllSongsModel.IAllSongsDataChange, AdapterView.OnItemClickListener, ListView.OnScrollListener , CollectListener {
    private String TAG = "AllSongsFragment";
    private MainActivity mActivity;
    private ListView mlv_allsongs;
    private UsbAllsongsAdapter allsongsAdapter;
    private List<ProAudio> mListData;

    private UsbMusicMainFragment fragment;
    private MusicPresent musicPresent;
    private int page = 0;
    private IndexTitleScrollView letterSidebar;
    private TextView tv_center_char;
    private AllSongsModel allSongsModel;
    private boolean mmIsTouchScrolling;
    private static Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
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
        fragment = (UsbMusicMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_USB_MUSIC_PLAY);
        LogUtil.i("initview all songs");
        musicPresent = MusicPresent.getInstance();
        mlv_allsongs = findViewById(R.id.lv_allsongs);
        tv_center_char = findViewById(R.id.tv_center_char);
        letterSidebar = (IndexTitleScrollView) findViewById(R.id.lsb);

        allsongsAdapter = new UsbAllsongsAdapter(mActivity);
        mlv_allsongs.setAdapter(allsongsAdapter);
        mlv_allsongs.setOnScrollListener(this);
        mlv_allsongs.setOnItemClickListener(this);
        allsongsAdapter.setCollectListener(this);
        letterSidebar.registIndexChanged(this);

        allSongsModel = new AllSongsModel();
        allSongsModel.setAllSongDataChangeListener(this);
        allSongsModel.loadFilters();
      //  mlv_allsongs.setOnScrollListener(new LvOnScroll());

    }

    @Override
    protected int getLayouId() {
        return R.layout.usb_music_all_songs_fragment;
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
    public void onIndexChanged(int index, char c) {
        tv_center_char.setVisibility(View.VISIBLE);
        tv_center_char.setText("" + c);
        int position = allsongsAdapter.getPositionForSection(c);
        mlv_allsongs.setSelection(position);

    }

    @Override
    public void onStopChanged(int index, char c) {
        tv_center_char.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        tv_center_char.setVisibility(View.VISIBLE);
        tv_center_char.setText("" + c);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_center_char.setVisibility(View.INVISIBLE);
            }
        }, 800);
        int position = allsongsAdapter.getPositionForSection(c);
        mlv_allsongs.setSelection(position);
    }


    @Override
    public void AllSongsDateChage(List<ProAudio> list) {
        LogUtil.i(TAG, "list size=" + list.size());
        if (list != null) {
            mListData = list;
            allsongsAdapter.refreshData(list, musicPresent.getCurrMedia());
            scrollToPlayingPos(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        execItemClick(position);
    }

    private void execItemClick(int position) {
        ProAudio itemMedia = (ProAudio) allsongsAdapter.getItemInfo(position);
        if (itemMedia == null) {
            return;
        }
        //
        musicPresent.playMusic(itemMedia.getMediaUrl(), position);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_TOUCH_SCROLL:
                mmIsTouchScrolling = true;
                Log.i(TAG, "LvOnScroll -SCROLL_STATE_TOUCH_SCROLL-");
                break;
            case SCROLL_STATE_IDLE:
                mmIsTouchScrolling = false;
                Log.i(TAG, "LvOnScroll -SCROLL_STATE_IDLE-");
                break;
            case SCROLL_STATE_FLING:
                Log.i(TAG, "LvOnScroll -SCROLL_STATE_FLING-");
                break;

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int section = allsongsAdapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        letterSidebar.setIndex(firstVisibleChar);
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        if (mListData.size() != 0) {
            ProAudio media = mListData.get(pos);
            switch (media.getCollected()) {
                case MediaCollectState.UN_COLLECTED:
                    media.setCollected(MediaCollectState.COLLECTED);
                    media.setUpdateTime(System.currentTimeMillis());
                    musicPresent.updateMediaCollect(pos, media);
                    ivCollect.setImageResource(R.drawable.favor_c);
                    //Clear history collect
                    musicPresent.clearHistoryCollect();
                    break;
                case MediaCollectState.COLLECTED:
                    media.setCollected(MediaCollectState.UN_COLLECTED);
                    media.setUpdateTime(System.currentTimeMillis());
                    musicPresent.updateMediaCollect(pos, media);
                    ivCollect.setImageResource(R.drawable.favor_c_n);
                    break;
            }
        }
    }


    /**
     * 显示播放的歌曲
     * @param isWaitLoading
     */
    public void scrollToPlayingPos(final boolean isWaitLoading) {
        mHandler.removeCallbacksAndMessages(null);
        if (isWaitLoading) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToPlayingPos(false);
                }
            }, 300);
        } else {
            try {
                //
                int currPos = musicPresent.getCurrPos();
                int firstPosOfPage = getPageFirstPos(currPos);
                mlv_allsongs.setSelection(firstPosOfPage);
                //
                ProAudio firstMediaOfCurrPage = mListData.get(firstPosOfPage);
                char c = firstMediaOfCurrPage.getTitlePinYin().charAt(0);
                letterSidebar.setIndex(c);
                allsongsAdapter.setSelectItem(currPos);
            } catch (Exception e) {
                Log.i(TAG, "refreshHLLetterOfSideBar() >> e:" + e.getMessage());
            }
        }
    }
}
