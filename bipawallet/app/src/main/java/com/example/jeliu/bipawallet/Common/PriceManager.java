package com.example.jeliu.bipawallet.Common;

import com.example.jeliu.bipawallet.Model.HZTokenPriceUnit;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.Network.RequestResult;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuming on 29/07/2018.
 */

public class PriceManager implements RequestResult {
    private static PriceManager s_inst = new PriceManager();

    private ArrayList<HZTokenPriceUnit> urls = new ArrayList<>();

    public static PriceManager getInst() {
        return s_inst;
    }

    public double usd2rmb = 1;
    public double eosPrice = 1;

    private ArrayList<PriceChangedListener> listeners = new ArrayList<>();

    public void setup() {
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.COIN_PRICE_URLS, null, this);

        HZHttpRequest request1 = new HZHttpRequest();
        request1.requestGet(Constant.USD_CNY_PRICE, null, this);

        HZHttpRequest requestEosPrice = new HZHttpRequest();
        requestEosPrice.requestGet(Constant.MARKET_EOS_PRICE, null, this);
    }

    public void addListener(PriceChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PriceChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifierListers() {
        for (PriceChangedListener listener : listeners) {
            listener.priceChanged();
        }
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        try {
            if (url.contains(Constant.COIN_PRICE_URLS)) {
                int code = jsonObject.getInt("code");
                if (code != 0) {
                    //String msg = jsonObject.getString("msg");
                    //showToastMessage(msg);
                    return false;
                } else {
                    JSONArray jsonArray = jsonObject.getJSONArray("queryurls");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; ++i) {
                        JSONObject js = jsonArray.getJSONObject(i);
                        HZTokenPriceUnit unit = new HZTokenPriceUnit();
                        unit.token = js.getString("token");
                        unit.url = js.getString("url");
                        urls.add(unit);
                        //urls.add(js);
                    }
                    getInfoFromUrls();
                    return true;
                }
            } else if (url.contains(Constant.USD_CNY_PRICE)) {
                int code = jsonObject.getInt("code");
                if (code != 0) {
                    //String msg = jsonObject.getString("msg");
                    //showToastMessage(msg);
                    return false;
                } else {
                    usd2rmb = jsonObject.getDouble("price");
                }
                return true;

            } else if (url.contains(Constant.MARKET_EOS_PRICE)) {
                if (jsonObject != null) {
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        JSONObject quotes = data.optJSONObject("quotes");
                        if (quotes != null) {
                            JSONObject USD = quotes.optJSONObject("USD");
                            if (USD != null) {
                                eosPrice = USD.getDouble("price");
                            }
                        }
                    }
                }
            } else {
                JSONObject data = jsonObject.getJSONObject("data");
                String symbol = data.getString("symbol");
                JSONObject quotes = data.getJSONObject("quotes");
                JSONObject USD = quotes.getJSONObject("USD");
                double price = USD.getDouble("price");
                for (HZTokenPriceUnit unit : urls) {
                    if (unit.token.equalsIgnoreCase(symbol)) {
                        unit.price = price;
                    }
                }
                notifierListers();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void onFailure(String szValue, String url) {

    }

    private void getInfoFromUrls() {
        for (HZTokenPriceUnit unit : urls) {
            HZHttpRequest request = new HZHttpRequest();
            request.requestGet(unit.url, null, this);
        }
    }

    public double tokenPrice(String token, double value) {
        for (HZTokenPriceUnit unit : urls) {
            if (unit.token.equalsIgnoreCase(token)) {
                int usd = UserInfoManager.getInst().getUsd();
                if (usd == 1) {
                    return unit.price * value;
                } else {
                    return usd2rmb * unit.price * value;
                }
            }
        }
        return 0;
    }

    public double getEosAssets(String valueStr) {
        double value = 0;
        try {
            value = Double.parseDouble(valueStr);
        } catch (Exception e) {
        }
        int usd = UserInfoManager.getInst().getUsd();
        if (usd == 1) {
            return eosPrice * value;
        } else {
            return usd2rmb * eosPrice * value;
        }
    }

    public String formatPrice(double value, double price) {
        int usd = UserInfoManager.getInst().getUsd();
        if (usd == 1) {
            return String.format("$%.06f", price * value);
        } else {
            return String.format("¥%.06f", usd2rmb * price * value);
        }
    }
}
