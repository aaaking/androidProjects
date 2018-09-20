package com.example.jeliu.bipawallet.Asset

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.R

/**
 * Created by 周智慧 on 2018/9/20.
 */
fun startTransportEosAC(activity: Activity?) = activity?.startActivity(Intent(activity, TransportEosAC::class.java))
class TransportEosAC : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_transport_eos)
    }
}