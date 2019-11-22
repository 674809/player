package com.egar.usbvideo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egar.mediaui.R;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/17 19:46
 * @see {@link }
 */
public class UsbVideoFoldersAdapter<T> extends BaseAdapter {
    private String TAG ="FoldersAdapter";
    private Context context;
    private List<T> mListData;
    /**
     * Playing media.
     */
    private ProVideo mPlayingMedia;
    private String mPlayingMediaFolderPath = "";
    private int page = 0;//一级界面
    public UsbVideoFoldersAdapter(Context context){
        this.context = context;
        mListData = (List<T>) new ArrayList<>();
    }
    public void refreshData(List<T> lists, ProVideo currMedia){
        synchronized (this) {
            mListData.clear();
            mListData.addAll(lists);
            setPlayingMedia(currMedia);
            notifyDataSetChanged();
        }

    }
    private void setPlayingMedia(ProVideo playingMedia) {
        try {
            this.mPlayingMedia = playingMedia;
            String playingMediaUrl = mPlayingMedia.getMediaUrl();
            this.mPlayingMediaFolderPath = playingMediaUrl.substring(0, playingMediaUrl.lastIndexOf("/"));
        } catch (Exception e) {
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
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    public T getItemInfo(int position) {
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
            convertView = View.inflate(context, R.layout.usb_video_folder_item, null);
            holder = new ViewHolder();
            holder.tv_text=(TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.imageView =(ImageView)convertView.findViewById(R.id.image_c);
            convertView.setTag(holder);

        }else{

            holder=(ViewHolder) convertView.getTag();
        }
         holder.tv_id.setText(position+1+"");
        if(0 == page){
            FilterFolder filterMedia = (FilterFolder) mListData.get(position);
            holder.tv_text.setText(filterMedia.mediaFolder.getName());
            holder.tv_id.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
        }else {
            ProVideo audio = (ProVideo) mListData.get(position);
            holder.tv_text.setText(audio.getTitle());
            holder.tv_id.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
        }


        //////////////
        if(position==selectItme){
            convertView.setBackgroundColor(Color.BLUE);
        }else{
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;

    }
    public  int selectItme=-1;
    public void setSelectItem(int selectItme){
        this.selectItme=selectItme;

    }

    static class ViewHolder{
        TextView tv_text;
        TextView tv_id;
        ImageView imageView;
    }
}
