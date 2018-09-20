package com.example.jeliu.bipawallet.ui

import android.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.Model.HZWallet
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.util.LogUtil
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.example.jeliu.eos.util.Utils
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/20.
 */
class PayEosWindow(var jsonObject: JSONObject, var activity: BaseActivity, var paySuccessCallback: IPayEosSuccess?) : PopupWindow() {
    @Inject
    lateinit var mDataManager: EoscDataManager
    var wallet: HZWallet? = HZWalletManager.getInst().getWallet(UserInfoManager.getInst().currentWalletAddress)
    var popupView: View
    var from = ""
    var payAddress = ""
    var payValue = 0.0

    init {
        activity.activityComponent.inject(this)
        val payToken = jsonObject.optString("token")
        payAddress = jsonObject.optString("id")
        payValue = jsonObject.optDouble("value")
        popupView = activity.layoutInflater.inflate(R.layout.window_pay_eos, null)

        popupView.findViewById<TextView>(R.id.textView_money).text = payValue.toString()

        val tv_from_address = popupView.findViewById<TextView>(R.id.tv_from_address)

        var sp_wallets_unlocked = popupView.findViewById<Spinner>(R.id.sp_wallets_unlocked)
        var walletNames = arrayListOf<String>()
        walletNames.add(0, activity.getString(R.string.select_wallet_to_save_keys))
        mDataManager.walletManager.listWallets(null).forEach { walletNames.add(it.walletName) }
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, walletNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_wallets_unlocked.adapter = adapter
        sp_wallets_unlocked.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                wallet = HZWalletManager.getInst().getWalletByName(sp_wallets_unlocked.getItemAtPosition(position).toString())
                tv_from_address.text = wallet?.address?.replace("\"", "") ?: ""
                from = tv_from_address.text.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        if (wallet?.type == WALLET_EOS) {
            for (i in 0 until sp_wallets_unlocked.adapter.count) {
                if (sp_wallets_unlocked.getItemAtPosition(i).toString() == wallet?.name) {
                    sp_wallets_unlocked.setSelection(i)
                    break
                }
            }
        } else {
            sp_wallets_unlocked.setSelection(0)
        }

        popupView.findViewById<TextView>(R.id.tv_address).text = payAddress

        popupView.findViewById<TextView>(R.id.tv_pay).text = payToken

        val btnPay = popupView.findViewById<Button>(R.id.button_pay)

        popupView.findViewById<ImageView>(R.id.imageView_back).setOnClickListener { dismiss() }
        btnPay.setOnClickListener {
            if (wallet == null || sp_wallets_unlocked.selectedItemPosition <= 0) {
                activity.showToastMessage("please select at lease one eos wallet")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(payToken)) {
                activity.showToastMessage("invalid token")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(payAddress) || !Pattern.matches(Constant.EOS_NAME_REGEX, payAddress)) {
                activity.showToastMessage("invalid to address")
                return@setOnClickListener
            }
            showInputPassword()
        }

        contentView = popupView
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        isFocusable = true
    }

