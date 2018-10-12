package com.example.jeliu.bipawallet.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.R
import com.example.jeliu.eos.data.EoscDataManager
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/10/12.
 */
class InputDataDialog : DialogFragment() {
    @Inject
    lateinit var mDataManager: EoscDataManager
    var mWalletName = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as BaseActivity).activityComponent.inject(this)
        mWalletName = arguments?.getString("wallet_name") ?: ""
        return inflater.inflate(R.layout.dialog_wallet_input_data, container, false).apply {
            findViewById<TextView>(R.id.tv_title).text = "Import Key: $mWalletName"
            findViewById<View>(R.id.btn_cancel).setOnClickListener { _ -> dismiss() }
            isCancelable = false
            findViewById<View>(R.id.btn_ok).setOnClickListener {
                if (mDataManager.walletManager.isLocked(mWalletName)) {
                    (activity as BaseActivity).showToastMessage("please unlock at least one eos wallet")
                    return@setOnClickListener
                }
                try {
                    mDataManager.walletManager.importKey(mWalletName, findViewById<EditText>(R.id.et_input_data).text.toString())
                    mDataManager.walletManager.saveFile(mWalletName)
                    (activity as BaseActivity).showToastMessage("import success.")
                    dismiss()
                } catch (e: Exception) {
                    (activity as BaseActivity).showToastMessage(e.toString())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}