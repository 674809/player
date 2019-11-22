package com.egar.mediaui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/4 09:01
 * @see {@link }
 */
public class MediaBoardcast extends BroadcastReceiver {
    private String TAG ="MediaBoardcast";
    protected static Map<String, IMediaReceiver> mNotifys = new HashMap<String, IMediaReceiver>();

    public interface IMediaReceiver {
        void onUdiskStateChange(int state);

    }

    public static void registerNotify(String notifyKey, IMediaReceiver mediaBtnListener) {
        if (!mNotifys.containsKey(notifyKey)) {
            mNotifys.remove(notifyKey);
            mNotifys.put(notifyKey, mediaBtnListener);
        }
    }

    public static void removeNotify(String notifyKey) {
        if (mNotifys.containsKey(notifyKey)) {
            mNotifys.remove(notifyKey);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(TAG,"action="+action);
        if(Configs.UDISK_MOUNT .equals(action)){
            UdiskStateChange(1);
        }else if(Configs.UDISK_UNMOUNT .equals(action) || "android.intent.action.MEDIA_EJECT".equals(action)){
            UdiskStateChange(0);
        }
    }


    public void UdiskStateChange(int state) {
        try {
            for (IMediaReceiver notify : mNotifys.values()) {
                notify.onUdiskStateChange(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
