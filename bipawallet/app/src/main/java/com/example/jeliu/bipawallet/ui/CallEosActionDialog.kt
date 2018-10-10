package com.example.jeliu.bipawallet.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.Model.HZWallet
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.util.LogUtil
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.data.remote.model.abi.EosAbiMain
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.example.jeliu.eos.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/10/10.
 */
class CallEosActionDialog : DialogFragment() {
    @Inject
    lateinit var mDataManager: EoscDataManager
    var wallet: HZWallet? = HZWalletManager.getInst().getWallet(UserInfoManager.getInst().currentWalletAddress)
    lateinit var rootView: View
    private lateinit var eos_contract: String
    private lateinit var eos_action: String
    private lateinit var eos_data_json: String
    var paySuccessCallback: IPayEosResult? = null

    init {
        LogUtil.i("CallEosActionDialog init111111 arguments ${arguments} activity ${activity}")
    }

    override fun show(manager: FragmentManager, tag: String) {
        LogUtil.i("CallEosActionDialog show ----- arguments ${arguments} activity ${activity} dialog ${dialog}")
        super.show(manager, tag)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as BaseActivity).activityComponent.inject(this)
        eos_contract = arguments?.getString(Constant.KEY_EOS_CONTRACT) ?: ""
        eos_action = arguments?.getString(Constant.KEY_EOS_ACTION) ?: ""
        eos_data_json = arguments?.getString(Constant.KEY_EOS_DATA_JSON) ?: ""
        LogUtil.i("CallEosActionDialog onCreateView22222 arguments ${arguments}  activity ${activity}")
        return inflater.inflate(R.layout.dialog_eos_push_action, container).apply {
            this@CallEosActionDialog.rootView = this
            findViewById<View>(R.id.imageView_back).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.tv_contract_address).text = eos_contract
            findViewById<TextView>(R.id.tv_contract_action).text = eos_action
            findViewById<View>(R.id.button_pay).setOnClickListener {
                tryPushAction()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LogUtil.i("CallEosActionDialog onStart 333333 arguments ${arguments} activity ${activity} dialog ${dialog}")
        if (dialog != null) {
//            val dm = DisplayMetrics()
//            activity?.windowManager?.defaultDisplay?.getMetrics(dm)
            dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            val lp = dialog.window!!.getAttributes()
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setWindowAnimations(R.style.PopupAnimation)
            dialog.window!!.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
//            dialog.setContentView(rootView)
            dialog.window.setBackgroundDrawable(null)
        }
        getAbi()
    }

    fun getAbi() {
        (activity as BaseActivity).showWaiting()
        (activity as BaseActivity).addDisposable(mDataManager.getAbi(eos_contract)
//                .doOnNext({ abi -> mDataManager.addAccountHistory(contract) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : RxCallbackWrapper<EosAbiMain>(activity) {
                    override fun onNext(result: EosAbiMain) {
                        (activity as BaseActivity).hideWaiting()
                        var hasAction = false
                        for (abi in result.actions) {
                            if (abi.name == eos_action) {
                                hasAction = true
                                break
                            }
                        }
                        if (!hasAction) {
                            // tell the upstream we can't accept any more data
                            dispose()
                            onError(Exception("no such action named ${eos_action}"))
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        (activity as BaseActivity).hideWaiting()
                        var errorMsg = Utils.getExceptionStr(e)//HttpCode:500 {"code":500,"message":"Internal Service Error","error":{"code":3010001,"name":"name_type_exception","what":"Invalid name","details":[]}}
                        LogUtil.i("zzh---createAccount error Throwable----", errorMsg)
                        (activity as BaseActivity).showToastMessage(errorMsg)
                        dismiss()
                    }
                })
        )
    }

    fun setCallback(callback: IPayEosResult) = apply { this.paySuccessCallback = callback }

    fun tryPushAction() {

    }
}