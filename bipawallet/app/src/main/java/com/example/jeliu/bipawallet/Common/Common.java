package com.example.jeliu.bipawallet.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.Webview.WebviewActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by liuming on 12/05/2018.
 */

public class Common {

    public static final double s_ether = 1000000000;

    public static JSONObject objectToJSONObject(Object object){
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

    public static JSONArray objectToJSONArray(Object object){
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
        tvHash.setText("hash值: "+ hash);

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

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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

    public static void WriteJsonFile(String strcontent,String strFilePath)
    {
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

//    private void dotest() {
//        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
//        Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/walletfile");
//
//    }

    //
//    fun loadWallet() {
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        if (!path.exists()) {
//            path.mkdir()
//        }
//        var fileName = "original" + ".json"//WalletUtils.generateLightNewWalletFile(password, File(path.toString()))
//        Log.e("zzh", "generateWallet: $path/$fileName")
//        credentials = WalletUtils.loadCredentials("12345678", path.toString() + "/" + fileName)
//        wallet_address.text = "钱包地址：\n" + credentials!!.getAddress()
//        //
//        //connect network
//        Thread(Runnable {
//            web3j = Web3jFactory.build(HttpService("http://47.52.224.7:8545"))//https://rinkeby.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89
//            val web3ClientVersion = web3j?.web3ClientVersion()?.send()
//            val clientVersion = web3ClientVersion?.getWeb3ClientVersion()
//            mTokenContract = Zzhc_sol_ZZHToken.load("0x122638aeaccdadb35a707c5ffcaa0226e43dc02b", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
//            runOnUiThread {
//                connect_info.text = "节点连接信息：\n" + clientVersion
//                balance.isEnabled = web3j != null && credentials != null
//                transfer.isEnabled = web3j != null && credentials != null
//            }
//            initObservable()
//        }).start()
//    }
//
//    public void createLocalWallet() {
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        if (!path.exists()) {
//            path.mkdir();
//        }
//        String fileName = "original" + ".json";
//        try {
//            String keystore = WalletUtils.generateLightNewWalletFile("123456", new File(path.toString() + "/" + fileName));
//            Log.d("Tag", keystore);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (CipherException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        try {
////            Credentials credentials = WalletUtils.loadCredentials("password", path.toString() + "/" + fileName);
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (CipherException e) {
////            e.printStackTrace();
////        }
//    }
//
//    public void loadWallet() {
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        if (!path.exists()) {
//            path.mkdir();
//        }
//        String fileName = "original" + ".json";
//        try {
//            Credentials credentials = WalletUtils.loadCredentials("123456", path.toString() + "/" + fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CipherException e) {
//            e.printStackTrace();
//        }
//    }
//
//    String signedEthTransactionData(String to, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, double value, Credentials credentials) {
//        //把十进制的转换成ETH的Wei, 1ETH = 10^18 Wei
//        BigDecimal realValue = Convert.toWei(value +"", Convert.Unit.ETHER);
//        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
//                nonce,
//                gasPrice,
//                gasLimit,
//                to,
//                realValue.toBigIntegerExact());
//        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//        //转换成0x开头的字符串
//
//        return Numeric.toHexString(signedMessage);
//    }
//
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