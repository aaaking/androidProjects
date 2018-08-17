package com.example.jeliu.bipawallet.Mine;

import android.os.Bundle;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;

/**
 * Created by jeliu on 7/13/18.
 */

public class MessageCenterActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);

        ButterKnife.bind(this);

        setTitle(getString(R.string.message_center));
        showBackButton();

        loadData();
    }

    private void loadData() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.ANNOUNCEMENT_URL, null, this);
    }


    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        if (url.contains(Constant.ANNOUNCEMENT_URL)) {
//            try {
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } else {

        }
        return false;
    }
}
