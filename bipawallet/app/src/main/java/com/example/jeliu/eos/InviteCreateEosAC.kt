package com.example.jeliu.eos

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Common
import com.example.jeliu.bipawallet.R
import com.example.jeliu.eos.crypto.ec.EosPrivateKey
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ac_invite_create_eos.*
import org.json.JSONObject
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/18.
 */
fun startInviteCreateEosAC(activity: Activity?) {
    activity?.startActivity(Intent(activity, InviteCreateEosAC::class.java))
}

class InviteCreateEosAC : BaseActivity() {
    lateinit var mOwnerKey: EosPrivateKey
    lateinit var mActiveKey: EosPrivateKey
    @Inject
    lateinit var mDataManager: EoscDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setTitle(resources.getString(R.string.invite))
        setContentView(R.layout.ac_invite_create_eos)
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
                                var copyPKListener = View.OnClickListener {
                                    copyText(if (it == btn_copy_owner_pk) mOwnerKey.toString() else mActiveKey.toString())
                                }
                                //owner
                                tv_owner_public_key.text = mOwnerKey.publicKey.toString()
                                btn_copy_owner_pk.setOnClickListener(copyPKListener)
                                btn_show_owner_pk.setOnClickListener {
                                    if (tv_owner_private_key.length() <= 5) {
                                        tv_owner_private_key.text = mOwnerKey.toString()
                                        btn_show_owner_pk.setText(R.string.hide_pk)
                                    } else {
                                        tv_owner_private_key.text = "***"
                                        btn_show_owner_pk.setText(R.string.show_pk)
                                    }
                                }
                                //active
                                tv_active_public_key.text = mActiveKey.publicKey.toString()
                                btn_copy_active_pk.setOnClickListener(copyPKListener)
                                btn_show_active_pk.setOnClickListener {
                                    if (tv_active_private_key.length() <= 5) {
                                        tv_active_private_key.text = mActiveKey.toString()
                                        btn_show_active_pk.setText(R.string.hide_pk)
                                    } else {
                                        tv_active_private_key.text = "***"
                                        btn_show_active_pk.setText(R.string.show_pk)
                                    }
                                }
                                //
                                copy_account_name_keys.setOnClickListener {
                                    checkAccountName(0)
                                }
                                //
                                generate_qr_code.setOnClickListener {
                                    checkAccountName(1)
                                }
                                hideWaiting()
                            }
                        })
        )
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
                            AlertDialog.Builder(this@InviteCreateEosAC).setMessage(R.string.account_name_exist).create().show()
                        }
                        override fun onError(e: Throwable) {
                            super.onError(e)
                            hideWaiting()
                            if (type == 0) {
                                copyText(generateAccountJson())
                            } else if (type == 1) {
                                imageView_qr.setImageBitmap(Common.encodeAsBitmap(generateAccountJson(), BarcodeFormat.QR_CODE, 200, 200))
                            }
                        }
                    })
            )
        } else {
            showToastMessage("account name invalid")
        }
    }

    fun generateAccountJson(): String {
        var jsonOb = JSONObject()
        jsonOb.put("account_name", et_account_name.text.toString())
        jsonOb.put("owner_public_key", mOwnerKey.publicKey.toString())
        jsonOb.put("active_public_key", mActiveKey.publicKey.toString())
        return jsonOb.toString()
    }
}