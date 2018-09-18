package com.example.jeliu.eos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.ui.WALLET_EOS
import com.example.jeliu.eos.crypto.ec.EosPrivateKey
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ac_import_eoswallet.*
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/17.
 */
fun startImportEosWalletAC(activity: Activity) {
    activity.startActivity(Intent(activity, ImportEosWalletAC::class.java))
}

class ImportEosWalletAC : BaseActivity() {
    var walletName: String = ""
    var accountName: String = ""
    @Inject
    lateinit var mDataManager: EoscDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setTitle(resources.getString(R.string.import_eos_wallet))
        setContentView(R.layout.ac_import_eoswallet)
        text_privacy.setOnClickListener { gotoWebView("http://47.52.224.7:8081") }
        button_import_key.setOnClickListener {
            if (!checkInputs(editText_name_store, et_key, et_key_password)) {
                return@setOnClickListener
            }
            if (!rb_key.isChecked) {
                showToastMessage(getString(R.string.agree_service_privacy_policy))
                return@setOnClickListener
            }
            showWaiting()
            getAccountByPk(et_key.text.toString())
        }
    }

    fun createWallet() {
        var walletPwd = et_key_password.text.toString()
        walletName = editText_name_store.text.toString()
        addDisposable(
                Observable.fromCallable { mDataManager.walletManager.create(walletName, walletPwd) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : RxCallbackWrapper<String>(this) {
                            override fun onNext(pw: String) {
                                importKey()
                            }
                            override fun onError(e: Throwable) {
                                super.onError(e)
                                showToastMessage(e.toString())
                            }
                        })
        )
    }

    fun getAccountByPk(pk: String) {
        var eosPK = EosPrivateKey(pk)
        var public_key = eosPK.publicKey.toString()
        addDisposable(mDataManager
                .getKeyAccounts(public_key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : RxCallbackWrapper<JsonObject>(this) {
                    override fun onNext(result: JsonObject) {//{"account_names":["ghhrag"]}
                        if (result != null) {
                            var array = result.getAsJsonArray("account_names")
                            if (array != null && array.size() > 0) {
                                accountName = array[0].toString()
                                createWallet()
                            }
                        }
                    }
                    override fun onError(e: Throwable) {
                        super.onError(e)
                        showToastMessage(e.toString())
                    }
                })
        )
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