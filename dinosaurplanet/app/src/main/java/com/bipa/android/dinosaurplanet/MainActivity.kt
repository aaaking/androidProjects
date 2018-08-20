package com.bipa.android.dinosaurplanet

import android.os.Bundle
import com.bipa.android.webview.WebViewActivity
import com.example.web3j.startWeb3jAC
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import android.widget.Toast
import dalvik.system.DexClassLoader


class MainActivity : WebViewActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testWebView.setOnClickListener { WebViewActivity.start(this@MainActivity, 0, "http://dinosaur.bipa.io/") }
        testWeb3j.setOnClickListener { startWeb3jAC(this@MainActivity) }
        test_main.setOnClickListener {
            testOne();
            useDexClassLoader()
        }
    }

    fun testOne () {
        var url = "bipa://com.bipa.wallet/main?params={‘id’:’0x4BaBf11D785922DDDb51076AC0030FDC41778607', 'value':1.000000, 'token':'eth', 'BipaWallet':1}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        intent.setType("text/plain")
        //intent.putExtras(mExtras);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this@MainActivity.startActivity(intent)
    }
    fun testTwo () {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, data?.toString() ?: "--------", Toast.LENGTH_LONG).show()
    }

    fun useDexClassLoader() {
        var intent = Intent("com.bipa.wallet.transfer", null)
        val plugins = packageManager.queryIntentActivities(intent, 0)
        if (plugins != null && plugins.size > 0) {
            var rinfo = plugins[0]
            var ainfo = rinfo.activityInfo
            var div = System.getProperty("path.separator")
            var packageName = ainfo.packageName
            var dexPath = ainfo.applicationInfo.sourceDir
            var dexOutputDir = applicationInfo.dataDir
            var libPath = ainfo.applicationInfo.nativeLibraryDir
            val cl = DexClassLoader(dexPath, dexOutputDir, libPath, this.javaClass.classLoader)
            try {
                var clazz = cl.loadClass(packageName + ".Main.NavActivity")
                var obj = clazz.newInstance()
                val params = arrayOfNulls<Class<*>>(2)
                params[0] = String::class.java
                params[1] = String::class.java
                var action = clazz.getMethod("transfer", *params)
                var ret = action.invoke(obj, "fffffff", "1111")
                Log.i("zzh", ret.toString())
            } catch (e: Exception) {

            }
        }
    }
}
