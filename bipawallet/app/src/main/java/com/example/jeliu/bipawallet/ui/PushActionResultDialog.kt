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
class PushActionResultDialog : DialogFragment() {
    var mResultSucc = true
    var mResultStatus: String = ""
    var mResultData: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mResultSucc = arguments?.getBoolean("result") ?: true
        mResultStatus = arguments?.getString("result_status") ?: ""
        mResultData = arguments?.getString("result_data") ?: ""

        var rootView = inflater.inflate(if (mResultSucc) R.layout.dialog_push_action_succ else R.layout.layout_pay_failed, container)
        if (mResultSucc) {
            initSuccView(rootView)
        } else {
            initFailView(rootView)
        }
        return rootView
    }

    fun initSuccView(view: View) {
        view.findViewById<TextView>(R.id.tv_push_status).text = mResultStatus
        view.findViewById<TextView>(R.id.tv_get_response).text = mResultData
    }

    fun initFailView(view: View) {

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