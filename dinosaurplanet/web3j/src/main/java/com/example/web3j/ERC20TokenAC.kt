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
import kotlinx.android.synthetic.main.ac_erc20.*
import org.web3j.abi.datatypes.Type
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Contract
import org.web3j.tx.ManagedTransaction

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
        //connect network
        Thread(Runnable {
            web3j = Web3jFactory.build(HttpService("https://rinkeby.infura.io/v3/1ef6eda7b4cb4444b3b6907f2086ba89"))
            val web3ClientVersion = web3j?.web3ClientVersion()?.send()
            val clientVersion = web3ClientVersion?.getWeb3ClientVersion()
            runOnUiThread {
                connect_info.text = "节点连接信息：\n" + clientVersion
            }
        }).start()
        //load wallet
        val permissionCheck = ContextCompat.checkSelfPermission(this@ERC20TokenAC, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@ERC20TokenAC, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_WRITE_STORAGE)
        } else {
            loadWallet()
        }
        var myContract = Zzhc_sol_ZZHToken.load("0x21a0d94b867659ba7487e8028122892d34e29c3f", web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT)
        Thread(Runnable {
            var result = myContract.balanceOf("0xc7B5F6d0245339674ae4264E44173bC606881651").send();
            Log.i("zzh", result.toString())
        }).start()
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
}