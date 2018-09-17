package com.example.jeliu.eos.data.remote.model.api;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

/**
 * Created by swapnibble on 2017-09-15.
 */

public class GetTableRequest {
    public static final int DEFAULT_FETCH_LIMIT = 10;

    @Expose
    public boolean json = true;

    @Expose
    public String code;

    @Expose
    public String scope;

    @Expose
    public String table;

    @Expose
    public String table_key = "";

    @Expose
    public String lower_bound= "";

    @Expose
    public String upper_bound= "";

    @Expose
    public int limit ;



    public GetTableRequest(String scope, String code, String table, String tableKey, String lowerBound, String upperBound, int limit ) {
        this.scope = scope;
        this.code = code;
        this.table = table;

        this.table_key = TextUtils.isEmpty( tableKey ) ? "" : tableKey;
        this.lower_bound = TextUtils.isEmpty( lowerBound) ? "" : lowerBound;
        this.upper_bound = TextUtils.isEmpty( upperBound) ? "" : upperBound;
        this.limit = limit <= 0 ? DEFAULT_FETCH_LIMIT : limit;
    }
}
