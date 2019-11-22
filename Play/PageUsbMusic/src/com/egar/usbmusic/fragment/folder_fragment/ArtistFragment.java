package com.egar.usbmusic.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.adapter.UsbArtistAdapter;
import com.egar.usbmusic.fragment.UsbMuiscFolderFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.ArtistsMode;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;

import java.util.List;

import juns.lib.media.bean.FilterMedia;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 20:21
 * @see {@link }
 * folder 二级页面
 */
public class ArtistFragment extends BaseLazyLoadFragment implements ArtistsMode.IArtistDataChange,
        AdapterView.OnItemClickListener, IndexTitleScrollView.OnIndexListener, ListView.OnScrollListener, CollectListener {
    private String TAG ="ArtistFragment";
    private MainActivity activity;
    private int page = 0;
    private ListView lv_artist;
    private IndexTitleScrollView indexTitleScrollView;
    private TextView tv_center_char;
    private UsbArtistAdapter usbArtistAdapter;
    private MusicPresent musicPresent;
    private List<ProAudio> audios;
    private List<FilterMedia> filterMedia;
    private ArtistsMode artmodel;
    private boolean mmIsTouchScrolling;
    private static Handler mHandler = new Handler();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void initView() {
        lv_artist = findViewById(R.id.lv_artist);
        indexTitleScrollView = findViewById(R.id.indexScroll);
        tv_center_char = findViewById(R.id.tv_center_char);
        usbArtistAdapter = new UsbArtistAdapter(activity);
        usbArtistAdapter.setCollectListener(this);
        lv_artist.setAdapter(usbArtistAdapter);
        musicPresent = MusicPresent.getInstance();
        artmodel = new ArtistsMode();
        artmodel.setArtistDataChangeListener(this);
        artmodel.loadFilters();
        lv_artist.setOnItemClickListener(this);
        lv_artist.setOnScrollListener(this);
        indexTitleScrollView.registIndexChanged(this);
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
    protected int getLayouId() {
        return R.layout.usb_music_artist_fragment;
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
    public void ArtistsDateChage(List<FilterMedia> filterFolders) {
        this.filterMedia = filterFolders;
        usbArtistAdapter.refreshData(filterFolders, musicPresent.getCurrMedia());
    }

    @Override
    public void ArtistsFileDataChange(List<ProAudio> audios) {
        this.audios = audios;
        usbArtistAdapter.refreshData(audios, musicPresent.getCurrMedia());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (usbArtistAdapter.getPage() == 0) {
            FilterMedia filter = (FilterMedia) (filterMedia.get(position));
            artmodel.loadMedias(filter.sortStr);
            usbArtistAdapter.setPage(1);
            page = 1;
            LogUtil.i(TAG, "forder");
        } else {
            LogUtil.i(TAG, "file");
            ProAudio media = audios.get(position);
            musicPresent.playMusic(media.getMediaUrl(), position);
            backPlayerPager();
        }
    }

    /**
     * 跳到播放页面
     */
    public void backPlayerPager() {
        ((UsbMuiscFolderFragment) getParentFragment()).BackUsbPlayerFragmet();
    }

    @Override
    public void onIndexChanged(int index, char c) {
        LogUtil.i(TAG,"onIndexChanged ="+c);
        tv_center_char.setVisibility(View.VISIBLE);
        tv_center_char.setText("" + c);
        int position = usbArtistAdapter.getPositionForSection(c);
        lv_artist.setSelection(position);
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
        int position = usbArtistAdapter.getPositionForSection(c);
        lv_artist.setSelection(position);
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
        int section = usbArtistAdapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        indexTitleScrollView.setIndex(firstVisibleChar);

    }


    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        int  item = usbArtistAdapter.getPage();
        if (item != 0) {
            ProAudio media = audios.get(pos);
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
}
