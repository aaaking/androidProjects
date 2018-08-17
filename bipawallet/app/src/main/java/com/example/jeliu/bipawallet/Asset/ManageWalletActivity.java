package com.example.jeliu.bipawallet.Asset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.HZBaseAdapter;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Main.HeaderAdapter;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/**
 * Created by liuming on 06/05/2018.
 */

public class ManageWalletActivity extends BaseActivity {
    @BindView(R.id.listview)
    ListView listView;

    @OnItemClick(R.id.listview) void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, WalletNameActivity.class);
        JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Iterator<?> iterator = jsonObject.keys();// 应用迭代器Iterator 获取所有的key值
            while (iterator.hasNext()) { // 遍历每个key
                String key = (String) iterator.next();
                intent.putExtra("name", jsonObject.getString(key));
                intent.putExtra("address", key);
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startActivityForResult(intent, 2);
    }

    private ManageWalletAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_wallet);
        ButterKnife.bind(this);

        setTitle(getString(R.string.manage));
        showBackButton();

        adapter = new ManageWalletAdapter(this);
        listView.setAdapter(adapter);

        refreshWallets();
    }

    private void refreshWallets() {
        JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
        adapter.setContent(jsonArray);
    }

    private class ManageWalletAdapter extends HZBaseAdapter {
        public ManageWalletAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            UnitListHolder holder = null;
            if (null == view) {
                view = inflater.inflate(R.layout.layout_manage_wallet_item, null);
                holder = new UnitListHolder();

                holder.tvName = (TextView) view.findViewById(R.id.textView_name);
                holder.tvAddress = (TextView) view.findViewById(R.id.textView_address);
                holder.ivSelect = (ImageView) view.findViewById(R.id.imageView_icon);

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
                    holder.tvAddress.setText(key);
                    HZWallet wallet = HZWalletManager.getInst().getWallet(key);
                    if (wallet != null) {
                        holder.ivSelect.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
                    }
                    break;
                }
            }
            catch (JSONException ex) {
                return null;
            }

            return view;
        }

        class UnitListHolder {
            public TextView tvName;
            public TextView tvAddress;
            public ImageView ivSelect;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            refreshWallets();
        }
    }
}
