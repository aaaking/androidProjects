package com.example.jeliu.bipawallet.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jeliu.bipawallet.R


/**
 * Created by 周智慧 on 2018/9/17.
 */
@JvmField val WALLET_ETH = 0
@JvmField val WALLET_EOS = 1
class WalletTypeDialog : DialogFragment() {
    var type = WALLET_ETH
    var callback: IWalletType? = null

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val dm = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(dm)
            dialog.window.setLayout((dm.widthPixels * 0.85).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_type_wallet, container)
        view.findViewById<View>(R.id.tv_wallet_eth).setOnClickListener {
            callback?.callback(WALLET_ETH)
            dismiss()
        }
        view.findViewById<View>(R.id.tv_wallet_eos).setOnClickListener {
            callback?.callback(WALLET_EOS)
            dismiss()
        }
        return view
    }
}

interface IWalletType {
    fun callback(walletType: Int)
}