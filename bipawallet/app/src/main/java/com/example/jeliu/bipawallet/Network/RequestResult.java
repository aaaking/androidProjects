package com.example.jeliu.bipawallet.Network;

import org.json.JSONObject;

/**
 * Created by jeliu on 4/7/18.
 */

public interface RequestResult {
    boolean onSuccess(JSONObject jsonObject, String url);
    void onFailure(String szValue, String url);
}
