package com.example.jeliu.bipawallet.Main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.HZBaseAdapter;
import com.example.jeliu.bipawallet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by liuming on 19/05/2018.
 */

public class MenuAdapter extends HZBaseAdapter {
    public MenuAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MenuAdapter.UnitListHolder holder = null;
        if (null == view) {
            view = inflater.inflate(R.layout.layout_menu_item, null);
            holder = new MenuAdapter.UnitListHolder();

            holder.tvName = (TextView) view.findViewById(R.id.textView_name);
            holder.ivSelect = (ImageView) view.findViewById(R.id.imageView_icon);

            view.setTag(holder);
        } else {
            holder = (MenuAdapter.UnitListHolder) view.getTag();
        }
        try {
            final JSONObject jsonObj = jsonArr.getJSONObject(i);
            holder.tvName.setText(jsonObj.getString("name"));
            holder.ivSelect.setImageDrawable(context.getResources().getDrawable(jsonObj.getInt("photo")));
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
