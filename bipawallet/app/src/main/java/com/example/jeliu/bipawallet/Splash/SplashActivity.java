package com.example.jeliu.bipawallet.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.jeliu.bipawallet.Common.HZLocalPhotosManager;
import com.example.jeliu.bipawallet.Common.PriceManager;
import com.example.jeliu.bipawallet.Main.NavActivity;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

/**
 * Created by liuming on 12/05/2018.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PriceManager.getInst().setup();
        int language = UserInfoManager.getInst().getLanguage();
        if (language == 1) {
            getWindow().setBackgroundDrawableResource(R.drawable.splash_en);
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.splash);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView iv = findViewById(R.id.imageView_splash);
        if (language == 1) {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.splash_en));
        } else {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.splash));
        }
        initApp();

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        new Handler().postDelayed(() -> {
            openMainActivity();
        }, 1500);
    }

    private void openMainActivity() {
        if (UserInfoManager.getInst().isEmptyWallet()) {
            Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
            if (getIntent().getData() != null) {
                i.putExtra("js", getIntent().getData().toString());
            }
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, NavActivity.class);
            if (getIntent().getData() != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("js", getIntent().getData().toString());
            }
            startActivity(i);
            finish();
        }

    }

    private void initApp() {
        UserInfoManager.getInst();
        HZLocalPhotosManager.getInst();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
