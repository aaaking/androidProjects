package com.example.jeliu.bipawallet.Asset;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.Network.IWallet;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 09/05/2018.
 */

public class ImportWalletActivity extends BaseActivity implements IWallet {
    @BindView(R.id.et_keystore)
    EditText etKeyStore;

    @BindView(R.id.et_keystore_password)
    EditText etKeyStorePassword;

    @BindView(R.id.et_key)
    EditText etKey;

    @BindView(R.id.et_key_password)
    EditText etKeyPassword;

    @BindView(R.id.radioButton_keystore)
    CheckBox rbKeyStore;

    @BindView(R.id.rb_key)
    CheckBox rbKey;

    @BindView(R.id.imageView_eye_store)
    ImageView ivEyeStore;
    //
    @BindView(R.id.imageView_key_eye)
    ImageView ivKeyEye;

    @BindView(R.id.editText_name_store)
    EditText etStoreName;

    @BindView(R.id.editText_name_key)
    EditText etKeyName;

    @OnClick({R.id.textView_privacy, R.id.text_privacy_key})
    void onPrivacy() {
        gotoWebView("http://47.52.224.7:8081");
    }

    private boolean passwordShown;
    private boolean passwordShownKey;

    @OnClick({R.id.imageView_question, R.id.btn_keystore})
    void onQuestion() {
        gotoWebView(Common.getCenterUrl() + "#question4");
    }

    @OnClick({R.id.iv_key_question, R.id.btn_key})
    void onQuestionKey() {
        gotoWebView(Common.getCenterUrl() + "#question3");
    }

    @OnClick(R.id.imageView_eye_store)
    void onEye() {
        passwordShown = !passwordShown;
        if (passwordShown) {
            etKeyStorePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivEyeStore.setImageDrawable(getResources().getDrawable(R.drawable.open));
        } else {
            etKeyStorePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivEyeStore.setImageDrawable(getResources().getDrawable(R.drawable.close));
        }
    }

    //
    @OnClick(R.id.imageView_key_eye)
    void onKeyEye() {
        passwordShownKey = !passwordShownKey;
        if (passwordShownKey) {
            etKeyPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivKeyEye.setImageDrawable(getResources().getDrawable(R.drawable.open));
        } else {
            etKeyPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivKeyEye.setImageDrawable(getResources().getDrawable(R.drawable.close));
        }
    }


    @OnClick({R.id.btn_scan_key, R.id.btn_scan_store})
    void onScanKey(View view) {
        if (view.getId() == R.id.btn_scan_key) {
            scanForKey = true;
        } else {
            scanForKey = false;
        }
        scanCode();
    }

    private boolean scanForKey;

    @OnClick({R.id.button_import_key, R.id.button_import_keystore})
    void onClick(View view) {
        String pwd = "";
        if (view.getId() == R.id.button_import_key) {
            if (!checkInputs(etKey, etKeyPassword, etKeyName)) {
                return;
            }
            if (!rbKey.isChecked()) {
                showToastMessage("请同意服务及隐私条款");
                return;
            }
            pwd = etKeyPassword.getText().toString();
//            HZHttpRequest request = new HZHttpRequest();
//            Map<String, String> param = new HashMap<>();
//            param.put("privatekey", etKey.getText().toString());
//            param.put("password", etKeyPassword.getText().toString());
////            param.put("privatekey", "b77005482c476a3e19c2f3353d5d0edd550439c13bdbcb5c8a61b326ec6efc71");
////            param.put("password", "chenjianlin7");
//            showWaiting();
//            request.requestPost(Constant.IMPORT_PRIVATE_KEY_URL, param, this);
        } else {
            if (!checkInputs(etKeyStore, etKeyStorePassword, etStoreName)) {
                return;
            }
            if (!rbKeyStore.isChecked()) {
                showToastMessage("请同意服务及隐私条款");
                return;
            }
            pwd = etKeyStorePassword.getText().toString();
//            HZHttpRequest request = new HZHttpRequest();
//            Map<String, String> param = new HashMap<>();
//            param.put("keystore", etKeyStore.getText().toString());
//            param.put("password", etKeyStorePassword.getText().toString());
//            showWaiting();
//            request.requestPost(Constant.IMPORT_KEYSTORE_URL, param, this);
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_STORAGE);
        } else {
            loadWallet(th.getCurrentTab(), pwd);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请开启文件读取权限", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "已经开启文件读取权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadWallet(int way, String pwd) {
        showWaiting();
        Credentials credentials = way == 0 ? Common.loadWalletByKeyStore(pwd, etKeyStore.getText().toString(), this) : Common.loadWalletByPrivateKey(pwd, etKey.getText().toString(), this);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
//        if (!super.onSuccess(jsonObject, url)) {
//            return true;
//        }
//        if (url == Constant.IMPORT_PRIVATE_KEY_URL || url == Constant.IMPORT_KEYSTORE_URL) {
//            try {
//                String address = jsonObject.getString("address");
//                if (address != null) {
//                    String name = "";
//                    if (url == Constant.IMPORT_PRIVATE_KEY_URL) {
//                        name = etKeyName.getText().toString();
//                    } else {
//                        name = etStoreName.getText().toString();
//                    }
//                    UserInfoManager.getInst().insertWallet(name, address, 0);
//                    UserInfoManager.getInst().setCurrentWalletAddress(address);
//                    setResult(RESULT_OK);
//                    finish();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return true;
    }

    private TabHost th;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        ButterKnife.bind(this);

        setTitle(getString(R.string.importWallet));
        showBackButton();

        th = (TabHost) findViewById(R.id.tabhost);
        setupTabHost();
    }

    private void setupTabHost() {
        th.setup();            //初始化TabHost容器

        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        th.addTab(th.newTabSpec("tabKeystore").setIndicator(getResources().getString(R.string.ks_wallet), null).setContent(R.id.tab1));
        th.addTab(th.newTabSpec("tabKey").setIndicator(getResources().getString(R.string.privacy_key), null).setContent(R.id.tab2));

        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equalsIgnoreCase("tabKeystore")) {
                } else {
                }
            }
        });

    }

    protected void scanDone(String barcode) {
        if (scanForKey) {
            etKeyStore.setText(barcode);
        } else {
            etKey.setText(barcode);
        }
    }

    @Override
    public void onWalletResult(Credentials credentials) {
        if (credentials != null) {
            String address = credentials.getAddress();
            if (address != null) {
                hideWaiting();
                String name = etKeyName.getText().toString();
                UserInfoManager.getInst().insertWallet(name, address, 0);
                UserInfoManager.getInst().setCurrentWalletAddress(address);
                setResult(RESULT_OK);
                finish();
            } else {
                hideWaiting();
            }
        } else {
            hideWaiting();
        }
    }
}
