package com.example.jeliu.bipawallet.debug

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.jeliu.bipawallet.Common.Common
import com.example.jeliu.bipawallet.Common.HZWalletManager
import com.example.jeliu.bipawallet.R
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager
import com.example.jeliu.bipawallet.bipacredential.BipaContract
import com.example.jeliu.bipawallet.bipacredential.ContractUtil
import com.example.jeliu.bipawallet.bipacredential.Greeter
import com.example.jeliu.bipawallet.util.Execute
import com.example.jeliu.bipawallet.util.LogUtil
import kotlinx.android.synthetic.main.ac_dev.*
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.AbiTypes
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created by 周智慧 on 2018/9/25.
 */
fun startDevAC(activity: Activity?) {
    activity?.startActivity(Intent(activity, DevAC::class.java))
}
class DevAC : Activity() {
    private var payAddress: String? = "0x8bfe2af929790bf5e68c3238c03ec626b5ab89c8"
    private var payToken: String? = null
    private var type_chain: Int = 0
    private var serialNum: String? = null
    private var uid: String? = null
    private var payValue: Double = 0.001.toDouble()
    internal var gasLimit: Double = 43000.toDouble()
    internal var gasPrice: Double = 1000000000.toDouble()
    internal var currentGasPrice: Double = 0.toDouble()
    var eth_func_name_params = "greet()"//similar as "transfer(address, uint256)"
    var eth_func_values = emptyArray<Any>()
    val web3j = Common.getWeb3j()
    val address = UserInfoManager.getInst().currentWalletAddress
    val wallet = HZWalletManager.getInst().getWallet(address)
    var credentials: Credentials? = null// = WalletUtils.loadCredentials("1111", Common.WALLET_PATH + File.separator + wallet.fileName)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_dev)
        //deploy
        deploy.setOnClickListener {
            Execute(Runnable {
                getCredential()
                //val contract = Greeter.deploy(web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, "Hello bipa!").send()
                val contract = Greeter.deploy(web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, "Hello bipa!").send()
                val contractAddress = contract.contractAddress
                Log.i("zzh", contractAddress)
                Log.i("zzh", "View contract at https://rinkeby.etherscan.io/address/" + contractAddress)
                runOnUiThread {
                    contract_address.text = contractAddress
                }
            })
        }
        //get
        btn_call.setOnClickListener {
            Execute(Runnable {
                try {
                    getCredential()
                    test3()
                } catch (e: Exception) {
                    Toast.makeText(this@DevAC, e.toString(), Toast.LENGTH_SHORT).show()
                    LogUtil.i("zzh----Exception---", "" + e.toString())
                }
//                var myContract = Greeter.load("0xa2264d93ccee94044b71da856e6b09c4e728530e", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
//                var content = myContract.greet().send()
//                Log.i("zzh", "Value stored in remote smart contract: " + content)
//                runOnUiThread {
//                    old_params.setText(content)
//                }
            })
        }
        //set
        btn_newGreeting.setOnClickListener {
            Execute(Runnable {
                getCredential()
                newGreeting2()
            })
        }
    }

    fun getCredential(): Credentials {
        if (credentials == null) {
            credentials = WalletUtils.loadCredentials("1111", Common.WALLET_PATH + File.separator + wallet.fileName)
        }
        return credentials!!
    }

    fun test1() {//ok but cannot get output
        eth_func_name_params = "greet()"
        var ethGetTransactionCount = web3j!!.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send()
        var nonce = ethGetTransactionCount.getTransactionCount()
        LogUtil.i("zzh----nonce---", "" + nonce)
        var data = ContractUtil.methodHeader(eth_func_name_params)
        val rawTransaction = RawTransaction.createTransaction(nonce, ManagedTransaction.GAS_PRICE,
                Contract.GAS_LIMIT, payAddress, Convert.toWei(BigDecimal(payValue), Convert.Unit.ETHER).toBigInteger(), data)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send()//EthSendTransaction
        val tx = ethSendTransaction.transactionHash
        LogUtil.i("zzh----ethSendTransaction---", "" + ethSendTransaction)
        LogUtil.i("zzh----tx---", "" + tx)
    }

    fun test2() {//ok
        var myContract = Greeter.load(payAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
//        myContract.setDeployedAddress("4", payAddress)
        Log.i("zzh", "isValid contract: " + myContract.isValid)
        var content = myContract.greet().send()
        Log.i("zzh", "Value stored in remote smart contract: " + content)
    }

    fun test3() {
        var input = emptyArray<String>()
        var output = arrayListOf<String>("string")
        var outputType = AbiTypes.getType("string")
        var contract = BipaContract(Greeter.BINARY, payAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
        Log.i("zzh", "isValid contract: " + contract.isValid)
        val function = Function("greet",
                Arrays.asList(),
                ContractUtil.getOutputs(arrayOf("string")))
        var content = contract.executeRemoteCallSingleValueRet(function, String::class.java).send()
        Log.i("zzh", "Value stored in remote smart contract: " + content.toString())
        runOnUiThread(Runnable {
            old_params.text = content
        })
//        var transaction = Transaction.createEthCallTransaction(address, functionEncoder, null)
    }

    fun newGreeting() {
        eth_func_name_params = "newGreeting(string)"
        var ethGetTransactionCount = web3j!!.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send()
        var nonce = ethGetTransactionCount.getTransactionCount()
        LogUtil.i("zzh----nonce---", "" + nonce)
        var dataP = ContractUtil.methodHeader(eth_func_name_params)
        var dataV = new_params.text.toString()
        val rawTransaction = RawTransaction.createTransaction(nonce, ManagedTransaction.GAS_PRICE,
                Contract.GAS_LIMIT, payAddress, Convert.toWei(BigDecimal(payValue), Convert.Unit.ETHER).toBigInteger(), dataP + dataV)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send()//EthSendTransaction
        val tx = ethSendTransaction.transactionHash
        LogUtil.i("zzh----ethSendTransaction---", "" + ethSendTransaction)
        LogUtil.i("zzh----tx---", "" + tx)
    }

    fun newGreeting2() {
        var contract = BipaContract(Greeter.BINARY, payAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
        Log.i("zzh", "isValid contract: " + contract.isValid)
        val function = Function("newGreeting",
                ContractUtil.getInputs(arrayOf("string"), arrayOf(new_params.text.toString())),
                ContractUtil.getOutputs(arrayOf("string")))
        var content = contract.executeRemoteCallTransactionBipa(function).send()
        Log.i("zzh", "Value stored in remote smart contract: " + content)
    }
}