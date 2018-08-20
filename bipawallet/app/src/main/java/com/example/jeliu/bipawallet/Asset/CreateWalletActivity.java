package com.example.jeliu.bipawallet.Asset;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.Network.IWallet;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.Webview.WebviewActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 07/05/2018.
 */

public class CreateWalletActivity extends BaseActivity implements IWallet {
    @BindView(R.id.editText_name)
    EditText etName;

    @BindView(R.id.editText_password)
    EditText etPassword;

    @BindView(R.id.checkbox_agree)
    CheckBox checkBox;

    @BindView(R.id.imageView_profile)
    ImageView ivProfile;

    @BindView(R.id.imageView_eye)
    ImageView ivEye;

    private boolean passwordShown;

    @OnClick(R.id.imageView_eye) void onEye() {
        passwordShown = !passwordShown;
        if (passwordShown) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivEye.setImageDrawable(getResources().getDrawable(R.drawable.open));
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivEye.setImageDrawable(getResources().getDrawable(R.drawable.close));
        }
    }

    private int currentProfileIndex = 0;

    @OnClick(R.id.textView_privacy) void onPrivacy() {
        gotoWebView("http://47.52.224.7:8081");
    }

    @OnClick(R.id.imageView_profile) void onChangeProfile() {
        ++ currentProfileIndex;
        int count = UserInfoManager.getInst().getDefaultProfilesCount();
        if (currentProfileIndex >= count) {
            currentProfileIndex = 0;
        }
        int profile = UserInfoManager.getInst().getProfile(currentProfileIndex);
        ivProfile.setImageDrawable(getResources().getDrawable(profile));
    }

    @OnClick(R.id.button_crate) void onCreateWallet() {
        if (!checkInputs(etName, etPassword)) {
            return;
        }
        if (!checkBox.isChecked()) {
            showToastMessage(getString(R.string.radiobutton_failed));
            return;
        }
        String pass = etPassword.getText().toString();
        if (pass.length() < 9) {
            showToastMessage(getString(R.string.min_password));
            return;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_STORAGE);
        } else {
            createWallet(etPassword.getText().toString());
        }
//        HZHttpRequest request = new HZHttpRequest();
//        Map<String, String> param = new HashMap<>();
//        param.put("password", etPassword.getText().toString());
//        request.requestPost(Constant.CREATE_ACCOUNT_URL, param, this);
    }

    public void createWallet(String pwd) {
        showWaiting();
        Credentials credentials = Common.createLocalWallet(etPassword.getText().toString(), this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请开启文件读取权限", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (etPassword.getText().toString().trim().length() > 0) {
                    createWallet(etPassword.getText().toString());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);

        setTitle(getString(R.string.create));
        showBackButton();

        int profile = UserInfoManager.getInst().getProfile(currentProfileIndex);
        ivProfile.setImageDrawable(getResources().getDrawable(profile));
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
//        try {
//            String address = jsonObject.getString("address");
//            String name = etName.getText().toString();
//            UserInfoManager.getInst().insertWallet(name, address, currentProfileIndex);
//            UserInfoManager.getInst().setCurrentWalletAddress(address);
//            setResult(RESULT_OK);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        finish();
        return false;
    }

    @Override
    public void onWalletResult(Credentials credentials) {
        if (credentials != null) {
            String address = credentials.getAddress();
            if (address != null) {
                String name = etName.getText().toString();
                UserInfoManager.getInst().insertWallet(name, address, currentProfileIndex);
                UserInfoManager.getInst().setCurrentWalletAddress(address);
                hideWaiting();
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
