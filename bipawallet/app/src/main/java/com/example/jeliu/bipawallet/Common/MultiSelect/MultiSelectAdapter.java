package com.example.jeliu.bipawallet.Common.MultiSelect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.HZBaseAdapter;
import com.example.jeliu.bipawallet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuming on 12/05/2018.
 */

public class MultiSelectAdapter extends HZBaseAdapter {

    public MultiSelectAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        UnitListHolder holder = null;
        if (null == view) {
            view = inflater.inflate(R.layout.layout_mul_select, null);
            holder = new UnitListHolder();

            holder.tvName = (TextView) view.findViewById(R.id.textView_name);
            holder.ivSelect = (ImageView) view.findViewById(R.id.imageView_check);

            view.setTag(holder);
        } else {
            holder = (UnitListHolder) view.getTag();
        }
        try {
            final String jsonObj = jsonArr.getString(i);
            holder.tvName.setText(jsonObj);
        }
        catch (JSONException ex) {
            return null;
        }

        return view;
    }

    class UnitListHolder {
        public TextView tvName;
        public ImageView ivSelect;
    }
}
