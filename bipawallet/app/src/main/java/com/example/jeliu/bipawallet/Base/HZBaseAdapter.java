package com.example.jeliu.bipawallet.Base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Common.MultiSelect.MultiSelectAdapter;
import com.example.jeliu.bipawallet.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by liuming on 12/05/2018.
 */

public class HZBaseAdapter extends BaseAdapter {
    protected Context context;
    protected JSONArray jsonArr;
    protected LayoutInflater inflater;

    public HZBaseAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    public void setContent(JSONArray jsonArr) {
        this.jsonArr = jsonArr;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (jsonArr == null || jsonArr.length() == 0) {
            return 0;
        }
        return jsonArr.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArr.get(position);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return view;
    }

}
