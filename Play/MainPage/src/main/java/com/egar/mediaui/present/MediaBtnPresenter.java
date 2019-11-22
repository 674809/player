package com.egar.mediaui.present;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.egar.mediaui.receiver.MediaBtnReceiver;
import com.egar.mediaui.util.LogUtil;


public class MediaBtnPresenter  implements MediaBtnReceiver.MediaBtnListener {
    //TAG
    private String TAG = "MediaBtnController";

    /**
     * {@link Context}
     */
//    private Context mContext;

    /**
     * Class ComponentName
     */
    private ComponentName mComponentName;

    /**
     * {@link AudioManager}
     */
    private AudioManager mAudioManager;

    private ImediaButton imediaButton;

    public boolean isfist = true;
    public MediaBtnPresenter(Context context) {
        //
//        mContext = context;
        mComponentName = new ComponentName(context.getPackageName(), MediaBtnReceiver.class.getName());
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        MediaBtnReceiver.registerNotify("MainActivity",this);
    }

    public void register() {
        unregister();
        Log.i(TAG, "register()");
        if (mAudioManager != null) {
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        }
    }

    public void unregister() {
        Log.i(TAG, "unregister()");
        if (mAudioManager != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        }
    }


    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    LogUtil.i(TAG, "next long click");
                    imediaButton.onNextLongClick();
                    break;
                case 2:
                    LogUtil.i(TAG, "next  click");
                    imediaButton.onNextClick();
                    break;
                case 3:
                    LogUtil.i(TAG, "prev long click");
                    imediaButton.onPrevLongClick();
                    break;
                case 4:
                    LogUtil.i(TAG, "prev  click");
                    imediaButton.onPrevClick();
                    break;
            }
        }
    };

  public   interface   ImediaButton{
      /**
       * 下一首长按事件
       */
      void onNextLongClick();

      /**
       * 下一首短按事件
       */
      void onNextClick();

      /**
       * 上一首长按事件
       */
      void onPrevLongClick();

      /**
       * 上一首短按事件
       */
      void onPrevClick();
    }

    public  void setMediaButListener(ImediaButton imediaButton){
        this.imediaButton = imediaButton;
    }

    @Override
    public void onMediaButton(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        if(isfist){
                            mhandler.sendMessageDelayed(mhandler.obtainMessage(1),1000);
                            isfist = false;
                        }
                        break;
                    case KeyEvent.ACTION_UP:
                        boolean hamessage = mhandler.hasMessages(1);
                       // LogUtil.i(TAG,"hamessage="+hamessage);
                        if(hamessage){
                            mhandler.sendEmptyMessage(2);
                            mhandler.removeMessages(1);
                        }
                        isfist = true;
                        break;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        if(isfist){
                            mhandler.sendMessageDelayed(mhandler.obtainMessage(3),1000);
                            isfist = false;
                        }
                        break;
                    case KeyEvent.ACTION_UP:
                        boolean hamessage = mhandler.hasMessages(3);
                    //    LogUtil.i(TAG,"hamessage="+hamessage);
                        if(hamessage){
                            mhandler.sendEmptyMessage(4);
                            mhandler.removeMessages(3);
                        }
                        isfist = true;
                        break;
                }
             //   LogUtil.i(TAG, "prev: ");
                break;
        }
    }
}
