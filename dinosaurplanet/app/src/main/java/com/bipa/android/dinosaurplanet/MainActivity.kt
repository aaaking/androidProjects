package com.bipa.android.dinosaurplanet

import android.os.Bundle
import com.bipa.android.webview.WebViewActivity
import com.example.web3j.startWeb3jAC
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : WebViewActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testWebView.setOnClickListener { WebViewActivity.start(this@MainActivity, 0, "http://dinosaur.bipa.io/") }
        testWeb3j.setOnClickListener { startWeb3jAC(this@MainActivity) }
    }
}
