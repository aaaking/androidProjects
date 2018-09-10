package com.example.jeliu.bipawallet.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.Network.IWallet;
import com.example.jeliu.bipawallet.Network.NetworkUtil;
import com.example.jeliu.bipawallet.Network.RequestResult;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.Webview.WebviewActivity;
import com.example.jeliu.bipawallet.bipacredential.BipaCredential;
import com.example.jeliu.bipawallet.bipacredential.BipaWalletFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by liuming on 12/05/2018.
 */

public class Common {

    public static final double s_ether = 1000000000;
    public static String WALLET_PATH = "";
    private static Web3j mWeb3j;
    public static Credentials mCredentials;

    public static void setWalletPath(Context context) {
        mWeb3j = Web3jFactory.build(new HttpService("http://47.52.224.7:8545"));
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            File file = context.getExternalFilesDir(null);
            if (file != null) {
                WALLET_PATH = file.getAbsolutePath() + File.separator + "walletfile";
            } else {
                WALLET_PATH = context.getFilesDir() + File.separator + "walletfile";
            }
        } else {
            if (context.getCacheDir() != null) {
                WALLET_PATH = context.getFilesDir() + File.separator + "walletfile";
            }
        }
    }

    public static Web3j getWeb3j() {
        if (mWeb3j == null) {
            mWeb3j = Web3jFactory.build(new HttpService("http://47.52.224.7:8545"));
        }
        return mWeb3j;
    }

    public static JSONObject objectToJSONObject(Object object) {
        Object json = null;
        JSONObject jsonObject = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONObject) {
            jsonObject = (JSONObject) json;
        }
        return jsonObject;
    }

    public static JSONArray objectToJSONArray(Object object) {
        Object json = null;
        JSONArray jsonArray = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            jsonArray = (JSONArray) json;
        }
        return jsonArray;
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    public static boolean emailValidation(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    public static boolean phoneValidation(String phone) {
        String regex = "^[0-9]*$";
        return phone.matches(regex);
    }

    public static String insertImageToSystem(Context context, String imagePath) {
        String url = "";
        try {
            url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, "name", "pic");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String takeViewScreenShot(Activity context, View view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = view;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            insertImageToSystem(context, mPath);
            return mPath;

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    public static String takeScreenshot(Activity context) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = context.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            insertImageToSystem(context, mPath);
            return mPath;

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    public static void showPaySucceed(final Context context, View llRoot, String hash) {
        int gravity = Gravity.BOTTOM;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_pay_succeed, null);

//        TextView tvStatus = popupView.findViewById(R.id.textView_status);
//        tvStatus.setText(status);

        ImageView ivQues = popupView.findViewById(R.id.imageView_question);
        ivQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, WebviewActivity.class);
                i.putExtra("url", getCenterUrl());
                context.startActivity(i);
            }
        });

        TextView tvHash = popupView.findViewById(R.id.textView_hash);
        tvHash.setText("hash值: " + hash);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // popupWindow.dismiss();
                return true;
            }
        });

        ImageView ivBack = popupView.findViewById(R.id.imageView_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(llRoot, gravity, 0, 0);
    }

    public static void showPayFailed(final Context context, View llRoot, String value, String hash) {
        int gravity = Gravity.BOTTOM;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_pay_failed, null);

        ImageView ivQues = popupView.findViewById(R.id.imageView_question);
        if (ivQues != null) {
            ivQues.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, WebviewActivity.class);
                    i.putExtra("url", getCenterUrl());
                    context.startActivity(i);
                }
            });
        }

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // popupWindow.dismiss();
                return true;
            }
        });

        ImageView ivBack = popupView.findViewById(R.id.imageView_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(llRoot, gravity, 0, 0);
    }

    public static String getCenterUrl() {
        if (UserInfoManager.getInst().getLanguage() == 1) {
            return "http://47.52.224.7:8081/HelpCenterEn.html";
        } else {
            return "http://47.52.224.7:8081/HelpCenter.html";
        }
    }

    public static String getEditionUrl() {
        if (UserInfoManager.getInst().getLanguage() == 1) {
            return "http://47.52.224.7:8081/editionEn.html";
        } else {
            return "http://47.52.224.7:8081/edition.html";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = HZApplication.getInst().getPackageManager();
            PackageInfo info = manager.getPackageInfo(HZApplication.getInst().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void WriteJsonFile(String strcontent, String strFilePath) {
        //每次写入时，都换行写
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                // Log.d("TestFile", "Create the file:" + strFilePath);
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strcontent.getBytes());
            raf.close();
        } catch (Exception e) {
            //Log.e("TestFile", "Error on write File.");
        }
    }

    public static Credentials loadWalletByPrivateKey(final String pwd, final String pk, final IWallet cb) {
        if (!NetworkUtil.isNetAvailable(HZApplication.getInst())) {
            Toast.makeText(HZApplication.getInst(), HZApplication.getInst().getString(R.string.network_exception), Toast.LENGTH_SHORT).show();
            cb.onWalletResult(null, "");
            return null;
        }
        final File destDir = new File(WALLET_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.GET_SALT_IV + "?password=" + pwd, null, new RequestResult() {
            @Override
            public boolean onSuccess(JSONObject jsonObject, String url) {
                Log.i("zzh-getsaltjson", jsonObject.toString());
                final String saltIVSeed = jsonObject.optString("msg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (saltIVSeed == null || saltIVSeed.trim().length() < 32) {
                                throw new Exception("get salt error");
                            }
                            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(pk, 16));
                            String fileName = WalletUtils.generateWalletFile(pwd, keyPair, destDir, false);
                            mCredentials = WalletUtils.loadCredentials(pwd, destDir + File.separator + fileName);
                            BipaCredential.encryptPK(pwd, mCredentials, saltIVSeed);
                            cb.onWalletResult(mCredentials, fileName);
                        } catch (Exception e) {
                            Looper.prepare();
                            cb.onWalletResult(null, "");
                            e.printStackTrace();
                            Toast.makeText(HZApplication.getInst(), "导入异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                return false;
            }

            @Override
            public void onFailure(String szValue, String url) {
                Toast.makeText(HZApplication.getInst(), "导入异常：" + szValue, Toast.LENGTH_SHORT).show();
                cb.onWalletResult(null, "");
            }
        });
        return null;
    }

    public static Credentials loadWalletByKeyStore(final String pwd, final String ks, final IWallet cb) {
        if (!NetworkUtil.isNetAvailable(HZApplication.getInst())) {
            Toast.makeText(HZApplication.getInst(), HZApplication.getInst().getString(R.string.network_exception), Toast.LENGTH_SHORT).show();
            cb.onWalletResult(null, "");
            return null;
        }
        final File destDir = new File(WALLET_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        final String fileName = dateFormat.format(new Date()) + "new-address" + ".json";
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.GET_SALT_IV + "?password=" + pwd, null, new RequestResult() {
            @Override
            public boolean onSuccess(JSONObject jsonObject, String url) {
                Log.i("zzh-getsaltjson", jsonObject.toString());
                final String saltIVSeed = jsonObject.optString("msg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (saltIVSeed == null || saltIVSeed.trim().length() < 32) {
                                throw new Exception("get salt error");
                            }
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(destDir + File.separator + fileName)));
//            new OutputStreamWriter(HZApplication.getInst().openFileOutput(destDir + File.separator + fileName, Context.MODE_PRIVATE));
                            outputStreamWriter.write(ks);
                            outputStreamWriter.close();
                            mCredentials = WalletUtils.loadCredentials(pwd, destDir + File.separator + fileName);
                            BipaCredential.encryptPK(pwd, mCredentials, saltIVSeed);
                            cb.onWalletResult(mCredentials, fileName);
                        } catch (Exception e) {
                            Looper.prepare();
                            cb.onWalletResult(null, null);
                            Toast.makeText(HZApplication.getInst(), "导入异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                return false;
            }

            @Override
            public void onFailure(String szValue, String url) {
                Toast.makeText(HZApplication.getInst(), "导入异常：" + szValue, Toast.LENGTH_SHORT).show();
                cb.onWalletResult(null, "");
            }
        });
        return null;
    }

    public static Credentials loadWalletByPKBipa(final String pwd, final String safePK, final IWallet cb) {
        if (!NetworkUtil.isNetAvailable(HZApplication.getInst())) {
            Toast.makeText(HZApplication.getInst(), HZApplication.getInst().getString(R.string.network_exception), Toast.LENGTH_SHORT).show();
            cb.onWalletResult(null, "");
            return null;
        }
        final File destDir = new File(WALLET_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.GET_SALT_IV + "?password=" + pwd, null, new RequestResult() {
            @Override
            public boolean onSuccess(JSONObject jsonObject, String url) {
                Log.i("zzh-getsaltjson", jsonObject.toString());
                final String saltIVSeed = jsonObject.optString("msg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WalletFile missingWallet = BipaWalletFile.findMissingWallet(safePK, pwd, saltIVSeed);
                        String pk = BipaCredential.getPK(missingWallet, safePK, pwd);
                        if (saltIVSeed == null || saltIVSeed.trim().length() < 32 || TextUtils.isEmpty(safePK) || safePK.length() <= 64 || TextUtils.isEmpty(pk)) {
                            Looper.prepare();
                            cb.onWalletResult(null, null);
                            Toast.makeText(HZApplication.getInst(), "导入异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }
                        try {
                            ECKeyPair pair = ECKeyPair.create(new BigInteger(pk, 16));
                            String fileName = WalletUtils.generateWalletFile(pwd, pair, destDir, false);
                            mCredentials = WalletUtils.loadCredentials(pwd, destDir + File.separator + fileName);

                            WalletFile walletFile = Wallet.createLight(pwd, ECKeyPair.create(new BigInteger(safePK.substring(0, 64), 16)));//its address is wrong
                            walletFile.setAddress(mCredentials.getAddress().substring(2).toLowerCase());
                            BipaWalletFile bipaWalletFile = new BipaWalletFile();
                            BipaWalletFile.duplicateToBipa(bipaWalletFile, walletFile);
                            bipaWalletFile.miss_mac = safePK.substring(64);//missingWallet.getCrypto().getMac();

                            SharedPreferences sp = HZApplication.getInst().getSharedPreferences(BipaCredential.SP_SAFE_BIPA, 0);
                            SharedPreferences.Editor localEditor = sp.edit();
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(bipaWalletFile);
                            localEditor.putString(mCredentials.getAddress().substring(2).toLowerCase(), jsonStr);
                            localEditor.apply();
                            cb.onWalletResult(mCredentials, fileName);
                        } catch (Exception e) {
                            Looper.prepare();
                            cb.onWalletResult(null, null);
                            Toast.makeText(HZApplication.getInst(), "导入异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                return false;
            }
            @Override
            public void onFailure(String szValue, String url) {
                Toast.makeText(HZApplication.getInst(), "导入异常：" + szValue, Toast.LENGTH_SHORT).show();
                cb.onWalletResult(null, "");
            }
        });
        return null;
    }

    public static Credentials loadWalletByKSBipa(final String pwd, final String ks, final IWallet cb) {
        if (!NetworkUtil.isNetAvailable(HZApplication.getInst())) {
            Toast.makeText(HZApplication.getInst(), HZApplication.getInst().getString(R.string.network_exception), Toast.LENGTH_SHORT).show();
            cb.onWalletResult(null, "");
            return null;
        }
        final File destDir = new File(WALLET_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<BipaWalletFile>() {
        }.getType();
        final BipaWalletFile bipaWalletFile = gson.fromJson(ks.replace("&quot;", "\""), type);
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.GET_SALT_IV + "?password=" + pwd, null, new RequestResult() {
            @Override
            public boolean onSuccess(JSONObject jsonObject, String url) {
                Log.i("zzh-getsaltjson", jsonObject.toString());
                final String saltIVSeed = jsonObject.optString("msg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String safePK = BipaCredential.getSafePK(bipaWalletFile, pwd);
                        WalletFile missingWallet = BipaWalletFile.findMissingWallet(safePK, pwd, saltIVSeed);
                        String pk = BipaCredential.getPK(missingWallet, safePK, pwd);
                        if (saltIVSeed == null || saltIVSeed.trim().length() < 32 || TextUtils.isEmpty(safePK) || TextUtils.isEmpty(pk) || TextUtils.isEmpty(bipaWalletFile.miss_mac) || bipaWalletFile.miss_mac.length() <= 0) {
                            Looper.prepare();
                            cb.onWalletResult(null, null);
                            Toast.makeText(HZApplication.getInst(), "导入异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }
                        //
                        try {
                            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(pk, 16));
                            String fileName = WalletUtils.generateWalletFile(pwd, keyPair, destDir, false);
                            mCredentials = WalletUtils.loadCredentials(pwd, destDir + File.separator + fileName);
                            //
                            SharedPreferences sp = HZApplication.getInst().getSharedPreferences(BipaCredential.SP_SAFE_BIPA, 0);
                            SharedPreferences.Editor localEditor = sp.edit();
                            localEditor.putString(mCredentials.getAddress().substring(2).toLowerCase(), ks.replace("&quot;", "\""));
                            localEditor.apply();
                            cb.onWalletResult(mCredentials, fileName);
                        } catch (Exception e) {
                            Looper.prepare();
                            cb.onWalletResult(null, null);
                            Toast.makeText(HZApplication.getInst(), "导入异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                return false;
            }
            @Override
            public void onFailure(String szValue, String url) {
                Toast.makeText(HZApplication.getInst(), "导入异常：" + szValue, Toast.LENGTH_SHORT).show();
                cb.onWalletResult(null, "");
            }
        });
        return null;
    }

    public static Credentials createLocalWallet(final String pwd, final IWallet cb) {
        if (!NetworkUtil.isNetAvailable(HZApplication.getInst())) {
            Toast.makeText(HZApplication.getInst(), HZApplication.getInst().getString(R.string.network_exception), Toast.LENGTH_SHORT).show();
            cb.onWalletResult(null, "");
            return null;
        }
        final File destDir = new File(WALLET_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.GET_SALT_IV + "?password=" + pwd, null, new RequestResult() {
            @Override
            public boolean onSuccess(JSONObject jsonObject, String url) {
                final String saltIVSeed = jsonObject.optString("msg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (saltIVSeed == null || saltIVSeed.trim().length() < 32) {
                                throw new Exception("get salt error");
                            }
                            String fileName = WalletUtils.generateLightNewWalletFile(pwd, new File(WALLET_PATH));
                            mCredentials = WalletUtils.loadCredentials(pwd, WALLET_PATH + File.separator + fileName);
                            BipaCredential.encryptPK(pwd, mCredentials, saltIVSeed);
                            cb.onWalletResult(mCredentials, fileName);
                        } catch (Exception e) {
                            Looper.prepare();
                            cb.onWalletResult(null, "");
                            Toast.makeText(HZApplication.getInst(), "创建异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            Looper.loop();
                        }
                    }
                }).start();
                return false;
            }
            @Override
            public void onFailure(String szValue, String url) {
                Toast.makeText(HZApplication.getInst(), "导入异常：" + szValue, Toast.LENGTH_SHORT).show();
                cb.onWalletResult(null, "");
            }
        });
        return null;
    }

//    public static byte[] strToByteArray(String str) {
//        if (str == null) {
//            return null;
//        }
//        byte[] byteArray = str.getBytes();
//        return byteArray;
//    }
//
//    public String methodHeader(String method) {
//        byte[] bytes = strToByteArray(method);
//        byte[] bytes1 = org.web3j.crypto.Hash.sha3(bytes);
//        String hex = Numeric.toHexString(bytes1, 0, 4, true);
//        return hex;
//    }
//
//    public String signedContractTransactionData(String contractAddress,//代币的智能合约地址
//                                                String toAdress,//对方的地址
//                                                BigInteger nonce,//获取到交易数量
//                                                BigInteger gasPrice,
//                                                BigInteger gasLimit,
//                                      double value, double decimal,
//                                                Credentials credentials) {
//        //因为每个代币可以规定自己的小数位, 所以实际的转账值=数值 * 10^小数位
//        BigDecimal realValue = BigDecimal.valueOf(value * Math.pow(10.0, decimal));
//        //0xa9059cbb代表某个代币的转账方法hex(transfer) + 对方的转账地址hex + 转账的值的hex
//        String data = methodHeader("transfer(address,uint256)") + //Params.Abi.transfer + // 0xa9059cbb
//        Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(toAdress), 64) + Numeric.toHexStringNoPrefixZeroPadded(realValue.toBigInteger(), 64);
//        RawTransaction rawTransaction = RawTransaction.createTransaction(
//                nonce,
//                gasPrice,
//                gasLimit,
//                contractAddress,
//                data);
//
//        //使用TransactionEncoder对RawTransaction进行签名操作
//        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//        //转换成0x开头的字符串
//        return Numeric.toHexString(signedMessage);
//    }
//
//
////    //ETH转账签名
////    fun signedEthTransactionData(to: String, //转账的钱包地址
////                                 nonce: BigInteger,//获取到的交易次数
////                                 gasPrice: BigInteger, //
////                                 gasLimit: BigInteger, //
////                                 value: Double, //转账的值
////                                 credentials: Credentials): String {
////        //把十进制的转换成ETH的Wei, 1ETH = 10^18 Wei
////        val realValue = Convert.toWei(value.toString(), Convert.Unit.ETHER)
////        val rawTransaction = RawTransaction.createEtherTransaction(
////                nonce,
////                gasPrice,
////                gasLimit,
////                to,
////                realValue.toBigIntegerExact())
////        //手续费= (gasPrice * gasLimit ) / 10^18 ether
////        //使用TransactionEncoder对RawTransaction进行签名操作
////        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
////        //转换成0x开头的字符串
////        return Numeric.toHexString(signedMessage)
////    }
////
////    //基于以太坊的代币转账签名
////    fun signedContractTransactionData(contractAddress: String,//代币的智能合约地址
////                                      toAdress: String,//对方的地址
////                                      nonce: BigInteger,//获取到交易数量
////                                      gasPrice: BigInteger,
////                                      gasLimit: BigInteger,
////                                      value: Double, decimal: Double,
////                                      credentials: Credentials): String {
////        //因为每个代币可以规定自己的小数位, 所以实际的转账值=数值 * 10^小数位
////        val realValue = BigDecimal.valueOf(value * Math.pow(10.0, decimal))
////        //0xa9059cbb代表某个代币的转账方法hex(transfer) + 对方的转账地址hex + 转账的值的hex
////        val data = methodHeader("transfer(address,uint256)")//Params.Abi.transfer + // 0xa9059cbb
////        Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(toAdress), 64) +
////                Numeric.toHexStringNoPrefixZeroPadded(realValue.toBigInteger(), 64)
////        val rawTransaction = RawTransaction.createTransaction(
////                nonce,
////                gasPrice,
////                gasLimit,
////                contractAddress,
////                data)
////
////        //使用TransactionEncoder对RawTransaction进行签名操作
////        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
////        //转换成0x开头的字符串
////        return Numeric.toHexString(signedMessage)
////    }
//
//    public void handleLocalTransaction() {
//        Web3j web3j = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
//        try {
//            Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/walletfile");
//            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
//                    "address", DefaultBlockParameterName.LATEST).sendAsync().get();
//            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
////            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
////                    nonce, <gas price>, <gas limit>, <toAddress>, <value>);
////
////// sign & send our transaction
////            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
////            String hexValue = Hex.toHexString(signedMessage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CipherException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
}
