package com.egar.usbmusic.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.adapter.UsbFoldersAdapter;
import com.egar.usbmusic.fragment.UsbMuiscFolderFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.FoldersModel;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;

import java.util.List;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 20:21
 * @see {@link }
 * folder 二级页面
 */
public class FolderFragment extends BaseLazyLoadFragment implements FoldersModel.IFolderDataChange,
        CollectListener, IndexTitleScrollView.OnIndexListener,ListView.OnScrollListener{
    private String TAG = "FolderFragment";
    private MainActivity activity;
    private ListView mlist_folder;
    private UsbFoldersAdapter adapter;
    private MusicPresent musicPresent;
    private int page = 0;//一级界面
    private Button bt;
    private List<ProAudio> audios;
    private  List<FilterFolder> filterFolders;
    private IndexTitleScrollView letterSidebar;
    private TextView mTv_center_char;
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
        LogUtil.w("init floder");
        mlist_folder =  findViewById(R.id.lv_folders);
        adapter = new UsbFoldersAdapter(activity);
        letterSidebar = (IndexTitleScrollView) findViewById(R.id.lsb);

        mTv_center_char = findViewById(R.id.tv_center_char);
        page = adapter.getPage();
        mlist_folder.setAdapter(adapter);
        musicPresent = MusicPresent.getInstance();
        adapter.setCollectListener(this);
        FoldersModel.getInstatnce().loadFilters();
        FoldersModel.getInstatnce().setFolderDataChangeListener(this);
        letterSidebar.registIndexChanged(this);
        mlist_folder.setOnScrollListener(this);

        mlist_folder.setOnItemClickListener(new ListOnItemClick());

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
            int section = adapter.getSectionForPosition(firstVisibleItem);
            Character firstVisibleChar = (char) section;
            letterSidebar.setIndex(firstVisibleChar);

    }


    public class ListOnItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LogUtil.i(TAG,"onItemClick");
            if(adapter.getPage() == 0){
                FilterFolder filterFolder = (FilterFolder) (filterFolders.get(position));
                FoldersModel.getInstatnce().loadMedias(filterFolder.mediaFolder.getPath());
                adapter.setPage(1);
                page = 1;
                LogUtil.i(TAG,"forder");
            }else {
                LogUtil.i(TAG,"file");
                ProAudio media = audios.get(position);
                musicPresent.playMusic(media.getMediaUrl(),position);
                //backPlayerPager();
            }
        }
    }


    @Override
    protected int getLayouId() {
        return R.layout.usb_music_folder_fragment;
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
    public void FolderDateChage(List<FilterFolder> filterFolders) {
        this.filterFolders = filterFolders;
        adapter.refreshData(filterFolders,musicPresent.getCurrMedia());
    }

    @Override
    public void FileDataChange(List<ProAudio> audios) {
        this.audios = audios;
        LogUtil.i(TAG,"refreshData file");
        adapter.refreshData(audios,musicPresent.getCurrMedia());
    }

    /**
     * 跳到播放页面
     */
    public void backPlayerPager(){
        ((UsbMuiscFolderFragment)getParentFragment()).BackUsbPlayerFragmet();
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        int  item = adapter.getPage();
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

    @Override
    public void onIndexChanged(int index, char c) {
        mTv_center_char.setText(""+c);
        mTv_center_char.setVisibility(View.VISIBLE);
        int position = adapter.getPositionForSection(c);
        mlist_folder.setSelection(position);
    }

    @Override
    public void onStopChanged(int index, char c) {
        mTv_center_char.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        mTv_center_char.setText(""+c);
        mTv_center_char.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTv_center_char.setVisibility(View.GONE);
            }
        }, 800);
        int position = adapter.getPositionForSection(c);
        mlist_folder.setSelection(position);
    }
}
