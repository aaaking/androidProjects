package com.example.web3j

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.ac_erc20.*
import org.web3j.abi.datatypes.Type
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction
import org.web3j.utils.Numeric
import java.math.BigInteger

/**
 * Created by 周智慧 on 31/07/2018.
 */
fun startERC20TokenAC(activity: Activity) {
    with(Intent(activity, ERC20TokenAC::class.java)) {
        activity.startActivity(this)
    }
}

class ERC20TokenAC : AppCompatActivity() {
    lateinit var web3j: Web3j
    lateinit var credentials: Credentials
    lateinit var mTokenContract: Zzhc_sol_ZZHToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_erc20)
        //load wallet
        val permissionCheck = ContextCompat.checkSelfPermission(this@ERC20TokenAC, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@ERC20TokenAC, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_WRITE_STORAGE)
        } else {
            loadWallet()
        }
        //contract
        callContract()
    }

    fun loadWallet() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!path.exists()) {
            path.mkdir()
        }
        var fileName = "original" + ".json"//WalletUtils.generateLightNewWalletFile(password, File(path.toString()))
        Log.e("zzh", "generateWallet: $path/$fileName")
        credentials = WalletUtils.loadCredentials("12345678", path.toString() + "/" + fileName)
        wallet_address.text = "钱包地址：\n" + credentials!!.getAddress()
        //
        //connect network
        Thread(Runnable {
            web3j = Web3jFactory.build(HttpService("https://rinkeby.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89"))
            val web3ClientVersion = web3j?.web3ClientVersion()?.send()
            val clientVersion = web3ClientVersion?.getWeb3ClientVersion()
            mTokenContract = Zzhc_sol_ZZHToken.load("0x122638aeaccdadb35a707c5ffcaa0226e43dc02b", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
            runOnUiThread {
                connect_info.text = "节点连接信息：\n" + clientVersion
                balance.isEnabled = web3j != null && credentials != null
                transfer.isEnabled = web3j != null && credentials != null
            }
        }).start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_STORAGE -> {
                if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    loadWallet()
                }
            }
        }
    }

    fun callContract() {
        var data_list = ArrayList<String>()
        data_list.add("0xc7B5F6d0245339674ae4264E44173bC606881651")
        data_list.add("0x4BaBf11D785922DDDb51076AC0030FDC41778607")
        data_list.add("0xC0C5D06DbDDF1c5F0A103c108bdE956D1e2A014e")
        data_list.add("0x8717C17C23a44564a8A08510278b9B45074F8f23")
        var arr_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list)
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arr_adapter
        balance.setOnClickListener {
            Thread(Runnable {
                var result = mTokenContract.balanceOf(spinner.selectedItem.toString()).send();
                runOnUiThread {
                    tv_balance.text = "余额：" + result
                }
            }).start()
        }
        //transfer
        transfer.setOnClickListener {
            Thread(Runnable {
                var transferReceipt = mTokenContract.transfer(spinner.selectedItem.toString(), BigInteger.valueOf(1e18.toLong())).send()
                var eventsStr = ""
                var listE = mTokenContract.getTransferEvents(transferReceipt)
                Log.i("zzh", listE?.size.toString())
                for (event in listE) {
                    eventsStr = eventsStr + "transfer from address " + event._from + " and to address " + event._to + " and value is " + event._value + "。LOG: " + event.log.toString() + "\n"
                    Log.i("zzh", eventsStr)
                }
                runOnUiThread {
                    transfer_receipt.text = "https://rinkeby.etherscan.io/tx/" + transferReceipt.transactionHash
                    transfer_events.text = eventsStr
                }
            }).start()
        }
    }
}