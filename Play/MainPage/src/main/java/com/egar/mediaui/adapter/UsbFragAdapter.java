package com.egar.mediaui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/29 11:44
 * @see {@link }
 */
public class UsbFragAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    /**
     * Fragment List
     */
    private List<T> mListFms;
    private List<String> mTitles;

    /**
     * Refresh Flag
     */
    private boolean mIsRefreshFlag = false;

    public UsbFragAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setListFrags(List<T> listFms, List<String> mTitles) {
        this.mListFms = listFms;
        this.mTitles = mTitles;
    }

    public void setRefreshFlag(boolean isRefresh) {
        this.mIsRefreshFlag = isRefresh;
    }

    public void refreshPages(boolean isRefresh) {
        setRefreshFlag(isRefresh);
        notifyDataSetChanged();
    }

    public void refresh(List<T> listFms, List<String> mTitles) {
        setListFrags(listFms, mTitles);
        notifyDataSetChanged();
    }

    public void setTitles(List<String> mTitles) {
        this.mTitles = mTitles;
    }

    public void refresh(List<T> listFms, List<String> mTitles, boolean isRefresh) {
        setRefreshFlag(isRefresh);
        setListFrags(listFms, mTitles);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mListFms == null) {
            return 0;
        }
        return mListFms.size();
    }

    @Override
    public Fragment getItem(int position) {
        if (mListFms == null || mListFms.size() == 0) {
            return null;
        }
        return mListFms.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if (mIsRefreshFlag) {
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }
}
