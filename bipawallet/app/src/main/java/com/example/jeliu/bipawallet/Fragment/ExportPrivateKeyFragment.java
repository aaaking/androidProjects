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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.textView_key)
    TextView tvKey;

    @BindView(R.id.textView_safe_key)
    TextView tvSafeKey;

    @BindView(R.id.imageView_qr)
    ImageView ivQr;
    @BindView(R.id.imageView_safe_qr)
    ImageView ivSafeQr;

    @BindView(R.id.tabHost)
    TabHost tabHost;

    @BindView(R.id.tv_transfer)
    TextView tvTransfer;

    @BindView(R.id.tab1)
    ScrollView tab1;

    @BindView(R.id.tv_transfer_tip)
    TextView tvTransferTip;

    @BindView(R.id.raw_credential_container)
    View raw_credential_container;

    @BindView(R.id.raw_qrcode_container)
    View raw_qrcode_container;

    @BindView(R.id.switch_raw_data)
    Switch switch_raw_data;

    @BindView(R.id.switch_raw_qr)
    Switch switch_raw_qr;


    @OnClick(R.id.button_copy)
    void onCopy() {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(privateKey);
        Toast.makeText(getActivity(), getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_copy_safe)
    void onCopySafe() {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(safePrivateKey);
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
        ButterKnife.bind(this, view);
        if (privateKey != null) {
            tvKey.setText(privateKey);
            tvKey.setTextIsSelectable(true);
            tvKey.setMovementMethod(new ScrollingMovementMethod());
        }
        if (safePrivateKey != null) {
            tvSafeKey.setText(safePrivateKey);
            tvKey.setTextIsSelectable(true);
            tvSafeKey.setMovementMethod(new ScrollingMovementMethod());
        }
        initTabhost();
        initQRCode();
        handleDiffer();
        tab1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvKey.getParent().requestDisallowInterceptTouchEvent(false);
                tvSafeKey.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        tvKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvKey.getParent().requestDisallowInterceptTouchEvent(true);
                tvSafeKey.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        tvSafeKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvKey.getParent().requestDisallowInterceptTouchEvent(true);
                tvSafeKey.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setSwitchAction();
        return view;
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

    private void initTabhost() {
        tabHost.setup();            //初始化TabHost容器

        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        if (showType == TYPE_SHOW_ETH_KS) {
            tabHost.addTab(tabHost.newTabSpec("key").setIndicator("keystore", null).setContent(R.id.tab1));
        } else if (showType == TYPE_SHOW_ETH_PK) {
            tabHost.addTab(tabHost.newTabSpec("key").setIndicator(getResources().getString(R.string.privacy_key), null).setContent(R.id.tab1));

        }
        tabHost.addTab(tabHost.newTabSpec("code").setIndicator(getResources().getString(R.string.qrcode), null).setContent(R.id.tab2));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equalsIgnoreCase("key")) {

                } else {

                }
            }
        });
    }

    private void initQRCode() {
        try {
            // generate a 150x150 QR code
            Bitmap bm = Common.encodeAsBitmap(privateKey, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap safeBm = Common.encodeAsBitmap(safePrivateKey, BarcodeFormat.QR_CODE, 200, 200);

            if (bm != null) {
                ivQr.setImageBitmap(bm);
            }
            if (safeBm != null) {
                ivSafeQr.setImageBitmap(safeBm);
            }
        } catch (WriterException e) { //eek
        }
    }

    private void handleDiffer() {
        if (showType == TYPE_SHOW_ETH_KS) {
            tvTransfer.setText(getString(R.string.password_case));
            tvTransferTip.setText(getString(R.string.password_case_tip));
        } else if (showType == TYPE_SHOW_ETH_PK) {
            tvTransfer.setText(getString(R.string.no_network_transfer));
            tvTransferTip.setText(getString(R.string.no_network_transfer_tip));
        }
    }

    public void setPrivateKey(String key, int type) {
        privateKey = key;
        this.showType = type;
    }

    public void setSafePrivateKey(String key, int type) {
        safePrivateKey = key;
        this.showType = type;
    }
}
