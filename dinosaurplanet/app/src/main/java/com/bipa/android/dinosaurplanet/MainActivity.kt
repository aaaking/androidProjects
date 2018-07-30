package com.bipa.android.dinosaurplanet

import android.os.Bundle
import com.bipa.android.webview.WebViewActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : WebViewActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test.setOnClickListener { WebViewActivity.start(this@MainActivity, 0, "http://dinosaur.bipa.io/") }
    }
}
