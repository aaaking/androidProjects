package com.example.jeliu.eos.data.remote.model.api;

import android.text.TextUtils;

import com.example.jeliu.eos.crypto.digest.Sha256;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2018-01-10.
 */

public class GetCodeResponse {
    @Expose
    public String account_name;

    @Expose
    public String wast;

    @Expose
    public String code_hash;

    @Expose
    public JsonObject abi;

    public boolean isValidCode() {
        return !(TextUtils.isEmpty(code_hash) || Sha256.ZERO_HASH.toString().equals(code_hash));
    }
}
