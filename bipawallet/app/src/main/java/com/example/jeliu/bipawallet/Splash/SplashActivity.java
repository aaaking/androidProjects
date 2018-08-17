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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView iv = findViewById(R.id.imageView_splash);
        if (UserInfoManager.getInst().getLanguage() == 1) {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.splash_en));
        } else {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.splash));
        }
        initApp();

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }

    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openMainActivity();
            }
        }, 3000);
    }

    private void openMainActivity() {
        PriceManager.getInst().setup();
        if (UserInfoManager.getInst().isEmptyWallet()) {
            Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, NavActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void initApp() {
        UserInfoManager.getInst();
        HZLocalPhotosManager.getInst();
    }
}
