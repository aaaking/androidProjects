package com.example.jeliu.bipawallet.Common;

import android.content.Context;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import java.io.File;

/**
 * Created by liuming on 18/05/2018.
 */

public class HZLocalPhotosManager {
    private static HZLocalPhotosManager s_inst = new HZLocalPhotosManager();
    private static String s_directory_name = "profiles";

    public static HZLocalPhotosManager getInst() {
        return s_inst;
    }

    private HZLocalPhotosManager() {
        String path = HZApplication.getInst().getFilesDir().getAbsolutePath();
        String result = path + File.separator + s_directory_name;
        File album = new File(result);
        if (album.exists()) {
        } else {
            album.mkdir();
        }
    }

    public String getPhotoPath(String address) {
        String path = HZApplication.getInst().getFilesDir().getAbsolutePath();
        String result = path + File.separator + s_directory_name + File.separator + address + ".jpg";
        return result;
    }

    //public void store

}
