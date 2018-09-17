package com.example.jeliu.bipawallet.Common;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.example.jeliu.bipawallet.Model.HZContact;
import com.example.jeliu.bipawallet.util.CacheConstantKt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuming on 27/06/2018.
 */

public class AttentionsManager {
    private static AttentionsManager s_inst;

    private List<String> attentions = new ArrayList<>();

    public static AttentionsManager getInst() {
        if (s_inst == null) {
            s_inst = new AttentionsManager();
        }
        return s_inst;
    }

    private AttentionsManager() {
        String[] tmp = loadArray("attentions", CacheConstantKt.getSAppContext());
        if (tmp != null) {
            for (String s : tmp) {
                attentions.add(s);
            }
        }
        if (!attentions.contains("eth")) {
            attentions.add("eth");
        }
    }

    public List<String> getAttentions() {
        return attentions;
    }

    public boolean contains(String attention) {
        return attentions.contains(attention);
    }

    public void addAttention(String attention) {
        attentions.add(attention);
        store();
    }

    private void store() {
        Gson gson = new Gson();
        String tmp[] = new String[attentions.size()];
        for (int i = 0; i < attentions.size(); ++ i) {
            String c = attentions.get(i);
            tmp[i] = c;
        }
        saveArray(tmp, "attentions", CacheConstantKt.getSAppContext());
    }

    public void removeAttention(String attention) {
        attentions.remove(attention);
        store();
    }

    private boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("AttentionsManager", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    private String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("AttentionsManager", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }
}
