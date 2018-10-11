package com.example.jeliu.bipawallet.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.example.jeliu.bipawallet.Base.BaseActivity
import com.example.jeliu.bipawallet.Common.Common
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.Main.NavActivity
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.bipacredential.BipaContract
import com.example.jeliu.bipawallet.bipacredential.ContractUtil
import com.example.jeliu.bipawallet.util.Execute
import com.example.jeliu.bipawallet.util.LogUtil
import org.json.JSONObject
import org.web3j.abi.datatypes.Function
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.utils.Convert
import java.io.File
import java.math.BigInteger

/**
 * Created by 周智慧 on 2018/9/28.
 */
class CallEthFuncDialog : AlertDialog {
    var mRootView: View? = null
    lateinit var tv_func_name: TextView
    lateinit var tv_contract_address: TextView
    lateinit var tv_function_params: TextView
    lateinit var textView_seek: TextView
    lateinit var seekBar: SeekBar
    var gasLimit = BigInteger.valueOf(43000)
    var gasPrice = BigInteger.valueOf(1)//1g
    var currentGasPrice = gasPrice
    var mBinary = ""
    var credentials: Credentials? = null
    lateinit var mActivity: BaseActivity

    constructor(activity: BaseActivity) : super(activity, R.style.cool_dialog_dim) {
        mActivity = activity
        initView()
    }

    constructor(activity: BaseActivity, theme: Int) : super(activity, theme) {
        mActivity = activity
        initView()
    }

    private fun initView() {
        mRootView = View.inflate(mActivity, R.layout.window_call_eth_func, null).apply {
            tv_func_name = findViewById(R.id.tv_func_name)
            tv_contract_address = findViewById(R.id.tv_contract_address)
            tv_function_params = findViewById(R.id.tv_function_params)
            textView_seek = findViewById(R.id.textView_seek)
            textView_seek.text = "${String.format("%f", gasPrice.toDouble() / Common.s_ether * gasLimit.toDouble())} ether"
            seekBar = findViewById(R.id.seekBar)
            seekBar.thumb.setColorFilter(mActivity.resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)//MULTIPLY
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    var progress = seekBar.progress
                    if (progress == 0) {
                        progress = 1
                    }
                    val fee = gasPrice * BigInteger(progress.toString())
                    val d = fee.toDouble() / Common.s_ether * gasLimit.toDouble()
                    textView_seek.text = "${String.format("%f", d)} ether"
                    currentGasPrice = fee
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })
            findViewById<View>(R.id.button_pay).setOnClickListener {
                val inflater = LayoutInflater.from(mActivity)
                val textEntryView = inflater.inflate(R.layout.layout_input_password, null)
                val etPassword = textEntryView.findViewById<View>(R.id.editText_password) as EditText

                val builder = AlertDialog.Builder(mActivity)
                builder.setCancelable(false)
                builder.setTitle("")
                builder.setView(textEntryView)
                builder.setPositiveButton(mActivity.getString(R.string.ok)
                ) { dialog, whichButton ->
                    mActivity.showWaiting()
                    val password = etPassword.text.toString()
                    Execute(Runnable {
                        try {
                            getCredential(password)
                            var contract = BipaContract(mBinary, tv_contract_address.text.toString(), Common.getWeb3j(), credentials, Convert.toWei(currentGasPrice.toBigDecimal(), Convert.Unit.GWEI).toBigInteger(), gasLimit)
                            val function = Function(tv_func_name.text.toString(),
                                    ContractUtil.getInputs(tv_function_params.text.toString()),
                                    ContractUtil.getOutputs(arrayOf("string")))
                            var content = contract.executeRemoteCallTransactionBipa(function).send()
                            var tx = content.transactionHash
                            LogUtil.i("zzh-call-func-tx", "https://rinkeby.etherscan.io/tx/" + tx)
                            if (TextUtils.isEmpty(tx)) {
                                Common.showPayFailed(mActivity, mActivity.findViewById(R.id.container), "", tv_contract_address.text.toString())
                                throw Exception("txhash null")
                            }
                            dismiss()
                            (mActivity as? NavActivity)?.apply {
                                runOnUiThread {
                                    hideWaiting()
                                    val js = JSONObject()
                                    js.put("tx", tx)
                                    sendToPlatformAfterPay(js, null, true)
                                }
                            }
                        } catch (e: Exception) {
                            LogUtil.i("zzh", "call function Exception: " + e.toString())
                            Looper.prepare()
                            mActivity.hideWaiting()
                            mActivity.showToastMessage(e.toString())
                            Looper.loop()
                        }
                    })
                }
                builder.setNegativeButton(mActivity.getString(R.string.cancel)) { dialog, whichButton -> }
                builder.show()
            }
        }
    }

    fun setConrtactAddress(address: String) = apply { tv_contract_address.text = address }
    fun setFuncName(name: String) = apply { tv_func_name.text = name }
    fun setFuncParams(params: String) = apply { tv_function_params.text = params }
    fun setBinary(binary: String) = apply { mBinary = binary }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val params = window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        window?.decorView?.setPadding(mPadding, mPadding, mPadding, mPadding)
    }

    override fun show() {
        super.show()
        val windowTest = getWindow()
        val lp = windowTest!!.getAttributes()
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowTest!!.setWindowAnimations(R.style.PopupAnimation)
        windowTest!!.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        setContentView(mRootView)
    }

    fun getCredential(pwd: String): Credentials {
        val address = UserInfoManager.getInst().currentWalletAddress
        val wallet = HZWalletManager.getInst().getWallet(address)
        if (credentials == null) {
            credentials = WalletUtils.loadCredentials(pwd, Common.WALLET_PATH + File.separator + wallet.fileName)
        }
        return credentials!!
    }
}