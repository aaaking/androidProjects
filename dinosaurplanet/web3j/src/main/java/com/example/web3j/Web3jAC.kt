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
import kotlinx.android.synthetic.main.ac_web3j.*
import android.widget.ArrayAdapter
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.io.File
import java.math.BigDecimal


/**
 * Created by 周智慧 on 30/07/2018.
 */
private val REQUEST_PERMISSION_WRITE_STORAGE = 0

fun startWeb3jAC(activity: Activity) {
    with(Intent(activity, Web3jAC::class.java)) {
        //        this.putExtras(activity.intent?.extras)
        activity.startActivity(this)
    }
}

class Web3jAC : AppCompatActivity() {
    var credentials: Credentials? = null
    var data_list = ArrayList<String>()
    var web3j: Web3j? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_web3j)
//        data_list.add("https://etherscan.io/address/0xc7b5f6d0245339674ae4264e44173bc606881651")
//        data_list.add("https://ropsten.etherscan.io/address/0xc7b5f6d0245339674ae4264e44173bc606881651")
//        data_list.add("https://kovan.etherscan.io/address/0xc7b5f6d0245339674ae4264e44173bc606881651")
//        data_list.add("https://rinkeby.etherscan.io/address/0xc7b5f6d0245339674ae4264e44173bc606881651")
        data_list.add("https://mainnet.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        data_list.add("https://ropsten.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        data_list.add("https://kovan.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        data_list.add("https://rinkeby.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89")
        var arr_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list)
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arr_adapter
        val thread = Thread(Runnable {
            web3j = Web3jFactory.build(HttpService(spinner.selectedItem.toString()))
            val web3ClientVersion = web3j?.web3ClientVersion()?.send()
            val clientVersion = web3ClientVersion?.getWeb3ClientVersion()
            Log.i("zzh", clientVersion?.toString())
            runOnUiThread {
                transfer.isEnabled = credentials != null && web3j != null
                status.text = if (web3j != null) "连接：" + clientVersion else "断开"
            }
        })
        btn_Connect.setOnClickListener {
            if (thread.getState() == Thread.State.NEW) {
                thread.start();
            }
        }
        //
        btn_GenerateWallet.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this@Web3jAC, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@Web3jAC, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_WRITE_STORAGE)
            } else {
                // We then need to load our Ethereum wallet file
                // FIXME: Generate a new wallet file using the web3j command line tools https://docs.web3j.io/command_line.html
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!path.exists()) {
                    path.mkdir()
                }
                var password = pwd.text.toString()
                var fileName = password + ".json"//WalletUtils.generateLightNewWalletFile(password, File(path.toString()))
                Log.e("zzh", "generateWallet: $path/$fileName")
                credentials = WalletUtils.loadCredentials(password, path.toString() + "/" + fileName)
                Log.i("zzh", credentials!!.getAddress())
                wallet_address.text = credentials!!.getAddress()
                Log.i("zzh", credentials.toString())
                transfer.isEnabled = credentials != null && web3j != null
            }
        }

        //transfer
        val transferThread = Thread(Runnable {
            // FIXME: Request some Ether for the Rinkeby test network at https://www.rinkeby.io/#faucet
            val transferReceipt = Transfer.sendFunds(web3j, credentials, "0x8717c17c23a44564a8a08510278b9b45074f8f23", BigDecimal.ONE, Convert.Unit.FINNEY).send()
            Log.i("zzh", "Transaction complete, view it at https://ropsten.etherscan.io/tx/" + transferReceipt.transactionHash)
            runOnUiThread {
                txhash.text = "Transaction complete, view it at https://ropsten.etherscan.io/tx/" + transferReceipt.transactionHash
            }
        })
        transfer.setOnClickListener {
            if (transferThread.getState() == Thread.State.NEW) {
                transferThread.start();
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_STORAGE -> {
                if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                }
            }
        }
    }
}