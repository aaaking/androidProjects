
package com.example.jeliu.bipawallet.Network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.util.CacheConstantKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeliu on 4/7/18.
 */

public class HZHttpRequest {
    protected RequestResult mObjResult;
    private int mMethod;
    private String url;

    public HZHttpRequest() {
        mMethod = Request.Method.POST;
    }

    public void requestPost(String url, Map<String, String> params, RequestResult objResult) {
        mObjResult = objResult;
        mMethod = Request.Method.POST;
        this.url = url;
        request(url, params);
    }

    public void requestPut(Map<String, String> params, RequestResult objResult) {
        mObjResult = objResult;
        mMethod = Request.Method.PUT;
    }

    public void requestGet(String url, Map<String, String> params, RequestResult objResult) {
        mObjResult = objResult;
        mMethod = Request.Method.GET;
        this.url = url;
        request(url, params);
    }

    public void requestDelete(Map<String, String> params, RequestResult objResult) {
        mObjResult = objResult;
        mMethod = Request.Method.DELETE;
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) CacheConstantKt.getSAppContext().getSystemService(CacheConstantKt.getSAppContext().CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    private void errorOccurred(VolleyError error) {
        if (error == null || error.networkResponse == null) {
            String message = error.getMessage();
            if (!isNetworkOnline()) {
                onFailure(CacheConstantKt.getSAppContext().getString(R.string.no_internet));
            } else {
                onFailure(null);
            }
//            if (message.contains("Failed to connect")) {
//                onFailure("no internet");
//            } else {
            //}
            return;
        }

        String body = "";
        //get status code here
        final String statusCode = String.valueOf(error.networkResponse.statusCode);
        //get response body and parse with appropriate encoding
        try {
            body = new String(error.networkResponse.data,"UTF-8");
            JSONObject obj = new JSONObject(body);
            String code = obj.getString("error_code");
            //String msg = ResponseCodeManager.getInst().getErrorMsg(code);
            onFailure("");
            return;
        } catch (UnsupportedEncodingException e) {
            // exception
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onFailure(body);
    }

    protected void request(String szUrl, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(mMethod, szUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorOccurred(error);
                    }
                }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestManager.getRequestQueue().add(jsObjRequest);
    }

    protected void onSuccess(JSONObject jsonObject) {
        mObjResult.onSuccess(jsonObject, url);
    }

    protected void onFailure(String szValue) {
        mObjResult.onFailure(szValue, url);
    }

    protected void onError() {
        mObjResult.onFailure("error", url);
    }
}

