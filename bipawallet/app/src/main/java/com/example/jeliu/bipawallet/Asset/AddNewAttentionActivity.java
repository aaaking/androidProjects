package com.example.jeliu.bipawallet.Asset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.HZBaseAdapter;
import com.example.jeliu.bipawallet.Common.AttentionsManager;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuming on 13/05/2018.
 */

public class AddNewAttentionActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView listView;
    private NewAttentionAdapter adapter;

    private JSONArray tokenList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_attention);
        ButterKnife.bind(this);

        setTitle(getString(R.string.add_new_attention));
        showBackButton();
        showSearch();

        setupView();
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (WalletUtils.isValidAddress(address)) {// only load data for ETH wallet
            loadData();
        }
    }

    private void setupView() {
        adapter = new NewAttentionAdapter(this);
        listView.setAdapter(adapter);
    }

    private void loadData() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.TOKENLIST_URL, null, this);
    }

    protected void onSearch() {
        Intent i = new Intent(this, SearchActivity.class);
        String content = tokenList.toString();
        i.putExtra("content", content);
        startActivity(i);
    }

    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        if (url.contains(Constant.TOKENLIST_URL)) {
            try {
                tokenList = jsonObject.getJSONArray("tokenlist");
                adapter.setContent(tokenList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public class NewAttentionAdapter extends HZBaseAdapter {
        public NewAttentionAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            UnitListHolder holder = null;
            if (null == view) {
                view = inflater.inflate(R.layout.layout_new_attention_item, null);
                holder = new UnitListHolder();

                holder.tvName = (TextView) view.findViewById(R.id.textView_name);//textView_desc
                holder.tvDes = (TextView) view.findViewById(R.id.textView_desc);
                holder.ivSelect = (ImageView) view.findViewById(R.id.imageView_wallet_pic);
                holder.ivAddAttention = (ImageView) view.findViewById(R.id.imageView_attention);

                view.setTag(holder);
            } else {
                holder = (UnitListHolder) view.getTag();
            }
            try {
                final String name = jsonArr.getString(i);
                holder.tvName.setText(name);
                holder.tvDes.setText(name);
                if (AttentionsManager.getInst().contains(name)) {
                    holder.ivAddAttention.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
                } else {
                    holder.ivAddAttention.setImageDrawable(getResources().getDrawable(R.drawable.attention));
                }
                holder.ivAddAttention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (AttentionsManager.getInst().contains(name)) {
                            AttentionsManager.getInst().removeAttention(name);
                        } else {
                            AttentionsManager.getInst().addAttention(name);
                        }
                        notifyDataSetChanged();
                    }
                });

            } catch (JSONException ex) {
                return null;
            }

            return view;
        }

        class UnitListHolder {
            public TextView tvName;
            public TextView tvDes;
            public ImageView ivSelect;
            public ImageView ivAddAttention;
        }
    }

}
