package com.example.jeliu.bipawallet.Record;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuming on 23/05/2018.
 */

public class RecordDetailsActivity extends BaseActivity {
    @BindView(R.id.textView_coin)
    TextView tvCoin;

    @BindView(R.id.textView_money)
    TextView tvMoney;

    @BindView(R.id.tv_hash)
    TextView tvHash;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_begintime)
    TextView tvBeginTime;

    @BindView(R.id.tv_address)
    TextView tvAddress;

    @BindView(R.id.tv_endtime)
    TextView tvEndtime;

    @BindView(R.id.iv_transfer_result)
    ImageView ivResult;
    //

    @BindView(R.id.tv_meony)
    TextView tvMeony;

    @BindView(R.id.tv_currency)
    TextView tvCurrency;

    @BindView(R.id.tv_address_to)
    TextView tvAddressTo;

    @BindView(R.id.tv_trans_result)
    TextView tvTransResult;

    private JSONObject jsonObject;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        try {
            jsonObject = new JSONObject(token);
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTitle(getString(R.string.trade_detail));
        showBackButton();
    }

    private void updateUI() {
        if (jsonObject == null) {
            return;
        }
        try {
            String token = jsonObject.getString("token");
            tvCoin.setText(token);

            String value = jsonObject.getString("value");
            tvMoney.setText("-" + value);

            int status = jsonObject.getInt("status");
            if (status == 1) {
                ivResult.setImageDrawable(getResources().getDrawable(R.drawable.success));
                //tvStatus.setText("交易成功");
            } else if (status == 2) {
                //tvStatus.setText("等待确认中");
                tvTransResult.setEnabled(false);
                ivResult.setEnabled(false);
                ivResult.setImageDrawable(getResources().getDrawable(R.drawable.wait));
            } else {
                //tvStatus.setText("交易失败");
                tvTransResult.setText(getString(R.string.failed_transfer));
                ivResult.setImageDrawable(getResources().getDrawable(R.drawable.fail));
            }
            String txhash = jsonObject.getString("txhash");
            tvHash.setText(txhash);

            long time = jsonObject.getLong("time");
            Date date = new Date(time);
            String s = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
            tvTime.setText(s);
            tvBeginTime.setText(s);

            String address = jsonObject.getString("address");
            tvAddress.setText(address);

            tvMeony.setText(value);
            tvCurrency.setText(token);

            String addressto = jsonObject.getString("to");
            tvAddressTo.setText(addressto);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            long time = jsonObject.getLong("endTime");
            Date date = new Date(time);
            String s = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date);
            tvEndtime.setText(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