    private fun showInputPassword() {
        val textEntryView = activity.layoutInflater.inflate(R.layout.layout_input_password, null)
        val etPassword = textEntryView.findViewById<View>(R.id.editText_password) as EditText
        etPassword.requestFocus()
        val dialogPwd = AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("")
                .setView(textEntryView)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(activity.getString(R.string.cancel)) { dialog, _ -> activity.showKeyboard(false, etPassword) }
                .create()
        dialogPwd.setOnShowListener {
            val b = dialogPwd.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                mDataManager.walletManager.lock(wallet?.name)
                if (etPassword.text.toString().isEmpty()) {
                    return@setOnClickListener
                }
                mDataManager.walletManager.unlock(wallet?.name, etPassword.text.toString())
                if (mDataManager.walletManager.isLocked(wallet?.name)) {
                    activity.showToastMessage("invalid password")
                    return@setOnClickListener
                }
                if (mDataManager.walletManager.listKeys(wallet?.name).isEmpty()) {
                    activity.showToastMessage(activity.getString(R.string.eos_wallet_no_keys))
                    return@setOnClickListener
                }
                activity.showKeyboard(false, etPassword)
                dialogPwd.dismiss()
                transfer()
            }
        }
        dialogPwd.show()
    }

    private fun transfer() {
        activity.showWaiting()
        activity.addDisposable(mDataManager
                .transfer(from, payAddress, payValue, popupView.findViewById<EditText>(R.id.et_memo).text.toString())
//                    .doOnNext { jsonObject -> mDataManager.addAccountHistory(from, payAddress) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : RxCallbackWrapper<JsonObject>(activity) {
                    override fun onNext(result: JsonObject) {
                        //{"transaction_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","processed":{"id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"receipt":{"status":"executed","cpu_usage_us":680,"net_usage_words":17},"elapsed":680,"net_usage":136,"scheduled":false,"action_traces":[{"receipt":{"receiver":"eosio.token","act_digest":"245aaf12b6ebbf78e7a6e354c94f26b58736600a78231bace59c372f7d03d2de","global_sequence":36508871,"recv_sequence":1770127,"auth_sequence":[["aaaking35512",46]],"code_sequence":4,"abi_sequence":4},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"aaaking35512","permission":"active"}],"data":{"from":"aaaking35512","to":"testaaa31111","quantity":"1.0000 EOS","memo":"1++++++"},"hex_data":"204229834d078d31104208c31893b1ca102700000000000004454f530000000007312b2b2b2b2b2b"},"context_free":false,"elapsed":255,"cpu_usage":0,"console":"","total_cpu_usage":0,"trx_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"account_ram_deltas":[],"inline_traces":[{"receipt":{"receiver":"aaaking35512","act_digest":"245aaf12b6ebbf78e7a6e354c94f26b58736600a78231bace59c372f7d03d2de","global_sequence":36508872,"recv_sequence":15,"auth_sequence":[["aaaking35512",47]],"code_sequence":4,"abi_sequence":4},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"aaaking35512","permission":"active"}],"data":{"from":"aaaking35512","to":"testaaa31111","quantity":"1.0000 EOS","memo":"1++++++"},"hex_data":"204229834d078d31104208c31893b1ca102700000000000004454f530000000007312b2b2b2b2b2b"},"context_free":false,"elapsed":10,"cpu_usage":0,"console":"","total_cpu_usage":0,"trx_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"account_ram_deltas":[],"inline_traces":[]},{"receipt":{"receiver":"testaaa31111","act_digest":"245aaf12b6ebbf78e7a6e354c94f26b58736600a78231bace59c372f7d03d2de","global_sequence":36508873,"recv_sequence":4,"auth_sequence":[["aaaking35512",48]],"code_sequence":4,"abi_sequence":4},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"aaaking35512","permission":"active"}],"data":{"from":"aaaking35512","to":"testaaa31111","quantity":"1.0000 EOS","memo":"1++++++"},"hex_data":"204229834d078d31104208c31893b1ca102700000000000004454f530000000007312b2b2b2b2b2b"},"context_free":false,"elapsed":19,"cpu_usage":0,"console":"","total_cpu_usage":0,"trx_id":"33ced87ecbea143174c02e391ed86844e548ff837b97f32fbffb405b12bd6409","block_num":15324286,"block_time":"2018-09-20T07:37:56.000","producer_block_id":null,"account_ram_deltas":[],"inline_traces":[]}]}],"except":null}}
                        activity.hideWaiting()
                        var tx = result.get("transaction_id").asString
                        LogUtil.i(tx)
                        if (!TextUtils.isEmpty(tx)) {
                            dismiss()
                            paySuccessCallback?.payEosSuccess(tx)
                        } else {
                            activity.showToastMessage("transfer error")
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
                        activity.showToastMessage(errorMsg)
                        activity.hideWaiting()
                    }
                })

        )
    }
}

interface IPayEosSuccess {
    fun payEosSuccess(data: Any)
}