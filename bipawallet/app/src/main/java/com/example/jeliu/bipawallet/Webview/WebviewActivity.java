package com.example.jeliu.bipawallet.Webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.R;

import butterknife.ButterKnife;

/**
 * Created by liuming on 07/07/2018.
 */

public class WebviewActivity extends BaseActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);

        mWebView = (WebView)findViewById(R.id.webview);

        setTitle("");
        showBackButton();
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
//"http://47.52.224.7:8081/HelpCenter.html"
        mWebView.loadUrl(url);
        initWebView();
    }

    void initWebView() {
        WebSettings ws = mWebView.getSettings();
        mWebView.setVerticalScrollBarEnabled(false); // 取消Vertical ScrollBar显示
        mWebView.setHorizontalScrollBarEnabled(false);// 取消Horizontal ScrollBar显示
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setDefaultTextEncodingName("utf-8"); // 设置默认的显示编码
        // 容许执行javascript
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        // ------------------------------------------
        ws.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小，我设的是8M
        String appCacheDir = getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath();
        ws.setAppCachePath(appCacheDir);
        ws.setAllowFileAccess(true);// 可以读取文件缓存(manifest生效)
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //
        ws.setPluginState(WebSettings.PluginState.OFF);
        // 设置网页能适配手机屏幕
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {

                return true;
            }

        });

        // 隐藏地址栏，在当前页面跳转
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;// 在本页面跳转,false跳转到浏览器
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

                mWebView.stopLoading();
                view.stopLoading();
            }
        });
    }
}
