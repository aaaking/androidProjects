package com.example.jeliu.bipawallet.Mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.jeliu.bipawallet.Asset.WalletNameActivity;
import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Main.NavActivity;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Splash.SplashActivity;
import com.example.jeliu.bipawallet.Splash.WelcomeActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuming on 05/05/2018.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.spinner_language)
    Spinner spLanguage;


    @BindView(R.id.spinner_currency)
    Spinner spCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setTitle(getString(R.string.system_setting));
        showBackButton();

        setupView();
    }

    private void setupView() {
        String[] items = new String[]{getString(R.string.s_chinese), getString(R.string.s_english)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spLanguage.setAdapter(adapter);

        spLanguage.setSelection(UserInfoManager.getInst().getLanguage());
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (UserInfoManager.getInst().getLanguage() != i) {
                    UserInfoManager.getInst().setLanguage(i);

                    Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        items = new String[]{getResources().getString(R.string.chinese_currency), getResources().getString(R.string.english_currency)};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spCurrency.setAdapter(adapter);


        spCurrency.setSelection(UserInfoManager.getInst().getUsd());
        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UserInfoManager.getInst().setUsd(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
