package com.egar.mediaui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.egar.mediaui.lib.BaseFragActivity;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 18:23
 * @see {@link }
 */
public class BaseSubActivity extends BaseFragActivity {
    private RelativeLayout mContentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContentLayout = (RelativeLayout) findViewById(R.id.content_view);

    }

    @Override
    public void setContentView(int layoutResID) {

        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        // TODO Auto-generated method stub
        if (mContentLayout != null) {
            mContentLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }


}
