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
import com.egar.usbmusic.adapter.UsbAlbumsAdapter;
import com.egar.usbmusic.fragment.UsbMuiscFolderFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.AlbumsModel;
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
public class AlbumsFragment extends BaseLazyLoadFragment implements ListView.OnItemClickListener,
        AlbumsModel.IAlbumsDataChange ,ListView.OnScrollListener,IndexTitleScrollView.OnIndexListener,
        CollectListener {

    private String TAG ="AlbumsFragment";
    private MainActivity activity;
    private int page = 0;
    private ListView lv_albums;
    private IndexTitleScrollView indexTitleScrollView;
    private MusicPresent musicPresent;
    private List<ProAudio> audios;
    private List<FilterMedia> filterMedia;
    private UsbAlbumsAdapter albumsAdapter;
    private AlbumsModel albumsModel;
    private TextView tv_center_char;
    private boolean mmIsTouchScrolling;
    private static Handler mHandler = new Handler();
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
        lv_albums = findViewById(R.id.lv_artist);
        indexTitleScrollView = findViewById(R.id.indexScroll);
        indexTitleScrollView.registIndexChanged(this);
        tv_center_char = findViewById(R.id.tv_center_char);
        musicPresent = MusicPresent.getInstance();

        albumsAdapter = new UsbAlbumsAdapter(activity);
        lv_albums.setAdapter(albumsAdapter);
        albumsModel = new AlbumsModel();
        albumsModel.setAlbumDataChangeListener(this);
        albumsModel.loadFilters();

        lv_albums.setOnItemClickListener(this);
        lv_albums.setOnScrollListener(this);
        albumsAdapter.setCollectListener(this);
    }

    @Override
    protected int getLayouId() {
        return R.layout.usb_music_albums_fragment;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (albumsAdapter.getPage() == 0) {
            FilterMedia filter = (FilterMedia) (filterMedia.get(position));
            albumsModel.loadMedias(filter.sortStr);
            albumsAdapter.setPage(1);
            page = 1;
            LogUtil.i(TAG, "forder");
        } else {
            LogUtil.i(TAG, "file");
            ProAudio media = audios.get(position);
            musicPresent.playMusic(media.getMediaUrl(), position);
            backPlayerPager();
        }
    }

    @Override
    public void AlbumsFolderDateChage(List<FilterMedia> filterFolders) {
        LogUtil.i(TAG,"filterFolders size="+filterFolders.size());
        this.filterMedia = filterFolders;
        albumsAdapter.refreshData(filterFolders, musicPresent.getCurrMedia());
    }

    @Override
    public void AlbumsFileDataChange(List<ProAudio> audios) {
        this.audios = audios;
        albumsAdapter.refreshData(audios, musicPresent.getCurrMedia());
    }

    /**
     * 跳到播放页面
     */
    public void backPlayerPager() {
        ((UsbMuiscFolderFragment) getParentFragment()).BackUsbPlayerFragmet();
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
        int section = albumsAdapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        indexTitleScrollView.setIndex(firstVisibleChar);
    }

    @Override
    public void onIndexChanged(int index, char c) {
        LogUtil.i(TAG,"onIndexChanged ="+c);
        tv_center_char.setVisibility(View.VISIBLE);
        tv_center_char.setText("" + c);
        int position = albumsAdapter.getPositionForSection(c);
        lv_albums.setSelection(position);
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
        int position = albumsAdapter.getPositionForSection(c);
        lv_albums.setSelection(position);
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        int  item = albumsAdapter.getPage();
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
