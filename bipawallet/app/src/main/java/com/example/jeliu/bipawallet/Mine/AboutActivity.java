package com.example.jeliu.bipawallet.Mine;

import android.os.Bundle;
import android.view.View;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.UpdateManager;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 19/05/2018.
 */

public class AboutActivity extends BaseActivity {
    private UpdateManager mUpdateManager;

    @OnClick({R.id.rl_agreement, R.id.rl_privacy, R.id.rl_version, R.id.rl_update}) void onClick(View view) {
        if (view.getId() == R.id.rl_agreement) {
            gotoWebView("http://47.52.224.7:8081");
        } else if (view.getId() == R.id.rl_privacy) {
            gotoWebView("http://47.52.224.7:8081");
        } else if (view.getId() == R.id.rl_version) {
            gotoWebView(Common.getEditionUrl());
        } else if (view.getId() == R.id.rl_update) {
            checkUpdate();
        }
    }

    private void checkUpdate() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        request.requestGet(Constant.APKVERSION, param, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abount_activity);
        ButterKnife.bind(this);

        setTitle(getString(R.string.about_us));
        showBackButton();
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            String version = data.getString("version");
            String currentVersion = Common.getVersion();
            if (version.compareToIgnoreCase(currentVersion) > 0) {
                String apkUrl = data.getString("url");
                if (apkUrl != null && apkUrl.length() == 0) {
                    mUpdateManager = new UpdateManager(this);
                    mUpdateManager.download(apkUrl);
                }
            } else {
                showToastMessage(getString(R.string.version_latest));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
