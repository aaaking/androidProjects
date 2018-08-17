package com.bipa.android.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

/**
 * 网页
 */
public class WebViewActivity extends AppCompatActivity {
    private final String TAG = "WebViewActivity";

    public static final String HREF_CHECK_IN = "zydzq_go_checkin:";// "startgame:"

    public final static int TYPE_APP_NOTICE = 1;//APP公告
    public final static int TYPE_HELP = 2;//帮助
    public final static int TYPE_TERMS = 3;//服务条款
    public final static int TYPE_SNG_RULE = 4;//SNG比赛模式规则
    public final static int TYPE_MTT_RULE = 5;//MTT比赛模式规则
    public final static int TYPE_ABOUT_US = 6;//MTT比赛模式规则
    public final static int TYPE_DISCOVERY_BANNER = 7;//"发现"tab页面顶部的广告链接
    public final static int TYPE_POKERCLANS_PROTOCOL = 8;//注册的时候点击"扑克部落服务协议和隐私政策"的h5落地页
    private WebView mWebView;
    String url;
    ProgressBar mProgressBar;
    int type = 0;
    private boolean isService = true;//隐私政策和服务条款
    String languae = "";
    Map<String, String> extraHeaders = new HashMap<>();
    AppNotify mAppNotify;

    public static void start(Activity activity, int type, AppNotify notify) {
        if (TextUtils.isEmpty(notify.url))
            return;
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE, type);
        intent.putExtra(Extras.EXTRA_URL, notify.url);
        intent.putExtra(Extras.EXTRA_DATA, notify);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, int type, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE, type);
        intent.putExtra(Extras.EXTRA_URL, url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        languae = BaseTools.getLanguage(this).toLowerCase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        url = "http://game.bipa.io";//getIntent().getStringExtra(Extras.EXTRA_URL);http://game.bipa.io/#/   http://dinosaur.bipa.io/
        type = getIntent().getIntExtra(Extras.EXTRA_TYPE, 0);
        if (type == TYPE_APP_NOTICE)
            mAppNotify = (AppNotify) getIntent().getSerializableExtra(Extras.EXTRA_DATA);
        extraHeaders.put(SignStringRequest.HEADER_KEY_APPVER, BaseTools.getAppVersionName(this));
        initHeadView();
        initView();
        initWebView();
        loadUrl();
    }

    public void loadUrl() {
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url, extraHeaders);
        }
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView = (WebView) findViewById(R.id.mWebView);
    }

    public void initHeadView() {
    }

    public void initWebView() {
        Log.i("hardwareAccelerated", "hardwareAccelerated: " + mWebView.isHardwareAccelerated() + "  " + mWebView.getLayerType());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
        }
        Log.i("hardwareAccelerated", "hardwareAccelerated: " + mWebView.isHardwareAccelerated() + "  " + mWebView.getLayerType());
        mWebView.setVisibility(View.VISIBLE);
//        mWebView.clearCache(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
        webSettings.setBuiltInZoomControls(false);
//        webSettings.setUseWideViewPort(true);//集WebView是否应该使支持“视窗”HTML meta标记或应该使用视窗。
        webSettings.setLoadWithOverviewMode(true);  //是否使用WebView加载页面,也就是说,镜头拉出宽度适合在屏幕上的内容。
        if (18 < Build.VERSION.SDK_INT ){
            //18 = JellyBean MR2, KITKAT=19
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
//        webSettings.setBlockNetworkImage(true);//图片后台加载
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //H5
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("openapp.nasnano")) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    WebViewActivity.this.startActivity(intent);
                    return true;
                }
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
//                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
//                mWebView.setVisibility(View.VISIBLE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.stopLoading();
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
