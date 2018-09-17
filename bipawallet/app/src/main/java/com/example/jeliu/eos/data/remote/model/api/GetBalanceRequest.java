package com.example.jeliu.eos.data.remote.model.api;

import com.example.jeliu.eos.data.remote.model.types.TypeAccountName;
import com.google.gson.annotations.Expose;
/**
 * Created by swapnibble on 2018-04-16.
 */
public class GetBalanceRequest extends GetRequestForCurrency {

    @Expose
    private TypeAccountName account;

    public GetBalanceRequest(String tokenContract, String account, String symbol) {
        super(tokenContract, symbol);
        this.account = new TypeAccountName(account);
    }
}
