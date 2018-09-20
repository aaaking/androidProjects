package com.example.jeliu.eos

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.Network.NetworkUtil
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.ui.WALLET_EOS
import com.example.jeliu.bipawallet.util.LogUtil
import com.example.jeliu.eos.crypto.ec.EosPrivateKey
import com.example.jeliu.eos.crypto.ec.EosPublicKey
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.data.remote.model.api.PushTxnResponse
import com.example.jeliu.eos.data.remote.model.chain.Action
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.example.jeliu.eos.util.Utils
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ac_create_eoswallet.*
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
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
            if (!checkInputs(et_account_name, et_stake_net, et_stake_cpu, et_buy_ram_eos, et_pwd)) {
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
        imageView_eye_store.setOnClickListener {
            passwordShown = !passwordShown
            if (passwordShown) {
                et_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageView_eye_store.setImageDrawable(resources.getDrawable(R.drawable.open))
            } else {
                et_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                imageView_eye_store.setImageDrawable(resources.getDrawable(R.drawable.close))
            }
        }
    }

    private var passwordShown: Boolean = false

    fun checkAccountName(type: Int) {//https://blog.csdn.net/zlp_zky/article/details/70214672
        var nameMatch = Pattern.matches(Constant.EOS_NAME_REGEX, et_account_name.text.toString())
        if (!NetworkUtil.isNetAvailable(this)) {
            showToastMessage("net work unavailable")
        } else if (nameMatch) {
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
                            super.onError(e)//java.net.SocketTimeoutException
                            hideWaiting()
                            var errorMsg = e.toString()
                            if (!NetworkUtil.isNetAvailable(this@CreateEosWalletAC)) {
                                showToastMessage("net work unavailable")
                            } else if (e is HttpException) {
                                val responseBody = e.response().errorBody()
                                var jsonError = Utils.getErrorMessage(responseBody)
                                errorMsg = String.format("HttpCode:%d\n%s", e.code(), jsonError)
                                LogUtil.i("zzh---readAccountInfo error HttpException----", errorMsg)
                                var jsonObject = JSONObject(jsonError)
                                if (jsonObject != null && jsonObject.opt("code") == 500) {
                                    showInputPassword()
                                }
                            } else {
                                showToastMessage(errorMsg)
                            }
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
                createAccountVerbose()
            }
        }
        dialogPwd.show()
    }

    fun createAccountVerbose() {
        showWaiting()
        var creator = HZWalletManager.getInst().getWalletByName(walletName)?.address ?: ""
        creator = creator.replace("\"", "")
        var newAccount = et_account_name.text.toString()
        var eosToBuyRam = et_buy_ram_eos.text.toString()
        var stake4net = et_stake_net.text.toString()
        var stake4cpu = et_stake_cpu.text.toString()
        var owner_public_key = EosPublicKey(et_owner_public_key.text.toString())
        var active_public_key = EosPublicKey(et_active_public_key.text.toString())
        addDisposable(Observable
                .zip(mDataManager.createAccountAction(creator, newAccount, owner_public_key, active_public_key),
                        mDataManager.buyRamInAssetAction(creator, newAccount, eosToBuyRam),
                        mDataManager.delegateAction(creator, newAccount, stake4net, stake4cpu, false),
                        Function3<Action, Action, Action, List<Action>> { createAccount, buyRam, delegate -> Arrays.asList(createAccount, buyRam, delegate) })
                .flatMap { actionList -> mDataManager.pushActions(actionList) }
//                .doOnNext{ jsonObject -> mDataManager.addAccountHistory( creator, newAccount }
                .subscribeOn(Schedulers.io())
//                .doOnNext {
//                    mDataManager.walletManager.importKeys(walletName, arrayOf(mOwnerKey, mActiveKey))
//                    mDataManager.walletManager.saveFile(walletName)
//                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : RxCallbackWrapper<PushTxnResponse>(this) {
                    override fun onNext(result: PushTxnResponse) {
                        LogUtil.i("zzh---createAccount----", result.toString())
                        tryCreateWallet()
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        var errorMsg = e.toString()
                        if (e is HttpException) {
                            val responseBody = e.response().errorBody()
                            errorMsg = String.format("HttpCode:%d\n%s", e.code(), Utils.getErrorMessage(responseBody))
                            LogUtil.i("zzh---createAccount error HttpException----", errorMsg)
                        } else {
                            LogUtil.i("zzh---createAccount error Throwable----", errorMsg)
                        }
                        showToastMessage(errorMsg)
                        hideWaiting()
                    }
                })
        )
    }

    fun tryCreateWallet() {
        val newAccount = et_account_name.text.toString()
        //create an eos wallet
        addDisposable(
                Observable.fromCallable { mDataManager.walletManager.create(newAccount, et_pwd.text.toString()) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : RxCallbackWrapper<String>(this) {
                            override fun onNext(pw: String) {
                                hideWaiting()
                                showToastMessage(getString(R.string.create_success))
                                if (mOwnerKey.publicKey.toString() == et_owner_public_key.text.toString() && mActiveKey.publicKey.toString() == et_active_public_key.text.toString()) {
                                    //if two public keys change, it means creator create a new eos account for another person and he has no private keys, so cannot import private keys in a wallet
                                    mDataManager.walletManager.importKeys(newAccount, arrayOf(mOwnerKey, mActiveKey))
                                    mDataManager.walletManager.saveFile(newAccount)
                                }
                                UserInfoManager.getInst().insertWallet(newAccount, newAccount, 0, Constant.TAG_EOS_WALLET, WALLET_EOS)
                                UserInfoManager.getInst().currentWalletAddress = newAccount
                                setResult(Activity.RESULT_OK)
                                finish()
                            }

                            override fun onError(e: Throwable) {
                                super.onError(e)
                                hideWaiting()
                                showToastMessage(e.toString())
                            }
                        })
        )
    }
}