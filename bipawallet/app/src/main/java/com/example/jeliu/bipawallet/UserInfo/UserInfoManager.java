package com.example.jeliu.bipawallet.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Model.HZContact;
import com.example.jeliu.bipawallet.Model.HZPrivateKey;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by liuming on 12/05/2018.
 */

public class UserInfoManager {
    private static UserInfoManager s_inst = new UserInfoManager();

    private int prifoles[] = {R.drawable.hs, R.drawable.mty, R.drawable.px, R.drawable.qe, R.drawable.sz, R.drawable.xn };
    private static String s_split = "______";
    private boolean firstRun;

    private HashMap<String, String> wallets = new HashMap<>();
    private ArrayList<HZContact> contacts = new ArrayList<>();
    private ArrayList<HZPrivateKey> privateKeys = new ArrayList<>();

    private JSONArray jsonArray;
    private String currentWalletAddress;

    public double gasLimited;
    public double gasPrice;

    private int language = 0;
    private int usd = 0;

    public static UserInfoManager getInst() {
        return s_inst;
    }

    private UserInfoManager() {
        SharedPreferences settings = HZApplication.getInst().getSharedPreferences("UserInfo", 0);

        language = settings.getInt("language", 0);
        usd = settings.getInt("usd", 0);
        //get from shared prefs
        Gson gson = new Gson();
        String storedHashMapString = settings.getString("wallets", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        wallets = gson.fromJson(storedHashMapString, type);
        if (wallets == null || wallets.size() == 0) {
            wallets = new HashMap<>();
        } else {
            currentWalletAddress = (String)wallets.keySet().toArray()[0];
            for (String key : wallets.keySet()) {
                HZWallet wallet = new HZWallet();
                wallet.address = key;
                String name = wallets.get(key);
                String splits[] = name.split("______");
                if (splits.length == 2) {
                    wallet.name = splits[0];
                    wallet.profileIndex = Integer.valueOf(splits[1]);
                } else {
                    continue;
                }
                HZWalletManager.getInst().addWallet(wallet);
            }
        }

        String[] tmp = loadArray("contacts", HZApplication.getInst());
        if (tmp != null) {
            for (String s : tmp) {
                HZContact hz = gson.fromJson(s, HZContact.class);
                if (hz != null) {
                    contacts.add(hz);
                }
            }
        }
    }

    public boolean isEmptyWallet() {
        return (wallets.size() == 0);
    }

    public void deleteWallet(String walletAddress) {
        HZWalletManager.getInst().removeWallet(walletAddress);
        if (wallets.containsKey(walletAddress)) {
            wallets.remove(walletAddress);
            jsonArray = null;
            synchronize();
        }
        if (currentWalletAddress.equalsIgnoreCase(walletAddress)) {
            currentWalletAddress = null;
        }
    }

    public int getUsd() {
        return usd;
    }

    public void setUsd(int u) {
        usd = u;

        SharedPreferences settings = HZApplication.getInst().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor localEditor = settings.edit();

        localEditor.putInt("usd", usd);

        localEditor.commit();
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;

        SharedPreferences settings = HZApplication.getInst().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor localEditor = settings.edit();

        localEditor.putInt("language", language);

        localEditor.commit();
    }

    public HashMap<String, String> getWallets() {
        return wallets;
    }

    public class FishNameComparator implements Comparator<String>
    {
        public int compare(String left, String right) {
            return left.compareTo(right);
        }
    }

    public JSONArray getJsonWallets() {
        if (jsonArray == null) {
            jsonArray = new JSONArray();
            HashMap<String, String> wallets = UserInfoManager.getInst().getWallets();

            ArrayList<String> names = new ArrayList<>();
            for (String key : wallets.keySet()) {
                String name = wallets.get(key);
                String splits[] = name.split(s_split);
                if (splits.length == 2) {
                    names.add(splits[0]+"___"+key);
                }
            }

            Collections.sort(names, new FishNameComparator());

            for (String name : names) {
                String splits[] = name.split("___");
                if (splits.length == 2) {
                    String com = wallets.get(splits[1]);
                    JSONObject jsonObject = new JSONObject();
                    String tmp[] = com.split(s_split);
                    if (tmp.length == 2) {
                        try {
                            jsonObject.put(splits[1], tmp[0]);
                            jsonObject.put("profile", tmp[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(jsonObject);
                    }

                }
            }
//            Arrays.sort(names, new Comparator<String>() {
//                @Override
//                public int compare(String s, String t1) {
//                    return 0;
//                }
//            });
        }

        return jsonArray;
    }

    public int getDefaultProfilesCount() {
        return prifoles.length;
    }

    public int getProfile(int index) {
        int count = prifoles.length;
        if (index >= count) {
            index = 0;
        }
        return prifoles[index];
    }

    public String getCurrentWalletAddress() {
        return currentWalletAddress;
    }

    public String getCurrentWalletName() {
        if (currentWalletAddress != null) {
            HZWallet wallet = HZWalletManager.getInst().getWallet(currentWalletAddress);
            if (wallet != null) {
                return wallet.name;
            }
        }
        return "";
    }

    public void setCurrentWalletAddress(String address) {
        currentWalletAddress = address;
    }

    public void insertWallet(String name, String address, int profile) {
        wallets.put(address, String.format("%s%s%d", name, s_split, profile));
        HZWallet wallet = new HZWallet();
        wallet.name = name;
        wallet.address = address;
        wallet.profileIndex = profile;
        HZWalletManager.getInst().addWallet(wallet);
        jsonArray = null;
        synchronize();
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    private void updateFirstRun() {
        SharedPreferences settings = HZApplication.getInst().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor localEditor = settings.edit();

        localEditor.putBoolean("firstRun", false);

        localEditor.commit();
    }

    public void insertContact(HZContact contact) {
        contacts.add(contact);
        saveContacts();
    }

    public ArrayList<HZContact> getContacts() {
        return contacts;
    }

    public void removeContact(HZContact contact) {
        contacts.remove(contact);
        saveContacts();
    }

    private void saveContacts() {
        Gson gson = new Gson();
        String tmp[] = new String[contacts.size()];
        for (int i = 0; i < contacts.size(); ++ i) {
            HZContact c = contacts.get(i);
            String s = gson.toJson(c);
            if (s != null) {
                tmp[i] = s;
            }
        }
        saveArray(tmp, "contacts", HZApplication.getInst());
    }

    public void insertPrivateKey(HZPrivateKey privateKey) {
        privateKeys.add(privateKey);

        Gson gson = new Gson();
        String tmp[] = new String[privateKeys.size()];
        for (int i = 0; i < privateKeys.size(); ++ i) {
            HZPrivateKey c = privateKeys.get(i);
            String s = gson.toJson(c);
            if (s != null) {
                tmp[i] = s;
            }
        }
        saveArray(tmp, "privatekeys", HZApplication.getInst());
    }

    public boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("UserInfo", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    private void synchronize() {
        SharedPreferences settings = HZApplication.getInst().getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor localEditor = settings.edit();

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(wallets);

        //save in shared prefs
        localEditor.putString("wallets", hashMapString);

        localEditor.commit();
    }

}
