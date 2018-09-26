package com.example.jeliu.bipawallet.Base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Network.RequestResult;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.Webview.WebviewActivity;
import com.example.jeliu.bipawallet.util.LogUtil;
import com.example.jeliu.eos.di.component.ActivityComponent;
import com.example.jeliu.eos.di.component.DaggerActivityComponent;
import com.example.jeliu.eos.di.module.ActivityModule;
import com.example.jeliu.zxingsimplify.zxing.Activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

//import org.bouncycastle.util.encoders.Hex;
//import org.web3j.abi.datatypes.generated.Int64;
//import org.web3j.crypto.CipherException;
//import org.web3j.crypto.Credentials;
//import org.web3j.crypto.RawTransaction;
//import org.web3j.crypto.TransactionEncoder;
//import org.web3j.crypto.WalletUtils;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
//import org.web3j.protocol.http.HttpService;
//import org.web3j.tx.Transfer;
//import org.web3j.utils.Convert;
//import org.web3j.utils.Numeric;

/**
 * Created by liuming on 05/05/2018.
 */

public class BaseActivity extends AppCompatActivity implements RequestResult {
    public static int REQUEST_PERMISSION_WRITE_STORAGE = 0;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    public static final int SCAN_REQUEST_CODE = 10;
    private static final int MY_PERMISSIONS_REQUEST = 20;

    private TextView tvTitle;
    ;
    private ImageView ivBack;
    private ImageView ivRight;
    private Button btnDone;
    private LinearLayout ll_Select;
    private ImageView ivDate;
    private ImageView ivSearch;

    protected ProgressDialog mProgressDialog;
    private int mMaxSize = 800;
    private float mAspectRatio = 1;

    private File selFile = null;
    private Uri mPhotoUri;
    private Uri uritempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLanguage();
        initView();
    }

    private void setupLanguage() {
        Resources resources = getResources();//获得res资源对象

        Configuration config = resources.getConfiguration();//获得设置对象

        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。

        // //简体中文
        if (UserInfoManager.getInst().getLanguage() == 1) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        //简体中文

        resources.updateConfiguration(config, dm);
    }

    protected void initView() {
        View v = (View) getLayoutInflater().inflate(R.layout.cusom_toolbar, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        //getSupportActionBar().setCustomView(v);

        //ButterKnife.bind(this, v);
        tvTitle = v.findViewById(R.id.textView_title);
        ivBack = v.findViewById(R.id.imageView_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnDone = v.findViewById(R.id.button_done);
        btnDone.setVisibility(View.GONE);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDone();
            }
        });

        ivSearch = v.findViewById(R.id.imageView_search);
        ivSearch.setVisibility(View.GONE);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearch();
            }
        });

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.FILL_VERTICAL | Gravity.FILL_HORIZONTAL);

        getSupportActionBar().setCustomView(v, params);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        Toolbar parent = (Toolbar) v.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);
        //getSupportActionBar().content

