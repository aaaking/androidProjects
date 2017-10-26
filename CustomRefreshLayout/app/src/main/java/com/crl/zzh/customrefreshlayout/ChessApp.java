package com.crl.zzh.customrefreshlayout;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.crl.zzh.customrefreshlayout.util.ScreenUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;


/**
 * Created by 周智慧 on 17/2/25.
 */

public class ChessApp extends Application {
    public static Context sAppContext;
    public static int sMaxThread;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        //test dump
        //mSettingsPreferences.getAuthcodeTime();
        sMaxThread = Runtime.getRuntime().availableProcessors() * 2 + 1;
        ScreenUtil.init(this);
        //1The IMEI: 仅仅只对Android手机有效:
//        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String szImei = "";//TelephonyMgr.getDeviceId();
        Log.i("唯一标识符1", szImei);
        //2. Pseudo-Unique ID, 这个在任何Android手机中都有效
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
        Log.i("唯一标识符2", m_szDevIDShort);
        //3. The Android ID
        //通常被认为不可信，因为它有时为null。开发文档中说明了：这个ID会改变如果进行了出厂设置。并且，如果某个Andorid手机被Root过的话，这个ID也可以被任意改变。
        String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("唯一标识符3", m_szAndroidID);
        //4. The WLAN MAC Address string
        //是另一个唯一ID。但是你需要为你的工程加入android.permission.ACCESS_WIFI_STATE 权限，否则这个地址会为null。
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        Log.i("唯一标识符4", m_szWLANMAC);
        //5. The BT MAC Address string
        //只在有蓝牙的设备上运行。并且要加入android.permission.BLUETOOTH 权限.
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();
        Log.i("唯一标识符5", TextUtils.isEmpty(m_szBTMAC) ? "Empty" : m_szBTMAC);
        Log.i("唯一标识符6", Installation.id(this));//每次安装都不一样
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            builder.detectFileUriExposure();
//        }
//        FrescoUtil.init(getApplicationContext());
//        Stetho.initializeWithDefaults(this);
    }
}

class Installation {
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}
