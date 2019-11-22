package com.egar.usbmusic.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
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
import com.egar.usbmusic.adapter.UsbFavoriteAdapter;
import com.egar.usbmusic.fragment.UsbMusicMainFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.FavoritesModel;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;

import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 19:59
 * @see {@link }
 */
public class FavoritesFragment extends BaseLazyLoadFragment implements AdapterView.OnItemClickListener,
        FavoritesModel.IFavoritesDataChange, IndexTitleScrollView.OnIndexListener,ListView.OnScrollListener, CollectListener {
    private String TAG = "FavoritesFragment";
    private ListView listView;
    private UsbFavoriteAdapter adapter;
    private MainActivity activity;
    private MusicPresent musicPresent;
    private UsbMusicMainFragment fragment;
    private List<ProAudio> mListData;
    //Task for loading medias.
    private IndexTitleScrollView letterSidebar;
    private TextView tv_center_toast, tv_center_char;
    private FavoritesModel favoritesModel;
    private static Handler mHandler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public int getPageIdx() {
        return 0;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    public void initView() {
        LogUtil.i("init favorite");
        fragment = (UsbMusicMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_USB_MUSIC_PLAY);
        musicPresent = MusicPresent.getInstance();
        listView = (ListView) findViewById(R.id.listView);
        tv_center_toast = findViewById(R.id.tv_center_toast);
        tv_center_char = findViewById(R.id.tv_center_char);
        letterSidebar = (IndexTitleScrollView) findViewById(R.id.lsb);
        letterSidebar.registIndexChanged(this);


        adapter = new UsbFavoriteAdapter(activity);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        favoritesModel = new FavoritesModel();
        favoritesModel.setFavoriteDataChangeListener(this);
        favoritesModel.loadFilters();
        adapter.setCollectListener(this);
    }

    @Override
    protected int getLayouId() {
        return R.layout.usb_music_favorite_fragment;
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
        LogUtil.i(TAG, "position=" + position);
        execItemClick(position);
        //跳转到播放页面
        fragment.setUsbPlayerCurrentItem(0);
    }

    private void execItemClick(int position) {
        ProAudio itemMedia = (ProAudio) adapter.getItemInfo(position);
        if (itemMedia == null) {
            return;
        }
        musicPresent.playMusic(itemMedia.getMediaUrl(), position);

    }

    @Override
    public void FavoritesDateChage(List<ProAudio> list) {
        LogUtil.i(TAG, "list =" + list.size());
        if (list.size() == 0) {
            tv_center_toast.setVisibility(View.VISIBLE);
        } else {
            tv_center_toast.setVisibility(View.GONE);
        }
        mListData = list;
        adapter.refreshData(mListData, musicPresent.getCurrMedia());
    }

    @Override
    public void onIndexChanged(int index, char c) {
        LogUtil.i(TAG,"onIndexChanged");
        if(mListData.size() == 0){
            tv_center_char.setVisibility(View.GONE);
        }else {
            tv_center_char.setVisibility(View.VISIBLE);
        }
        tv_center_char.setText("" + c);
        int position = adapter.getPositionForSection(c);
        listView.setSelection(position);
    }

    @Override
    public void onStopChanged(int index, char c) {
        tv_center_char.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        LogUtil.i(TAG,"onClickChar");
        if(mListData.size() == 0){
            tv_center_char.setVisibility(View.GONE);
        }else {
            tv_center_char.setVisibility(View.VISIBLE);
        }
        tv_center_char.setText("" + c);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_center_char.setVisibility(View.INVISIBLE);
            }
        }, 800);
        int position = adapter.getPositionForSection(c);
        listView.setSelection(position);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int section = adapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        letterSidebar.setIndex(firstVisibleChar);
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        LogUtil.d(TAG,"onClickCollectBtn "+pos);
        ProAudio item = (ProAudio) adapter.getItemInfo(pos);
        if (item == null) {
            return;
        }
        if (item.getCollected() == MediaCollectState.COLLECTED) {
            item.setCollected(MediaCollectState.UN_COLLECTED);
            item.setUpdateTime(System.currentTimeMillis());
            musicPresent.updateMediaCollect(pos, item);
            favoritesModel.loadFilters();
        }
    }
}
