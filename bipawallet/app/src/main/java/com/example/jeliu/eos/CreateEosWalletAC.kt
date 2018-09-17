package com.example.jeliu.eos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.R

/**
 * Created by 周智慧 on 2018/9/17.
 */
fun startCreateEosWalletAC(activity: Activity) {
    activity.startActivity(Intent(activity, CreateEosWalletAC::class.java))
}
class CreateEosWalletAC : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(resources.getString(R.string.create_eos_wallet))
        var tv =  TextView(this)
        tv.text = "create"
        setContentView(tv)
    }
}