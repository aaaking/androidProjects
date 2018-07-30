package com.bipa.android.webview;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class SignStringRequest extends StringRequest {
    private final static String TAG = "SignStringRequest";
    public final static String HEADER_KEY_SIGN = "sn";
    public final static String HEADER_KEY_RAND = "ds";
    public final static String HEADER_KEY_TIME = "tm";
    public final static String HEADER_KEY_BODY = "by";
    public final static String HEADER_KEY_APPVER = "ver";
    public final static String HEADER_KEY_AREA = "area";
    public static final int DEFAULT_TIMEOUT_MS = 8000;

    public SignStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }
}