//        val parent = supportActionBar?.customView?.parent as Toolbar
//        parent?.setPadding(0, 0, 0, 0)//for tab otherwise give space in tab
//        parent?.setContentInsetsAbsolute(0, 0)
    }

    protected void setTitle(String title) {
        tvTitle.setText(title);
    }

    protected void hideActionbar() {
        getSupportActionBar().hide();
    }

    protected void showActionbar(String title) {
        getSupportActionBar().show();
        tvTitle.setText(title);
    }

    protected void changeDoneTitle(String title) {
        btnDone.setText(title);
    }

    protected void reset() {
        ivBack.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
        ll_Select.setVisibility(View.GONE);
        ivDate.setVisibility(View.GONE);
        ivSearch.setVisibility(View.GONE);
    }

    protected void showBackButton() {
        ivBack.setVisibility(View.VISIBLE);
    }

    protected void showDone() {
        btnDone.setVisibility(View.VISIBLE);
    }

    protected void hideDone() {
        btnDone.setVisibility(View.GONE);
    }

    protected void onDone() {
    }

    protected void showSearch() {
        ivSearch.setVisibility(View.VISIBLE);
    }

    protected void onSearch() {

    }

    public void showWaiting() {
        mProgressDialog = ProgressDialog.show(BaseActivity.this, "", getResources().getString(R.string.waiting), true);
    }

    public void hideWaiting() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showToastMessage(String message) {
        if (message == null || message.length() == 0) {
            // message = getString(R.string.error_);
            message = "error";
        }
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        hideWaiting();
        try {
            int code = jsonObject.getInt("code");
            if (code != 0) {
                String msg = jsonObject.getString("msg");
                showToastMessage(msg);
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onFailure(String szValue, String url) {
        LogUtil.INSTANCE.i("request error: " + szValue + " and url " + url);
        hideWaiting();
        showToastMessage(szValue);
    }

    protected boolean checkInputs(EditText... args) {
        boolean pass = true;
        for (int i = 0; i < args.length; ++i) {
            EditText et = args[i];
            if (et.getText().toString() == null || et.getText().toString().trim().length() <= 0) {
                pass = false;
                break;
            }
        }
        if (!pass) {
            showToastMessage(getString(R.string.input_check_failed));
        }
        return pass;
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }


    private void doAdd() {
        new AlertDialog.Builder(this)
                .setTitle("头像设置")
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        // 调用系统的拍照功能
                        if (!checkPermission(Manifest.permission.CAMERA)) {
                            Toast.makeText(BaseActivity.this, "请打开相机权限", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(BaseActivity.this, "请打开读写权限", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new ContentValues());
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
//                        if (!checkStoragePermission()) {
//                            Toast.makeText(BaseActivity.this, "请打开读写权限", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
            }
        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case PHOTO_REQUEST_TAKEPHOTO:
//                    startPhotoZoom(mPhotoUri, mMaxSize);
//                    break;
//
//                case PHOTO_REQUEST_GALLERY:
//                    if (data != null) {
//                        startPhotoZoom(data.getData(), mMaxSize);
//                    }
//                    break;
//                case PHOTO_REQUEST_CUT:
//                    try {
//                        selFile = new File(new URI(uritempFile.toString()));
////                        if (mAspectRatio > 1.1) {
////                            Bitmap bitmap = getSmallBitmap(selFile.getPath());
////                            if (bitmap != null) {
////                                String path = savebitmap("small_new.jpg", bitmap);
////                                selFile = new File(path);
////                            }
////                        }
//                        if (selFile != null) {
//                            Bitmap bitmap = BitmapFactory.decodeFile(selFile.getPath());
//                           // iconImageView.setImageBitmap(bitmap);
//                            //Picasso.with(SettingActivityiw.this).load(selFile.getPath()).into(iconImageView);
//                        }
//
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//            }
//        }
//    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 0.99);

        // outputX,outputY 是剪裁图片的宽高
        if (mAspectRatio > 1.1) {

        } else {
            intent.putExtra("outputX", mMaxSize);
            intent.putExtra("outputY", mMaxSize);
        }

        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        //intent.putExtra("return-data", true);

        //uritempFile为Uri类变量，实例化uritempFile
        uritempFile = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    protected void scanCode() {
        if (!checkPermission(Manifest.permission.CAMERA)) {
            Toast.makeText(this, "请打开相机权限", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityForResult(new Intent(this, CaptureActivity.class), SCAN_REQUEST_CODE);

//        Intent intent = new Intent(this, CaptureActivity.class);
//        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    protected boolean checkAddress(String barcode) {
        try {
            JSONObject jsonObject = new JSONObject(barcode);
            int bitpa = jsonObject.getInt("BipaWallet");
            return bitpa == 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showToastMessage(getResources().getString(R.string.address_wrong));
        return false;
    }

    protected String retrieveAddress(String barcode) {
        try {
            JSONObject jsonObject = new JSONObject(barcode);
            return jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void scanDone(String barcode) {

    }

    public boolean checkMail(String mail) {
        if (!Common.emailValidation(mail)) {
            Toast.makeText(BaseActivity.this, getString(R.string.mail_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean checkPhone(String phone) {
        if (!Common.phoneValidation(phone)) {
            Toast.makeText(BaseActivity.this, getString(R.string.phone_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String handlePhone(String phone) {
        String result = "";
        int length = phone.length();
        for (int i = 0; i < length; ++i) {
            char c = phone.charAt(i);
            if (c >= 48 && c <= 57) {
                result += c;
            }
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get selected images from selector
        if (requestCode == SCAN_REQUEST_CODE) {
            if (data != null) {
                // Toast.makeText(mContext,data.getStringExtra("barCode"),Toast.LENGTH_LONG).show();
                String barcode = data.getStringExtra("barCode");
                scanDone(barcode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void gotoWebView(String url) {
        Intent i = new Intent(this, WebviewActivity.class);
        i.putExtra("url", url);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        if (null != mCompositeDisposable) {
            mCompositeDisposable.clear();
        }
        super.onDestroy();
    }

    private ActivityComponent mActivityComponent;

    public ActivityComponent getActivityComponent() {
        if (null == mActivityComponent) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .appComponent(HZApplication.get(this).getAppComponent())
                    .build();
        }

        return mActivityComponent;
    }

    private CompositeDisposable mCompositeDisposable;

    public void addDisposable(Disposable d) {
        if (null == mCompositeDisposable) {
            mCompositeDisposable = new CompositeDisposable();
        }

        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.add(d);
        }
    }

    public void copyText(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(content);
        Toast.makeText(this, getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
    }

    public void showKeyboard(boolean isShow, View input) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null || input != null) {
                imm.hideSoftInputFromWindow(input != null ? input.getWindowToken() : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

}
