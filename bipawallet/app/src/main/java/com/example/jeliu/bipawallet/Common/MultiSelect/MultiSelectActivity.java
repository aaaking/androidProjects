package com.example.jeliu.bipawallet.Common.MultiSelect;

import android.os.Bundle;
import android.widget.ListView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.Network.RequestResult;
import com.example.jeliu.bipawallet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

/**
 * Created by liuming on 12/05/2018.
 */

public class MultiSelectActivity extends BaseActivity implements RequestResult{
    @BindView(R.id.listview)
    ListView listView;

    @OnItemSelected(R.id.listview) void onItemSelected() {

    }

    private MultiSelectAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_select);
        ButterKnife.bind(this);
        adapter = new MultiSelectAdapter(this);
        listView.setAdapter(adapter);


        setTitle(getString(R.string.choose_currency));
        //showBackButton();
        showDone();
        loadData();
    }

    protected void onDone() {
        finish();
    }

    private void loadData() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.TOKENLIST_URL, null, this);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("tokenlist");
            if (jsonArray != null) {
                adapter.setContent(jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
