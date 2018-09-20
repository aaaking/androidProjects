package com.example.jeliu.bipawallet.Asset;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_EOS;
import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;

/**
 * Created by liuming on 20/05/2018.
 */

public class WithdrawActivity extends BaseActivity {
    @BindView(R.id.imageView_qr)
    ImageView ivQr;

    @BindView(R.id.textView_name)
    TextView tvName;

    @BindView(R.id.textView_address)
    TextView tvAddress;

    @BindView(R.id.editText_value)
    EditText etValue;

    @BindView(R.id.spinner_token)
    Spinner spinnerToken;

    @BindView(R.id.imageView_profile)
    ImageView ivProfile;

    @BindView(R.id.ll_container)
    LinearLayout llContainer;

    private String content;
    private JSONArray tokenList;
    private String token;

    @OnClick(R.id.button_copy)
    void onCopy() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {
            cm.setText(address);
            Toast.makeText(this, getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.copy_address_error), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);

        setTitle("");
        showBackButton();
        showDone();
        changeDoneTitle(getString(R.string.share));
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        initQRCode();
        setupView();
        if (WalletUtils.isValidAddress(UserInfoManager.getInst().getCurrentWalletAddress())) {
            loadEthData();
        } else {
            updateToken();
        }
    }

    private void loadEthData() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.TOKENLIST_URL, null, this);
    }

    protected void onDone() {
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, getString(R.string.open_storage), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkInputs(etValue)) {
            return;
        }

        String imagePath = Common.takeViewScreenShot(this, llContainer);
        if (imagePath != null) {
            String imageUri = Common.insertImageToSystem(this, imagePath);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //shareIntent.putExtra(Intent.EXTRA_TEXT, "一些文字");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri));
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, ""));
        } else {
            showToastMessage("share error");
        }
    }

    private void updateToken() {
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        int currentIndex = 0;
        ArrayList<String> temp = new ArrayList<String>();
        if (wallet == null || wallet.type == WALLET_ETH) {
            for (int i = 0; i < tokenList.length(); ++i) {
                String token = tokenList.optString(i);
                temp.add(token);
                if (token.equalsIgnoreCase(this.token)) {
                    currentIndex = i;
                }
            }
            if (!temp.contains("eth")) {
                temp.add(0, "eth");
            }
        } else if (wallet.type == WALLET_EOS) {
            temp.add("eos");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, temp);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        spinnerToken.setAdapter(adapter);
        spinnerToken.setSelection(currentIndex);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        if (url.contains(Constant.TOKENLIST_URL)) {
            try {
                tokenList = jsonObject.getJSONArray("tokenlist");
                updateToken();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private void setupView() {
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {
            tvName.setText(UserInfoManager.getInst().getCurrentWalletName());
            tvAddress.setText("ID: " + address);
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            if (wallet != null) {
                ivProfile.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
            }
        }
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                initQRCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spinnerToken.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initQRCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initQRCode() {
        try {
            String address = UserInfoManager.getInst().getCurrentWalletAddress();
            // generate a 150x150 QR code
            String value = etValue.getText().toString();
            float iv = 0;
            if (value.length() > 0)
                iv = Float.valueOf(value);
            String token = "";
            int position = spinnerToken.getSelectedItemPosition();

            if (position >= 0) {
                token = spinnerToken.getAdapter().getItem(position).toString();
            }

            content = String.format("{\"id\":\"%s\", \"value\":%.06f, \"token\":\"%s\", \"BipaWallet\":1}", address, iv, token);
            Bitmap bm = Common.encodeAsBitmap(content, BarcodeFormat.QR_CODE, 190, 190);

            if (bm != null) {
                ivQr.setImageBitmap(bm);
            }
        } catch (WriterException e) { //eek
        }
    }
}
