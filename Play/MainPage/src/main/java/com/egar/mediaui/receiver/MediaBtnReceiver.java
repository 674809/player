package com.egar.mediaui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * Media button broadcast receiver.
 * <p>1. Register {@link MediaBtnReceiver} with action:"android.intent.action.MEDIA_BUTTON" in your 'AndroidManifest.xml'</p>
 * <p>2. Register in where you want use media button {@link android.media.AudioManager#registerMediaButtonEventReceiver}</p>
 *
 * @author Jun.Wang
 */
public class MediaBtnReceiver extends BroadcastReceiver {
    //TAG
    private final String TAG = "ybfBtnReceiver";

    protected static Map<String, MediaBtnListener> mMapNotifys = new HashMap<String, MediaBtnListener>();


    public interface MediaBtnListener {
        void onMediaButton(KeyEvent event);
    }

    public static void registerNotify(String notifyKey, MediaBtnListener mediaBtnListener) {
        if (!mMapNotifys.containsKey(notifyKey)) {
            mMapNotifys.remove(notifyKey);
            mMapNotifys.put(notifyKey, mediaBtnListener);
        }
    }

    public static void removeNotify(String notifyKey) {
        if (mMapNotifys.containsKey(notifyKey)) {
            mMapNotifys.remove(notifyKey);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        mediaButton(keyEvent);
    }


    public void mediaButton(KeyEvent event) {
        try {
            for (MediaBtnListener notify : mMapNotifys.values()) {
                notify.onMediaButton(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
