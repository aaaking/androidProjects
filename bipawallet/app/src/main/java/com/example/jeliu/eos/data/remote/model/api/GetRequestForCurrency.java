package com.example.jeliu.eos.data.remote.model.api;

import android.text.TextUtils;

import com.example.jeliu.eos.data.remote.model.types.TypeName;
import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2018-04-16.
 */
public class GetRequestForCurrency {
    @Expose
    protected boolean json = false;

    @Expose
    protected TypeName code;

    @Expose
    protected String symbol;

    public GetRequestForCurrency(String tokenContract, String symbol) {
        this.code = new TypeName(tokenContract);
        this.symbol = TextUtils.isEmpty(symbol) ? null : symbol;
    }
}
