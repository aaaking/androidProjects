package com.example.jeliu.eos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.ui.WALLET_EOS
import com.example.jeliu.eos.data.EoscDataManager
import kotlinx.android.synthetic.main.ac_import_eoswallet.*
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/17.
 */
fun startCreateEosWalletAC(activity: Activity) {
    activity.startActivity(Intent(activity, CreateEosWalletAC::class.java))
}
class CreateEosWalletAC : BaseActivity() {
    var walletName: String = ""
    var accountName: String = ""
    @Inject
    lateinit var mDataManager: EoscDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setTitle(resources.getString(R.string.create_eos_wallet))
        setContentView(R.layout.ac_create_eoswallet)
    }

    fun importKey() {
        mDataManager.walletManager.importKey(walletName, et_key.text.toString())
        mDataManager.walletManager.saveFile(walletName)
        hideWaiting()
        UserInfoManager.getInst().insertWallet(walletName, accountName, 0, Constant.TAG_EOS_WALLET, WALLET_EOS)
        UserInfoManager.getInst().currentWalletAddress = accountName
        setResult(Activity.RESULT_OK)
        finish()
    }
}