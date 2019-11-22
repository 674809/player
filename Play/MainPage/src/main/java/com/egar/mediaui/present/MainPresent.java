package com.egar.mediaui.present;

import android.content.ComponentName;
import android.egar.ActivityPolicyClient;
import android.egar.CarManager;
import android.egar.EventProxyClient;
import android.egar.IActivityStatus;
import android.egar.IReverseStatus;
import android.os.RemoteException;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IWindowChange;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.model.FragmentFactory;
import com.egar.mediaui.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 14:42
 * @see {@link }
 */
public class MainPresent {
    private String TAG= "Present";
    private static MainPresent mPresnet;
    private CarManager carManager;
    FragmentFactory mFragmentFactory;
    private ActivityPolicyClient activityPolicyClient;
    private EventProxyClient eventProxyClient;
    private  ActivityState activityState;
    //监听集合
    private List<IWindowChange>  iWindowChanges = new ArrayList<>();
    /**
     * 初始化页面
     */
    private MainPresent(){
        mFragmentFactory = new  FragmentFactory();
        carManager = new CarManager(App.getContext());
        activityPolicyClient = carManager.getActivityPolicy(App.getContext());
    }

    /**
     * 注册倒车事件
     */
    public void registReverse(){
        eventProxyClient = carManager.getEventProxy(App.getContext());
        eventProxyClient.registerReverseStatus(new Revers());
    }

    /**
     * 注册全屏半屏监听
     * @param
     */
    public void registActivitState(){
        LogUtil.i(TAG,"activityPolicyClient : "+activityPolicyClient);
         if(activityPolicyClient == null ){
             LogUtil.i(TAG,"activityPolicyClient is null ");
             return;
         }else {
             activityState = new ActivityState();
             activityPolicyClient.registerActivityCallback(activityState);

         }

    }
    /**
     *反注册 全半屏监听
     * @param
     */
    public void unRegistActivityState(){
        if(activityPolicyClient !=null){
        activityPolicyClient.unregisterActivityCallback(activityState);
        }

    }

    /**
     * 切换半屏或全屏
     * @param activity
     * @param isfull
     */
    public void checkFullOrHalf(MainActivity activity ,boolean isfull){
        if(activityPolicyClient !=null){
            activityPolicyClient.switchLayout(activity,isfull);
        }
    }

    public int getActivityPosition(MainActivity activity){
        LogUtil.i(TAG,"getActivityPosition");
        if(activityPolicyClient != null){
            LogUtil.i(TAG,"activityPolicyClient = "+activityPolicyClient.getActivityPosition(activity));
           return activityPolicyClient.getActivityPosition(activity);
        }else {
            return 0;
        }

    }
    public static MainPresent getInstatnce(){
        if (mPresnet == null){
            synchronized (MainPresent.class){
                mPresnet = new MainPresent();
            }
        }
        return mPresnet;
    }

    /**
     * 获取MainFragment数据
     * @return
     */
    public List<BaseLazyLoadFragment> getMainFragmentList(){
        return mFragmentFactory.loadFragments();
    }


    /**
     * 获取UsbFragment数据
     * @return
     */
    public List<BaseLazyLoadFragment> getUsbFragmentList(){
        return mFragmentFactory.loadUsbFragments();
    }


    /**
     * 获取Usbmusic数据
     * @return
     */
    public List<BaseLazyLoadFragment> getUsbMusicFragmentList(){
        return mFragmentFactory.loadUsbMusicFragments();
    }

    /**
     * 获取USbMusci 子页面Fragemnt
     * @param position
     * @return
     */
    public BaseLazyLoadFragment getUsbMuiscChiledFragment(int position){
        return mFragmentFactory.getUsbMuiscChildFragment(position);
    }

    /**
     * 获取video fragment数据
     * @return
     */
    public List<BaseLazyLoadFragment> getUsbVideoFragmentList(){
        return mFragmentFactory.loadUsbVideoFragments();
    }

    /**
     * 获取USb-Video 子页面Fragemnt
     * @param position
     * @return
     */
    public BaseLazyLoadFragment getUsbVideoChiledFragment(int position){
        return mFragmentFactory.getUsbVideoChildFragment(position);
    }
    /**
     * 获取Main当前fragment
     * @param position
     * @return
     */
    public BaseLazyLoadFragment getCurrenFragmen(int position){
        return mFragmentFactory.getMainCurrentFragmet(position);
    }


    /**
     * 获取Usb当前fragment
     * @param position
     * @return
     */
    public BaseLazyLoadFragment getCurrentUsbFragmen(int position){
        return mFragmentFactory.getUsbCurrentFragmet(position);
    }

    /**
     * 获取UsbFragment
     * @return
     */
    public BaseUsbFragment getUSbFragment(){
        return (BaseUsbFragment) mFragmentFactory.getMainCurrentFragmet(Configs.PAGE_INDX_USB);
    }

    /**
     * 设置usbIinditer 隐藏与显示
     * @param ishide
     */
    public void setInditeHide(boolean ishide){
        BaseUsbFragment fragment1 = (BaseUsbFragment) MainPresent.getInstatnce().getUSbFragment();
        fragment1.setIndicatorVisib(ishide);
    }
    /**
     * 添加全屏半屏监听
     */
    public void setOnWindowChange(IWindowChange iWindowChange){
        iWindowChanges.add(iWindowChange);
    }
    /**
     *添加半屏监听
     */
    public void setOnWindowChangeHalf(){
        for (IWindowChange windowChange :iWindowChanges){
            windowChange.onWindowChangeHalf();
        }
    }
    /**
     * 添加全屏监听
     */
    public void setOnWindowChangeFull(){
        for (IWindowChange windowChange :iWindowChanges){
            windowChange.onWindowChangeFull();
        }
    }


    public void Destory(){
        iWindowChanges.clear();
        mPresnet = null;
    }



    class  Revers extends  IReverseStatus.Stub{

        @Override
        public void onReverseStatus(boolean isRevers) throws RemoteException {
            LogUtil.d(TAG," isRevers: "+isRevers); //是否倒车状态
        }
    }

    class  ActivityState  extends IActivityStatus.Stub{

        @Override
        public void onActivityStatus(ComponentName componentName) throws RemoteException {
            LogUtil.i(TAG,"ComponentName :"+componentName);
        }
    }
}
