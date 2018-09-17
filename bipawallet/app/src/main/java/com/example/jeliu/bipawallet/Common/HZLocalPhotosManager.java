package com.example.jeliu.bipawallet.Common;

import com.example.jeliu.bipawallet.util.CacheConstantKt;

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
        String path = CacheConstantKt.getSAppContext().getFilesDir().getAbsolutePath();
        String result = path + File.separator + s_directory_name;
        File album = new File(result);
        if (album.exists()) {
        } else {
            album.mkdir();
        }
    }

    public String getPhotoPath(String address) {
        String path = CacheConstantKt.getSAppContext().getFilesDir().getAbsolutePath();
        String result = path + File.separator + s_directory_name + File.separator + address + ".jpg";
        return result;
    }

    //public void store

}
