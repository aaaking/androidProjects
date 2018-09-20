package com.example.jeliu.bipawallet.Fragment;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.util.LogUtil;
import com.example.jeliu.eos.crypto.ec.EosPublicKey;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.Map;

/**
 * Created by liuming on 19/05/2018.
 */

public class ExportPrivateKeyFragment extends DialogFragment {
    public static int TYPE_SHOW_ETH_PK = 0;
    public static int TYPE_SHOW_ETH_KS = 1;
    public static int TYPE_SHOW_EOS_PK = 2;
    private String privateKey;
    private String safePrivateKey;
    private int showType = TYPE_SHOW_ETH_PK;

    TabHost tabHost;

    TextView textView_key;
    TextView textView_safe_key;
    ImageView imageView_qr;
    ImageView imageView_safe_qr;
    TextView tv_transfer;
    ScrollView scrollView1;
    TextView tv_transfer_tip;
    View raw_credential_container;
    View raw_qrcode_container;
    Switch switch_raw_data;
    Switch switch_raw_qr;

    void copyStr(String str) {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(str);
        Toast.makeText(getActivity(), getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.INSTANCE.i("onCreate");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LogUtil.INSTANCE.i("onCreateDialog");
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        LogUtil.INSTANCE.i("onCreateView");
        View view = inflater.inflate(R.layout.fragment_private_key, container);
        tabHost = view.findViewById(R.id.tabHost);
        ViewStub keyVS = ((ViewStub) view.findViewById(R.id.vs_eth_tab1));
        ViewStub codeVS = ((ViewStub) view.findViewById(R.id.vs_eth_tab2));
        if (showType == TYPE_SHOW_ETH_PK || showType == TYPE_SHOW_ETH_KS) {
            keyVS.setLayoutResource(R.layout.info_eth_key);
            keyVS.inflate();
            codeVS.setLayoutResource(R.layout.info_eth_code);
            codeVS.inflate();
            scrollView1 = view.findViewById(R.id.eth_tab1);
            textView_key = view.findViewById(R.id.textView_key);
            textView_key.setText(privateKey != null ? privateKey : "");
            textView_key.setTextIsSelectable(true);
            textView_key.setMovementMethod(new ScrollingMovementMethod());
            textView_safe_key = view.findViewById(R.id.textView_safe_key);
            textView_safe_key.setText(safePrivateKey != null ? safePrivateKey : "");
            textView_safe_key.setTextIsSelectable(true);
            textView_safe_key.setMovementMethod(new ScrollingMovementMethod());
            imageView_qr = view.findViewById(R.id.imageView_qr);
            imageView_safe_qr = view.findViewById(R.id.imageView_safe_qr);
            tv_transfer = view.findViewById(R.id.tv_transfer);
            tv_transfer_tip = view.findViewById(R.id.tv_transfer_tip);
            raw_credential_container = view.findViewById(R.id.raw_credential_container);
            raw_qrcode_container = view.findViewById(R.id.raw_qrcode_container);
            switch_raw_data = view.findViewById(R.id.switch_raw_data);
            switch_raw_qr = view.findViewById(R.id.switch_raw_qr);
            setScrollListener();
            handleDiffer();
            setSwitchAction();
            initQRCode();
            initEthTabhost();
            view.findViewById(R.id.button_copy).setOnClickListener(it -> copyStr(privateKey));
            view.findViewById(R.id.button_copy_safe).setOnClickListener(it -> copyStr(safePrivateKey));
        } else if (showType == TYPE_SHOW_EOS_PK) {
            keyVS.setLayoutResource(R.layout.info_eos_key);
            keyVS.inflate();
            codeVS.setLayoutResource(R.layout.info_eos_code);
            codeVS.inflate();
            initEosTabhost();
        }
        return view;
    }

    private void setScrollListener() {
        scrollView1.setOnTouchListener((v, event) -> {
            textView_key.getParent().requestDisallowInterceptTouchEvent(false);
            textView_safe_key.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        textView_key.setOnTouchListener((v, event) -> {
            textView_key.getParent().requestDisallowInterceptTouchEvent(true);
            textView_safe_key.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        textView_safe_key.setOnTouchListener((v, event) -> {
            textView_key.getParent().requestDisallowInterceptTouchEvent(true);
            textView_safe_key.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.INSTANCE.i("onStart");
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void setSwitchAction() {
        CompoundButton.OnCheckedChangeListener ls = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == switch_raw_data) {
                    raw_credential_container.setVisibility(switch_raw_data.isChecked() ? View.VISIBLE : View.INVISIBLE);
                } else if (buttonView == switch_raw_qr) {
                    raw_qrcode_container.setVisibility(switch_raw_qr.isChecked() ? View.VISIBLE : View.INVISIBLE);
                }
            }
        };
        switch_raw_data.setOnCheckedChangeListener(ls);
        switch_raw_qr.setOnCheckedChangeListener(ls);
    }

    private void initEthTabhost() {
        tabHost.setup(); //初始化TabHost容器
        if (showType == TYPE_SHOW_ETH_KS) {
            tabHost.addTab(tabHost.newTabSpec("key").setIndicator("keystore", null).setContent(R.id.eth_tab1));
        } else if (showType == TYPE_SHOW_ETH_PK) {
            tabHost.addTab(tabHost.newTabSpec("key").setIndicator(getResources().getString(R.string.privacy_key), null).setContent(R.id.eth_tab1));
        }
        tabHost.addTab(tabHost.newTabSpec("code").setIndicator(getResources().getString(R.string.qrcode), null).setContent(R.id.eth_tab2));
    }

    private void initEosTabhost() {
        tabHost.setup(); //初始化TabHost容器
        tabHost.addTab(tabHost.newTabSpec("key").setIndicator(getResources().getString(R.string.privacy_key), null).setContent(R.id.eos_tab1));
        tabHost.addTab(tabHost.newTabSpec("code").setIndicator(getResources().getString(R.string.qrcode), null).setContent(R.id.eos_tab2));
    }

    private void initQRCode() {
        try {
            // generate a 150x150 QR code
            Bitmap bm = Common.encodeAsBitmap(privateKey, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap safeBm = Common.encodeAsBitmap(safePrivateKey, BarcodeFormat.QR_CODE, 200, 200);
            if (bm != null) {
                imageView_qr.setImageBitmap(bm);
            }
            if (safeBm != null) {
                imageView_safe_qr.setImageBitmap(safeBm);
            }
        } catch (WriterException e) { //eek
        }
    }

    private void handleDiffer() {
        if (showType == TYPE_SHOW_ETH_KS) {
            tv_transfer.setText(getString(R.string.password_case));
            tv_transfer_tip.setText(getString(R.string.password_case_tip));
        } else if (showType == TYPE_SHOW_ETH_PK) {
            tv_transfer.setText(getString(R.string.no_network_transfer));
            tv_transfer_tip.setText(getString(R.string.no_network_transfer_tip));
        }
    }

    public void setPrivateKey(String key) {
        privateKey = key;
    }

    public void setSafePrivateKey(String key) {
        safePrivateKey = key;
    }

    public void setShowType(int type) {
        this.showType = type;
    }

    public void setEosKeys(Map<EosPublicKey, String> data) {

    }
}
