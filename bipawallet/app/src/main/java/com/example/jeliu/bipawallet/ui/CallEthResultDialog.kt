package com.example.jeliu.bipawallet.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.jeliu.bipawallet.R

/**
 * Created by 周智慧 on 2018/10/11.
 */
class CallEthResultDialog : DialogFragment() {
    var mResultSucc = true
    var mResultData: String = ""
    var mFailStr: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mResultSucc = arguments?.getBoolean("result") ?: true
        mResultData = arguments?.getString("result_data") ?: ""
        mFailStr = arguments?.getString("result_fail") ?: ""

        var rootView = inflater.inflate(if (mResultSucc) R.layout.layout_pay_succeed else R.layout.layout_pay_failed, container)
        if (mResultSucc) {
            initSuccView(rootView)
        } else {
            initFailView(rootView)
        }
        return rootView
    }

    fun initSuccView(view: View) {
        view.findViewById<TextView>(R.id.textView_title).text = activity?.resources?.getString(R.string.call_eth_func_succ)
        view.findViewById<TextView>(R.id.textView_status).text = activity?.resources?.getString(R.string.call_succeed_tip)
        view.findViewById<TextView>(R.id.textView_hash).text = "hash value: $mResultData"
    }

    fun initFailView(view: View) {
        view.findViewById<TextView>(R.id.textView_title).text = activity?.resources?.getString(R.string.call_eth_func_fail)
        view.findViewById<TextView>(R.id.textView_status).text = mFailStr
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setWindowAnimations(R.style.PopupAnimation)
            dialog.window!!.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
            dialog.window.setBackgroundDrawable(null)
        }
    }
}