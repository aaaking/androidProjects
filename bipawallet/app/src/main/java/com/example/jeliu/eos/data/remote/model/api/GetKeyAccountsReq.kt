package com.example.jeliu.eos.data.remote.model.api

import com.google.gson.annotations.Expose

/**
 * Created by 周智慧 on 2018/9/17.
 */
class GetKeyAccountsReq {
    @Expose
    private var public_key: String

    constructor (name: String) {
        public_key = name
    }
}