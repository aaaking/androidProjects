package com.example.jeliu.bipawallet.Fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 19/05/2018.
 */

public class ExportPrivateKeyFragment extends DialogFragment {
    private String privateKey;
    private String safePrivateKey;
    private boolean forKeyStore;

    @BindView(R.id.textView_key) TextView tvKey;
    @BindView(R.id.textView_safe_key) TextView tvSafeKey;

    @BindView(R.id.imageView_qr) ImageView ivQr;
    @BindView(R.id.imageView_safe_qr) ImageView ivSafeQr;

    @BindView(R.id.tabHost)
    TabHost tabHost;

    @BindView(R.id.tv_transfer)
    TextView tvTransfer;

    @BindView(R.id.tv_transfer_tip)
    TextView tvTransferTip;


    @OnClick(R.id.button_copy) void onCopy() {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(privateKey);
        Toast.makeText(getActivity(), getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_private_key, container);
        ButterKnife.bind(this, view);
        if (privateKey != null) {
            tvKey.setText(privateKey);
            //tvKey.setTextIsSelectable(true);
            tvKey.setMovementMethod(new ScrollingMovementMethod());
        }
        if (safePrivateKey != null) {
            tvSafeKey.setText(safePrivateKey);
            //tvKey.setTextIsSelectable(true);
            tvSafeKey.setMovementMethod(new ScrollingMovementMethod());
        }
        initTabhost();
        initQRCode();
        handleDiffer();
        return view;
    }

    private void initTabhost() {
        tabHost.setup();            //初始化TabHost容器

        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        if (forKeyStore) {
            tabHost.addTab(tabHost.newTabSpec("key").setIndicator("keystore",null).setContent(R.id.tab1));
        } else {
            tabHost.addTab(tabHost.newTabSpec("key").setIndicator(getResources().getString(R.string.privacy_key),null).setContent(R.id.tab1));

        }
        tabHost.addTab(tabHost.newTabSpec("code").setIndicator(getResources().getString(R.string.qrcode),null).setContent(R.id.tab2));

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

            if(bm != null) {
                ivQr.setImageBitmap(bm);
            }
            if (safeBm != null) {
                ivSafeQr.setImageBitmap(safeBm);
            }
        } catch (WriterException e) { //eek
        }
    }

    private void handleDiffer() {
        if (forKeyStore) {
            tvTransfer.setText(getString(R.string.password_case));
            tvTransferTip.setText(getString(R.string.password_case_tip));
        } else {
            tvTransfer.setText(getString(R.string.no_network_transfer));
            tvTransferTip.setText(getString(R.string.no_network_transfer_tip));
        }
    }

    public void setPrivateKey(String key, boolean forKeyStore) {
        privateKey = key;
        this.forKeyStore = forKeyStore;
    }

    public void setSafePrivateKey(String key, boolean forKeyStore) {
        safePrivateKey = key;
        this.forKeyStore = forKeyStore;
    }
}
