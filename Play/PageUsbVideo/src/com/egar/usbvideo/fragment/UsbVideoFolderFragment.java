package com.egar.usbvideo.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.utils.FragUtil;
import com.egar.usbvideo.fragment.folder_fragment.VideoFilesFragment;
import com.egar.usbvideo.fragment.folder_fragment.VideoFolderFragment;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/25 15:17
 * @see {@link }
 */
public class UsbVideoFolderFragment extends BaseUsbScrollLimitFragment implements View.OnClickListener {
    private TextView mtv_back, mtvMediaName, mtvMediaFolder;
    private FrameLayout frameLayout;
    private UsbVideoMainFragment fragment;
    private BaseLazyLoadFragment fragToLoad;

    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_USB_VIDEO_FOLDER;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    public void initView() {
        super.initView();
        fragment = (UsbVideoMainFragment) MainPresent.getInstatnce().getCurrentUsbFragmen(Configs.PAGE_IDX_USB_VIDEO);

        mtv_back = findViewById(R.id.tv_back);
        mtv_back.setOnClickListener(this);
        mtvMediaName = findViewById(R.id.tvMediaName);
        mtvMediaName.setOnClickListener(this);
        mtvMediaFolder = findViewById(R.id.tvMediaFolder);
        mtvMediaFolder.setOnClickListener(this);
        frameLayout = findViewById(R.id.usb_video_framelayout);
        initFiles();
    }

    @Override
    protected int getLayouId() {
        super.getLayouId();
        return R.layout.usb_video_frag_folder;
    }

    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
    }

    @Override
    public void onPageResume() {
        super.onPageResume();
    }

    @Override
    public void onPageStop() {
        super.onPageStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                fragment.setVideoPager(Configs.PAGE_USB_VIDEO_PLAY);
                break;
            case R.id.tvMediaName:
                initFiles();
                break;
            case R.id.tvMediaFolder:
                initFolder();
                break;

        }
    }

    private void initFiles() {
        LogUtil.w("intFavorites　");
        if (fragToLoad != null) {
            FragUtil.removeV4Fragment(fragToLoad, getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new VideoFilesFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_video_framelayout, fragToLoad, getChildFragmentManager());
    }

    private void initFolder() {
        LogUtil.w("intFavorites　");
        if (fragToLoad != null) {
            FragUtil.removeV4Fragment(fragToLoad, getChildFragmentManager());
            fragToLoad = null;
        }
        fragToLoad = new VideoFolderFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_video_framelayout, fragToLoad, getChildFragmentManager());
    }

    @Override
    public void onBack() {
        super.onBack();
        fragment.setVideoPager(Configs.PAGE_USB_VIDEO_PLAY);
    }
}
