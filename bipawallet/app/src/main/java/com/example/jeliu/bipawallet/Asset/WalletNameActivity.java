package com.example.jeliu.bipawallet.Asset;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Fragment.ExportPrivateKeyFragment;
import com.example.jeliu.bipawallet.Model.HZToken;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Splash.WelcomeActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 06/05/2018.
 */

public class WalletNameActivity extends BaseActivity {

    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    @BindView(R.id.textview_name)
    TextView tvName;

    @BindView(R.id.textview_address)
    TextView tvAddress;

    @BindView(R.id.textView_money)
    TextView tvMoney;

    @BindView(R.id.imageView_profile)
    ImageView ivProfile;

    private String walletAddress;
    private AlertDialog alertDialog;

    @OnClick({R.id.rl_private_key, R.id.rl_keystore, R.id.button_delete}) void onClick(View view) {
        if (view.getId() == R.id.rl_private_key) {
            showInputPassword(0);
        } else if (view.getId() == R.id.rl_keystore) {
            showInputPassword(1);
        } else {
            showInputPassword(2);
        }
    }

    private void deleteWallet(String password) {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        param.put("address", walletAddress);
        param.put("password", password);
        request.requestPost(Constant.DELETEACCOUNT, param, this);
    }

    private void deleteWalletImp() {
        UserInfoManager.getInst().deleteWallet(walletAddress);
        JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
        if (jsonArray.length() == 0) {
            Intent intent = new Intent(WalletNameActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    //flag 0 : export private key
    // 1 : export key store
    // 2 : delete
    private void showInputPassword(final int flag) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(
                R.layout.layout_input_password, null);
        final EditText etPassword = (EditText)textEntryView.findViewById(R.id.editText_password);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setView(textEntryView);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String password = etPassword.getText().toString();
                        if (flag == 0) {
                            exportPrivateKey(password);
                        } else if (flag == 1) {
                            exportKeystore(password);
                        } else if (flag == 2) {
                            //delete
                            deleteWallet(password);
                        }
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //setTitle("");
                    }
                });
        builder.show();
    }

    private void exportPrivateKey(String password) {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        param.put("address", walletAddress);
        param.put("password", password);
        request.requestPost(Constant.GET_PRIVATEKEY, param, this);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }

        if (url.contains(Constant.GET_PRIVATEKEY)) {
            ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
            try {
                String privateKey = jsonObject.getString("privatekey");
                fragment.setPrivateKey(privateKey, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
        } else if (url.contains(Constant.GET_KEYSTORE)) {
            ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
            try {
                String privateKey = jsonObject.getString("keystore");
                fragment.setPrivateKey(privateKey, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
        } else if (url.contains(Constant.BALANCE_URL)) {
            try {
                JSONArray balance = jsonObject.getJSONArray("balance");
                HZWalletManager.getInst().updateWalletInfo(walletAddress, balance);
                updateUI(walletAddress, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(Constant.DELETEACCOUNT)) {
            deleteWalletImp();
        }
        return false;
    }

    private void showPay() {
        int gravity = Gravity.BOTTOM;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_popup_pay, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        popupWindow.showAtLocation(llRoot, gravity, 0, 0);
    }

    private void exportKeystore(String password) {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        param.put("password", password);
        param.put("address", walletAddress);
        request.requestPost(Constant.GET_KEYSTORE, param, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_wallet_name);

        ButterKnife.bind(this);
        setupView();
    }

    private void updateUI(String address, boolean shouldRefresh) {
        HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (wallet != null) {
            if (wallet.tokenList.size() == 0 && shouldRefresh) {
                showWaiting();
                HZHttpRequest request = new HZHttpRequest();
                request.requestGet(Constant.BALANCE_URL + "?address="+address, null, this);
            } else {
                double total = 0;
                for (HZToken token : wallet.tokenList) {
                    double value = token.value;
                    total += value;
                }
                tvMoney.setText(""+total);
            }
        }
    }

    void setupView() {
        showBackButton();

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        setTitle(name);
        walletAddress = i.getStringExtra("address");
        if (walletAddress != null) {
            HZWallet wallet = HZWalletManager.getInst().getWallet(walletAddress);
            if (wallet != null) {
                ivProfile.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
            }
        }

        updateUI(walletAddress, true);

        tvName.setText(name);
        tvAddress.setText(walletAddress);
    }
}