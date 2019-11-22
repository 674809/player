package com.egar.usbmusic.fragment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.fragment.folder_fragment.AlbumsFragment;
import com.egar.usbmusic.fragment.folder_fragment.AllSongsFragment;
import com.egar.usbmusic.fragment.folder_fragment.ArtistFragment;
import com.egar.usbmusic.fragment.folder_fragment.FavoritesFragment;
import com.egar.usbmusic.fragment.folder_fragment.FolderFragment;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.utils.FragUtil;


/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/11 09:25
 * 文件夹页面
 * @see {@link }
 */
public class UsbMuiscFolderFragment extends BaseUsbScrollLimitFragment implements View.OnClickListener{
    private String TAG = "UsbFolderFragment";
    private MainActivity activity;
    private UsbMusicMainFragment musicMainFrag;
    private MusicPresent musicPresent;

    private FavoritesFragment favoritesFrag;
    private FolderFragment folderFrag;
    private BaseLazyLoadFragment fragToLoad;
    private TextView tv_title;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_USB_MUSIC_FOLDER;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    protected int getLayouId() {
        super.getLayouId();
        return R.layout.usb_music_frag_folder;
    }

    @Override
    public void initView() {
        super.initView();
        musicMainFrag = (UsbMusicMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_USB_MUSIC_PLAY);
        initfindView();
        tv_title.setText(R.string.favorite);
    }


    private void initfindView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.bt_favorites).setOnClickListener(this);
        findViewById(R.id.bt_folder).setOnClickListener(this);
        findViewById(R.id.bt_songs).setOnClickListener(this);
        findViewById(R.id.bt_art).setOnClickListener(this);
        findViewById(R.id.bt_album).setOnClickListener(this);
        tv_title =  findViewById(R.id.tv_title);
    }


    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        LogUtil.w("init folder");
        initMusicPresent();
        initFavorites();
    }

    private void initFavorites() {
        LogUtil.w("intFavorites　");
        if(fragToLoad != null){
            FragUtil.removeV4Fragment(fragToLoad,getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new  FavoritesFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder,fragToLoad,getChildFragmentManager());
    }

    private void initFoler() {
        if(fragToLoad != null){
            FragUtil.removeV4Fragment(fragToLoad,getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new  FolderFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder,fragToLoad,getChildFragmentManager());

    }

    private void initAllSongs() {
        if(fragToLoad != null){
            FragUtil.removeV4Fragment(fragToLoad,getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new AllSongsFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder,fragToLoad,getChildFragmentManager());
    }
    private void initArtist() {
        if(fragToLoad != null){
            FragUtil.removeV4Fragment(fragToLoad,getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new ArtistFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder,fragToLoad,getChildFragmentManager());
    }
    private void initAlbums() {
        if(fragToLoad != null){
            FragUtil.removeV4Fragment(fragToLoad,getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new AlbumsFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder,fragToLoad,getChildFragmentManager());
    }


    /**
     * 初始化音乐操作句柄
     */
    private void initMusicPresent() {
        if (musicPresent == null) {
            musicPresent =MusicPresent.getInstance();
        }
    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        LogUtil.w( "folder onPageLoadStop");
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackKey();
                break;
            case R.id.bt_favorites:
                initFavorites();
                tv_title.setText(R.string.favorite);
                break;
            case R.id.bt_folder:
                initFoler();
                tv_title.setText(R.string.folder);
                break;
            case R.id.bt_songs:
                initAllSongs();
                tv_title.setText(R.string.all_song);
                break;
            case R.id.bt_art:
                initArtist();
                tv_title.setText(R.string.artists);
                break;
            case R.id.bt_album:
                initAlbums();
                tv_title.setText(R.string.albums);
                break;
        }
    }

    @Override
    public void onBack() {
        super.onBack();
        LogUtil.i(TAG, "onBack");
        onBackKey();
    }

    /**
     * 返回键
     */
    public void onBackKey(){
        int pageLayer = fragToLoad.getPageIdx();
        LogUtil.i(TAG,"pageLayer ="+pageLayer);
        if(fragToLoad instanceof FolderFragment ){
            if(pageLayer == 1){ //如果是列表页面，返回时，重新创建第一文件夹页面
                initFoler();
            }else {
                BackUsbPlayerFragmet();
            }
        }else if(fragToLoad instanceof ArtistFragment) {
            if(pageLayer == 1){ //如果是列表页面，返回时，重新创建第一文件夹页面
                initArtist();
            }else {
                BackUsbPlayerFragmet();
            }
        }else if(fragToLoad instanceof  AlbumsFragment){
            if(pageLayer == 1){ //如果是列表页面，返回时，重新创建第一文件夹页面
                initAlbums();
            }else {
                BackUsbPlayerFragmet();
            }
        }else {
            BackUsbPlayerFragmet();
        }
    }

    /**
     * 返回到Usb 播放歌曲页面
     */
    public void BackUsbPlayerFragmet(){
       UsbMusicMainFragment fragment = (UsbMusicMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(0);
        fragment.setUsbPlayerCurrentItem(0);
    }
}
