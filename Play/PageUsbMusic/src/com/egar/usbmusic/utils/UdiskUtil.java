package com.egar.usbmusic.utils;

import android.content.Context;
import android.util.Log;

import com.egar.mediaui.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UdiskUtil {
    private static String TAG = "UtilsUdisk";

    /**
     * 获取sd卡和U盘路径
     *
     * @return
     */
    public static List<String> getAllExterSdcardPath() {
        List<String> SdList = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                Log.d("", line);
                // 将常见的linux分区过滤掉
                // SdList.add(line);
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                // 下面这些分区是我们需要的
                if (line.contains("vfat") || line.contains("fuse")
                        || line.contains("fat") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1) {
                        String path = items[2].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && !SdList.contains(path)
                                && path.contains("media_rw")) {
                            if(path.contains("media_rw/sdcard")){
                                    continue;
                            }
                            SdList.add(items[2]);
                        }

                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return SdList;
    }

    public static boolean isHasSupperUDisk(Context context) {
        for (String list : getAllExterSdcardPath()){
            LogUtil.i(TAG,"udisk path ="+list);
        }
        return getAllExterSdcardPath().size() > 0;
    }

    /**
     * 判断文件是否存在
     */
    private static boolean isFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean isExist() {
        List<String> list = getAllExterSdcardPath();
        boolean isexist = false;
        if (list.size() > 0) {
            isexist = isFile(list.get(0) + "/Android");
            Log.e(TAG, "Android is " + isexist);
            return isexist;
        }
        return false;

    }
}
