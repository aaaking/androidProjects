package com.example.jeliu.bipawallet.Base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Fragment.RecordFragment;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Record.RecordDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuming on 26/05/2018.
 */

public class RecordAdapter extends BaseAdapter {
    private class ViewHolder {
        public TextView name;
        public TextView detail;
        public TextView value;
    }

    ArrayList<JSONObject> mMaps = new ArrayList<>();
    private Context context;

    public RecordAdapter(Context context) {
        this.context = context;
    }

    public void setContent(ArrayList<JSONObject> maps) {
        mMaps.clear();
        mMaps.addAll(maps);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMaps.size();
    }

    @Override
    public Object getItem(int position) {
        return mMaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_record_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.textView_name);
            holder.detail = (TextView) convertView.findViewById(R.id.textView_detail);
            holder.value = (TextView) convertView.findViewById(R.id.textView_value);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = mMaps.get(position);
                Intent intent = new Intent(context, RecordDetailsActivity.class);
                intent.putExtra("token", jsonObject.toString());
                context.startActivity(intent);
            }
        });

        JSONObject jsonObject = mMaps.get(position);
        String name = null;
        try {
            String token = jsonObject.getString("token");
            holder.name.setText(context.getString(R.string.s_wallet)+token+context.getString(R.string.s_wallet_trans));

            String txhash = jsonObject.getString("txhash");
            holder.detail.setText(txhash);

            String value = jsonObject.getString("value");
            holder.value.setText("- " + value+ " "+token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
