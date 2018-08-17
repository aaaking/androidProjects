package com.example.jeliu.bipawallet.Main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.HZBaseAdapter;
import com.example.jeliu.bipawallet.Common.MultiSelect.MultiSelectAdapter;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by liuming on 12/05/2018.
 */

public class HeaderAdapter extends HZBaseAdapter {
    public HeaderAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        UnitListHolder holder = null;
        if (null == view) {
            view = inflater.inflate(R.layout.layout_header_item, null);
            holder = new UnitListHolder();

            holder.tvName = (TextView) view.findViewById(R.id.textView_wallet_name);
            holder.ivSelect = (ImageView) view.findViewById(R.id.imageView_wallet_pic);

            view.setTag(holder);
        } else {
            holder = (UnitListHolder) view.getTag();
        }
        try {
            final JSONObject jsonObj = jsonArr.getJSONObject(i);
            Iterator<?> iterator = jsonObj.keys();// 应用迭代器Iterator 获取所有的key值
            while (iterator.hasNext()) { // 遍历每个key
                String key = (String) iterator.next();
                holder.tvName.setText(jsonObj.getString(key));
                break;
            }
            int profile = jsonObj.getInt("profile");
            holder.ivSelect.setImageDrawable(context.getResources().getDrawable(UserInfoManager.getInst().getProfile(profile)));
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
