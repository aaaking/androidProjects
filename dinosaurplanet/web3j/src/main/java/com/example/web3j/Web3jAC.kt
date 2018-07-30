package com.example.web3j

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.ac_web3j.*
import android.widget.ArrayAdapter
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3jFactory


/**
 * Created by 周智慧 on 30/07/2018.
 */
fun startWeb3jAC(activity: Activity) {
    with(Intent(activity, Web3jAC::class.java)) {
//        this.putExtras(activity.intent?.extras)
        activity.startActivity(this)
    }
}
class Web3jAC : AppCompatActivity() {
    var data_list = ArrayList<String>()
    var web3j: Web3j? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_web3j)
        data_list.add("https://etherscan.io/")
        data_list.add("https://ropsten.etherscan.io/")
        data_list.add("https://kovan.etherscan.io/")
        data_list.add("https://rinkeby.infura.io/")
        var arr_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list)
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arr_adapter
        btn_Connect.setOnClickListener {
            web3j = Web3jFactory.build(HttpService("https://rinkeby.infura.io/"))
            Log.i("zzh", "Connected to Ethereum client version: " + web3j?.web3ClientVersion()?.send()?.web3ClientVersion)
        }
    }
}