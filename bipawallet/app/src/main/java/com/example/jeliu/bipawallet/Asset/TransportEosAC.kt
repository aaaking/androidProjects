package com.example.jeliu.bipawallet.Asset

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.util.LogUtil
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.example.jeliu.eos.util.Utils
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ac_transport_eos.*
import retrofit2.HttpException
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/20.
 */
fun startTransportEosAC(activity: Activity?, token: String) = activity?.startActivity(Intent(activity, TransportEosAC::class.java).putExtra("token", token))

class TransportEosAC : BaseActivity() {
    private var passwordShown: Boolean = false
    @Inject
    lateinit var mDataManager: EoscDataManager
    val address = UserInfoManager.getInst().currentWalletAddress
    val wallet = HZWalletManager.getInst().getWallet(address)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setContentView(R.layout.ac_transport_eos)
        setTitle(intent.getStringExtra("token") + getString(R.string.transfer))
        initViews()
    }

    fun initViews() {
        tv_from.text = "${getString(R.string.from)}: ${wallet?.address?.replace("\"", "")}"
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
        btn_transfer.setOnClickListener {
            if (!checkInputs(et_pwd, et_to, et_amount)) {
                showToastMessage("please fill all fields")
                return@setOnClickListener
            }
            if (!Pattern.matches(Constant.EOS_NAME_REGEX, et_to.text.toString())) {
                showToastMessage("invalid to address")
                return@setOnClickListener
            }
            mDataManager.walletManager.lock(wallet?.name)
            mDataManager.walletManager.unlock(wallet?.name, et_pwd.text.toString())
            if (mDataManager.walletManager.isLocked(wallet?.name)) {
                showToastMessage("invalid password")
                return@setOnClickListener
            }
            if (mDataManager.walletManager.listKeys(wallet?.name).isEmpty()) {
                showToastMessage(getString(R.string.eos_wallet_no_keys))
                return@setOnClickListener
            }
            transfer()
        }
    }

    fun transfer() {
        try {
            val value = java.lang.Double.parseDouble(et_amount.text.toString())
            showWaiting()
            addDisposable(mDataManager
                    .transfer(wallet?.address?.replace("\"", ""), et_to.text.toString(), value, et_remark.text.toString())
//                    .doOnNext { jsonObject -> mDataManager.addAccountHistory(from, payAddress) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : RxCallbackWrapper<JsonObject>(this) {
                        override fun onNext(result: JsonObject) {
                            //{"transaction_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","processed":{"id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"receipt":{"status":"executed","cpu_usage_us":680,"net_usage_words":17},"elapsed":680,"net_usage":136,"scheduled":false,"action_traces":[{"receipt":{"receiver":"eosio.token","act_digest":"245aaf12b6ebbf78e7a6e354c94f26b58736600a78231bace59c372f7d03d2de","global_sequence":36508871,"recv_sequence":1770127,"auth_sequence":[["aaaking35512",46]],"code_sequence":4,"abi_sequence":4},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"aaaking35512","permission":"active"}],"data":{"from":"aaaking35512","to":"testaaa31111","quantity":"1.0000 EOS","memo":"1++++++"},"hex_data":"204229834d078d31104208c31893b1ca102700000000000004454f530000000007312b2b2b2b2b2b"},"context_free":false,"elapsed":255,"cpu_usage":0,"console":"","total_cpu_usage":0,"trx_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"account_ram_deltas":[],"inline_traces":[{"receipt":{"receiver":"aaaking35512","act_digest":"245aaf12b6ebbf78e7a6e354c94f26b58736600a78231bace59c372f7d03d2de","global_sequence":36508872,"recv_sequence":15,"auth_sequence":[["aaaking35512",47]],"code_sequence":4,"abi_sequence":4},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"aaaking35512","permission":"active"}],"data":{"from":"aaaking35512","to":"testaaa31111","quantity":"1.0000 EOS","memo":"1++++++"},"hex_data":"204229834d078d31104208c31893b1ca102700000000000004454f530000000007312b2b2b2b2b2b"},"context_free":false,"elapsed":10,"cpu_usage":0,"console":"","total_cpu_usage":0,"trx_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"account_ram_deltas":[],"inline_traces":[]},{"receipt":{"receiver":"testaaa31111","act_digest":"245aaf12b6ebbf78e7a6e354c94f26b58736600a78231bace59c372f7d03d2de","global_sequence":36508873,"recv_sequence":4,"auth_sequence":[["aaaking35512",48]],"code_sequence":4,"abi_sequence":4},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"aaaking35512","permission":"active"}],"data":{"from":"aaaking35512","to":"testaaa31111","quantity":"1.0000 EOS","memo":"1++++++"},"hex_data":"204229834d078d31104208c31893b1ca102700000000000004454f530000000007312b2b2b2b2b2b"},"context_free":false,"elapsed":19,"cpu_usage":0,"console":"","total_cpu_usage":0,"trx_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"account_ram_deltas":[],"inline_traces":[]}]}],"except":null}}
                            hideWaiting()
                            var tx = result.get("transaction_id").asString
                            LogUtil.i(tx)
                            if (!TextUtils.isEmpty(tx)) {
                                showToastMessage(getString(R.string.transfer_succeed))
                                // todo: goto transaction detail page
                            } else {
                                showToastMessage("transfer error")
                            }
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
        } catch (e: Exception) {
            hideWaiting()
            showToastMessage(e.toString())
        }
    }
}