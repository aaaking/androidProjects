package com.example.jeliu.bipawallet.Network;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jeliu on 4/7/18.
 */

public class RequestManager {
    private static RequestQueue mRequestQueue;

    private RequestManager() {

    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static RequestQueue getRequestQueue() {
        if (null == mRequestQueue) {
            throw new IllegalStateException("Not initialized");
        } else {
            return mRequestQueue;
        }
    }
}
