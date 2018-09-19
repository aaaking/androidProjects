package com.example.jeliu.eos

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.ui.WALLET_EOS
import com.example.jeliu.bipawallet.util.LogUtil
import com.example.jeliu.eos.crypto.ec.EosPrivateKey
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ac_create_eoswallet.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/17.
 */
fun startCreateEosWalletAC(activity: Activity) {
    activity.startActivity(Intent(activity, CreateEosWalletAC::class.java))
}

class CreateEosWalletAC : BaseActivity() {
    lateinit var mOwnerKey: EosPrivateKey
    lateinit var mActiveKey: EosPrivateKey
    var walletName: String = ""
    var accountName: String = ""
    @Inject
    lateinit var mDataManager: EoscDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setTitle(resources.getString(R.string.create_eos_wallet))
        setContentView(R.layout.ac_create_eoswallet)
        btn_invite.setOnClickListener {
            startInviteCreateEosAC(this)
        }
        var walletNames = arrayListOf<String>()
        // select wallet notice..
        walletNames.add(0, getString(R.string.select_wallet_to_save_keys))
        mDataManager.walletManager.listWallets(null).forEach { walletNames.add(it.walletName) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, walletNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_wallets_unlocked.setAdapter(adapter)
        sp_wallets_unlocked.setSelection(0)
        sp_wallets_unlocked.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                walletName = sp_wallets_unlocked.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        showWaiting()
        addDisposable(
                mDataManager
                        .createKey(2)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : RxCallbackWrapper<Array<EosPrivateKey>>(this) {
                            override fun onNext(keys: Array<EosPrivateKey>) {
                                mOwnerKey = keys[0]
                                mActiveKey = keys[1]
                                LogUtil.i("---------------")
                                LogUtil.i("zzh-own-1", mOwnerKey.toString())
                                LogUtil.i("zzh-own-2", mOwnerKey.getAsBigInteger().toString())
                                LogUtil.i("zzh-own-3", mOwnerKey.getPublicKey().toString())
                                LogUtil.i("zzh-active-1", mActiveKey.toString())
                                LogUtil.i("zzh-active-2", mActiveKey.asBigInteger.toString())
                                LogUtil.i("zzh-active-3", mActiveKey.publicKey.toString())
                                et_owner_public_key.setText(mOwnerKey.publicKey.toString())
                                et_active_public_key.setText(mActiveKey.getPublicKey().toString())
                                hideWaiting()
                            }
                        })
        )
        btn_create.setOnClickListener {
            if (!checkInputs(et_account_name, et_stake_net, et_stake_cpu, et_buy_ram_eos)) {
                showToastMessage("please fill all fields")
                return@setOnClickListener
            }
            if (java.lang.Float.parseFloat(et_stake_net.text.toString()) <= 0 || java.lang.Float.parseFloat(et_stake_cpu.text.toString()) <= 0 || java.lang.Float.parseFloat(et_buy_ram_eos.text.toString()) <= 0) {
                showToastMessage("number of eos should be greater than 0")
                return@setOnClickListener
            }
            if (HZWalletManager.getInst().walletNameExist(et_account_name.text.toString())) {
                showToastMessage("wallet name already exists, please change another one")
                return@setOnClickListener
            }
            if (sp_wallets_unlocked.selectedItemPosition == 0 || walletName.trim().isEmpty()) {
                showToastMessage("please select at lease one eos wallet")
                return@setOnClickListener
            }
            checkAccountName(0)
        }
    }

    fun checkAccountName(type: Int) {//https://blog.csdn.net/zlp_zky/article/details/70214672
        var nameMatch = Pattern.matches("^[1-5a-z]{12}$", et_account_name.text.toString())
        if (nameMatch) {
            showWaiting()
            addDisposable(mDataManager
                    .readAccountInfo(et_account_name.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : RxCallbackWrapper<JsonObject>(this) {
                        override fun onNext(result: JsonObject) {
                            hideWaiting()
                            AlertDialog.Builder(this@CreateEosWalletAC).setMessage(R.string.account_name_exist).create().show()
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            hideWaiting()
                            showInputPassword()
                        }
                    })
            )
        } else {
            showToastMessage("account name invalid")
        }
    }

    private fun showInputPassword() {
        val inflater = LayoutInflater.from(this)
        val textEntryView = inflater.inflate(R.layout.layout_input_password, null)
        val etPassword = textEntryView.findViewById<View>(R.id.editText_password) as EditText
        etPassword.requestFocus()
        val dialogPwd = AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("")
                .setView(textEntryView)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> showKeyboard(false, etPassword) }
                .create()
        dialogPwd.setOnShowListener {
            val b = dialogPwd.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                mDataManager.walletManager.lock(walletName)
                if (etPassword.text.toString().isEmpty()) {
                    return@setOnClickListener
                }
                mDataManager.walletManager.unlock(walletName, etPassword.text.toString())
                if (mDataManager.walletManager.isLocked(walletName)) {
                    showToastMessage("invalid password")
                    return@setOnClickListener
                }
                showKeyboard(false, etPassword)
                dialogPwd.dismiss()
            }
        }
        dialogPwd.show()
    }

    fun importKey() {
//        mDataManager.walletManager.importKey(walletName, et_key.text.toString())
//        mDataManager.walletManager.saveFile(walletName)
        hideWaiting()
        UserInfoManager.getInst().insertWallet(walletName, accountName, 0, Constant.TAG_EOS_WALLET, WALLET_EOS)
        UserInfoManager.getInst().currentWalletAddress = accountName
        setResult(Activity.RESULT_OK)
        finish()
    }
}