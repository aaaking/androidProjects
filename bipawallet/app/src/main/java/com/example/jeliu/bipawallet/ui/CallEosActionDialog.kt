package com.example.jeliu.bipawallet.ui

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Constant
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.Model.HZWallet
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.ui.abiview.AbiViewBuilder
import com.example.jeliu.bipawallet.util.LogUtil
import com.example.jeliu.bipawallet.util.Util
import com.example.jeliu.eos.data.EoscDataManager
import com.example.jeliu.eos.data.remote.model.abi.EosAbiMain
import com.example.jeliu.eos.data.remote.model.api.PushTxnResponse
import com.example.jeliu.eos.ui.base.RxCallbackWrapper
import com.example.jeliu.eos.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import javax.inject.Inject

/**
 * Created by 周智慧 on 2018/10/10.
 */
class CallEosActionDialog : DialogFragment() {
    @Inject
    lateinit var mDataManager: EoscDataManager
    var mWallet: HZWallet? = HZWalletManager.getInst().getWallet(UserInfoManager.getInst().currentWalletAddress)
    lateinit var rootView: ViewGroup
    private lateinit var eos_contract: String
    private lateinit var eos_action: String
    private lateinit var eos_data_json: String
    private lateinit var eos_permission: String
    private lateinit var permissionAccount: String
    private var permissionName: String = "active"
    var paySuccessCallback: IPayEosResult? = null
    var mAbiViewBuilder: AbiViewBuilder? = null
    lateinit var button_pay: View
    lateinit var tv_action_params: TextView

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
        eos_permission = arguments?.getString(Constant.KEY_EOS_PERMISSION) ?: ""
        LogUtil.i("CallEosActionDialog onCreateView22222 arguments ${arguments}  activity ${activity}")
        return inflater.inflate(R.layout.dialog_eos_push_action, container).apply {
            this@CallEosActionDialog.rootView = this as ViewGroup
            findViewById<View>(R.id.imageView_back).setOnClickListener { dismiss() }
            findViewById<TextView>(R.id.tv_contract_address).text = eos_contract
            findViewById<TextView>(R.id.tv_contract_action).text = eos_action
            findViewById<View>(R.id.button_pay).apply {
                button_pay = this
                setOnClickListener {
                    tryPushAction()
                }
            }
            findViewById<TextView>(R.id.tv_action_params).apply {
                tv_action_params = this
                text = try {
                    var json = JSONTokener(eos_data_json).nextValue()
                    var result = "error"
                    if (json is JSONObject) {
                        result = json.toString(4)
                    } else if (json is JSONArray) {
                        result = json.toString(4)
                    }
                    result
                } catch (e: Exception) {
                    button_pay.isEnabled = false
                    e.toString()
                }
            }
            findViewById<TextView>(R.id.et_permission_name).also {
                var arr = eos_permission.split("@")
                permissionAccount = arr[0]
                arr.takeIf { arr.size >= 2 && arr[1].isNotEmpty() }?.apply {
                    permissionName = this[1]
                    it.text = permissionName
                }
            }
            findViewById<Spinner>(R.id.sp_wallets_unlocked).apply {
                var walletNames = arrayListOf<String>()
                walletNames.add(0, activity!!.getString(R.string.select_wallet_to_save_keys))
                mDataManager.walletManager.listWallets(null).forEach { walletNames.add(it.walletName) }
                adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, walletNames)
                (adapter as? ArrayAdapter<*>)?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                        mWallet = HZWalletManager.getInst().getWalletByName(getItemAtPosition(position).toString())
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {}
                }
                for (i in 0 until adapter.count) {
                    takeIf { HZWalletManager.getInst().getWalletByName(getItemAtPosition(i).toString())?.address == permissionAccount }?.apply {
                        setSelection(i)
                    }
                }
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
                                resolveParams()
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
                        button_pay.isEnabled = false
                        tv_action_params.text = errorMsg
                    }
                })
        )
    }

    fun resolveParams() {
        val actionView = mAbiViewBuilder?.getViewForAction(rootView, eos_action)
        if (actionView == null) {
//            val textView = TextView(activity)
//            textView.text = "action params nil"
//            rootView.addView(textView)
        } else {
            rootView.addView(actionView)
        }
    }

    fun setCallback(callback: IPayEosResult) = apply { this.paySuccessCallback = callback }

    fun tryPushAction() {
        if (mWallet?.type != WALLET_EOS) {
            (activity as BaseActivity).showToastMessage("please select an eos wallet")
            return
        }
        showInputPassword()
    }

    private fun showInputPassword() {
        val textEntryView = LayoutInflater.from(activity).inflate(R.layout.layout_input_password, null)
        val etPassword = textEntryView.findViewById<View>(R.id.editText_password) as EditText

        val builder = AlertDialog.Builder(activity!!)
        builder.setCancelable(false)
        builder.setTitle("")
        builder.setView(textEntryView)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, whichButton ->
            mDataManager.walletManager.lock(mWallet?.name)
            if (etPassword.text.toString().isEmpty()) {
                return@setPositiveButton
            }
            mDataManager.walletManager.unlock(mWallet?.name, etPassword.text.toString())
            if (mDataManager.walletManager.isLocked(mWallet?.name)) {
                (activity as BaseActivity).showToastMessage("invalid password")
                return@setPositiveButton
            }
            if (mDataManager.walletManager.listKeys(mWallet?.name).isEmpty()) {
                (activity as BaseActivity).showToastMessage(activity!!.getString(R.string.eos_wallet_no_keys))
                return@setPositiveButton
            }
            pushAction(etPassword.text.toString())
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, whichButton -> }
        builder.show()
    }

    fun pushAction(pwd: String) {
        (activity as BaseActivity).showWaiting()
        val permissions = arrayOf("$permissionAccount@$permissionName")
        (activity as BaseActivity).addDisposable(mDataManager.pushAction(eos_contract, eos_action, tv_action_params.text.toString().replace("\\r|\\n".toRegex(), ""), permissions)
//                .mergeWith { jsonObject -> mDataManager.addAccountHistory(getAccountListForHistory(eos_contract, permissionAccount)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : RxCallbackWrapper<PushTxnResponse>(activity) {
                    override fun onNext(result: PushTxnResponse) {
                        mResultStatus = result.toString()
                        mResultData = Util.prettyPrintJson(result)
                        LogUtil.i("zzh-----pushAction---", mResultStatus)
                        LogUtil.i("zzh-----pushAction---", mResultData)
                        (activity as BaseActivity).hideWaiting()
                        showSuccDialog(true)
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        var errorMsg = Utils.getExceptionStr(e)
                        LogUtil.i("zzh---push action error Throwable----", errorMsg)
                        (activity as BaseActivity).hideWaiting()
                        showSuccDialog(false)
                    }
                })
        )
    }

    var mResultStatus: String = ""
    var mResultData: String = ""
    fun showSuccDialog(succ: Boolean) {
        dismiss()
        var dialog = PushActionResultDialog()
        var bundle = Bundle()
        bundle.putBoolean("result", succ)
        bundle.putString("result_status", mResultStatus)
        bundle.putString("result_data", mResultData)
        dialog.arguments = bundle
        if (succ) {
            dialog.show(activity!!.supportFragmentManager, "")
        } else {

        }
    }
}