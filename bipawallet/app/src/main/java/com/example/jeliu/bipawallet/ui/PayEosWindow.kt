package com.example.jeliu.bipawallet.ui

import android.view.View
import android.widget.*
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.eos.data.EoscDataManager
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/9/20.
 */
class PayEosWindow(var jsonObject: JSONObject, var activity: BaseActivity) : PopupWindow() {
    @Inject
    lateinit var mDataManager: EoscDataManager

    init {
        activity.activityComponent.inject(this)
        var curAddress = UserInfoManager.getInst().currentWalletAddress
        var wallet = HZWalletManager.getInst().getWallet(curAddress)
        val payToken = jsonObject.optString("token")
        val payAddress = jsonObject.optString("id")
        val payValue = jsonObject.optDouble("value")
        val popupView = activity.layoutInflater.inflate(R.layout.window_pay_eos, null)

        val et_memo = popupView.findViewById<EditText>(R.id.et_memo)

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
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        if (wallet != null && wallet.type == WALLET_EOS) {
            for (i in 0 until sp_wallets_unlocked.adapter.count) {
                if (sp_wallets_unlocked.getItemAtPosition(i).toString() == wallet.name) {
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
        }
        popupView.setOnTouchListener { _, _ -> true }

        contentView = popupView
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        isFocusable = true
    }
}