package com.egar.usbvideo.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egar.mediaui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/17 19:46
 * @see {@link }
 */
public class UsbVideoFileAdapter<T> extends BaseAdapter {
    private String TAG ="FoldersAdapter";
    private Context context;
    private List<ProVideo> mListData;
    private boolean mIsScrolling = false;
    /**
     * Playing media.
     */
    private ProVideo mPlayingMedia;
    private String mSelectedMediaUrl = "";
    private int mSelectedPos = -1;
    private int page = 0;//一级界面
    public UsbVideoFileAdapter(Context context){
        this.context = context;
        this.mListData = new ArrayList<>();

    }
    private void setListData(List<ProVideo> listData) {
        mListData.clear();
        mListData.addAll(listData);
    }
    public void setScrollState(boolean isScrolling) {
        mIsScrolling = isScrolling;
        if (!isScrolling) {
        notifyDataSetChanged();
        }
    }
    public void refreshData(String selectedMediaUrl) {
        this.mSelectedMediaUrl = selectedMediaUrl;
        setSelect(selectedMediaUrl);
        notifyDataSetChanged();
    }

    public void refreshData(List<ProVideo> listData, String selectedMediaUrl) {
        setSelect(selectedMediaUrl);
        setListData(listData);
        notifyDataSetChanged();
    }

    public void setSelect(String mediaUrl) {
        this.mSelectedMediaUrl = mediaUrl;
    }
    public void select(int pos) {
        Log.i(TAG, "select(" + pos + ")");
        mSelectedPos = pos;
        ProVideo item = getItemInfo(pos);

        if (item != null) {
            refreshData(item.getMediaUrl());
        }
    }

    public void setPage(int i){
        page = i;
    }

    public int getPage(){
        return page;
    }
    @Override
    public int getCount() {
        if(mListData == null){
            return 0;
        }
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    public ProVideo getItemInfo(int position) {
        try {
            return mListData.get(position);
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        if(null==convertView){
            convertView = View.inflate(context, R.layout.usb_video_files_fragment_item_name, null);
            holder = new ViewHolder();
          //  holder.vCover = (ImageView) convertView.findViewById(R.id.v_cover);
            holder.vName = (TextView) convertView.findViewById(R.id.v_name);
            convertView.setTag(holder);

        }else{

            holder=(ViewHolder) convertView.getTag();
        }
        ProVideo item = (ProVideo) getItemInfo(position);
        if (item != null) {
            holder.vName.setText(item.getFileName());
            //Set video image resource
            //Cover
            if (!mIsScrolling) {
                try {
                   String coverPicFilePath =item.getCoverUrl();
                    Log.i("coverAdapter", "coverPicFile: " + coverPicFilePath);
                    File coverPicFile = new File(coverPicFilePath);
                    if (coverPicFile.exists()) {
                       // holder.vCover.setImageURI(Uri.parse(coverPicFilePath));
                    } else {
                      //  holder.vCover.setImageResource(R.color.user_icon_2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Selected
            if (TextUtils.equals(mSelectedMediaUrl, item.getMediaUrl())) {
            //    convertView.setBackgroundResource(getColorResId("video_item_bg"));
                //Not selected
            } else {
                convertView.setBackgroundResource(0);
            }
        }
        return convertView;

    }



    private final class ViewHolder {
        ImageView vCover;
        TextView vName;
    }
}
