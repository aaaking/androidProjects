package com.example.jeliu.bipawallet.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.jeliu.bipawallet.Asset.CreateWalletActivity;
import com.example.jeliu.bipawallet.Asset.ImportWalletActivity;
import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Main.NavActivity;
import com.example.jeliu.bipawallet.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 06/07/2018.
 */

public class WelcomeActivity extends BaseActivity {
    @OnClick({R.id.btn_create, R.id.btn_import}) void onClick(View view) {
        //Intent i = new Intent(WelcomeActivity.this, NavActivity.class);
        if (view.getId() == R.id.btn_import) {
            Intent i = new Intent(WelcomeActivity.this, ImportWalletActivity.class);
            //i.putExtra("FromSplash", true);
            //startActivity(i);
            startActivityForResult(i, Constant.import_wallet_request_code);
        } else {
            Intent i = new Intent(WelcomeActivity.this, CreateWalletActivity.class);
            //i.putExtra("FromSplash", true);
            startActivityForResult(i, Constant.create_wallet_request_code);
            //startActivity(i);
        }
        //finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        hideActionbar();
        requestPermission();

//        setTitle(getString(R.string.create));
//        showBackButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get selected images from selector
        if(requestCode == Constant.create_wallet_request_code || requestCode == Constant.import_wallet_request_code || requestCode == Constant.manage_wallet_request_code) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(WelcomeActivity.this, NavActivity.class);
                if (getIntent().getStringExtra("js") != null) {
                    i.putExtra("js", getIntent().getStringExtra("js"));
                }
                startActivity(i);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
