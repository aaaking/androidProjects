package com.example.jeliu.bipawallet.Common;

import com.example.jeliu.bipawallet.Model.HZToken;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuming on 02/07/2018.
 */

public class HZWalletManager {
    private static HZWalletManager s_inst = new HZWalletManager();
    private List<HZWallet> walletList = new ArrayList<>();

    public static HZWalletManager getInst() {
        return s_inst;
    }

    public void addWallet(HZWallet wallet) {
        if (walletList.contains(wallet)) {
            walletList.remove(wallet);
        }
        walletList.add(wallet);
    }

    public HZWallet getWallet(String address) {
        for (HZWallet hzWallet : walletList) {
            if (hzWallet.address.equalsIgnoreCase(address)) {
                return hzWallet;
            }
        }
        return null;
    }

    public void removeWallet(String address) {
        for (HZWallet hzWallet : walletList) {
            if (hzWallet.address.equalsIgnoreCase(address)) {
                walletList.remove(hzWallet);
                break;
            }
        }
    }

    public void updateWalletInfo(String address, JSONArray jsonArray) {
        boolean find = false;
        for (HZWallet hzWallet : walletList) {
            if (hzWallet.address.equalsIgnoreCase(address)) {
                find = true;
                int count = jsonArray.length();
                hzWallet.tokenList = new ArrayList<>();
                for (int i = 0; i < count; ++ i) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HZToken token = new HZToken();
                        token.token = jsonObject.getString("token");
                        token.value = jsonObject.getDouble("value");
                        hzWallet.tokenList.add(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        if (!find) {
            HZWallet wallet = new HZWallet();
            String name = address.substring(0, 6);
            wallet.name = name;
            wallet.address = address;
            wallet.tokenList = new ArrayList<>();
            addWallet(wallet);
            updateWalletInfo(address, jsonArray);
        }
    }
}
