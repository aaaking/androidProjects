package com.example.jeliu.bipawallet.Base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Network.RequestResult;
import com.example.jeliu.bipawallet.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuming on 05/05/2018.
 */

public class BaseFragment extends Fragment implements RequestResult {
    public void onSelect(View ancor) {}
    public void onDate() {}

    protected void showWaiting() {
        BaseActivity ba = (BaseActivity)getActivity();
        if (ba != null) {
            ba.showWaiting();
        }
    }

    protected void hideWaiting() {
        BaseActivity ba = (BaseActivity)getActivity();
        if (ba != null) {
            ba.hideWaiting();
        }
    }

    protected void showToastMessage(String message) {
        BaseActivity ba = (BaseActivity)getActivity();
        if (ba != null) {
            ba.showToastMessage(message);
        }
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        BaseActivity ba = (BaseActivity)getActivity();
        if (ba != null) {
            return ba.onSuccess(jsonObject, url);
        }
        return false;
    }

    @Override
    public void onFailure(String szValue, String url) {
        BaseActivity ba = (BaseActivity)getActivity();
        if (ba != null) {
            ba.onFailure(szValue, url);
        }
    }
}
