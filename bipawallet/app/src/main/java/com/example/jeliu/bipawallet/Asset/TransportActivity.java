package com.example.jeliu.bipawallet.Asset;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Record.RecordDetailsActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 09/05/2018.
 */

public class TransportActivity extends BaseActivity {
    @BindView(R.id.checkBox)
    CheckBox checkBox;

    @BindView(R.id.rl_simple)
    RelativeLayout rlSimple;

    @BindView(R.id.rl_advance)
    RelativeLayout rlAdvance;

    @BindView(R.id.editText_address)
    EditText etAddress;

    @BindView(R.id.tv_value)
    EditText tvValue;

    @BindView(R.id.et_gas)
    EditText etGas;

    @BindView(R.id.et_price)
    EditText etPrice;

    //et_password
    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.textView_seek)
    TextView tvSeek;

    @BindView(R.id.imageView_eye)
    ImageView ivEye;

    double gasLimit;
    double gasPrice;
    double currentGasPrice;
    private boolean passwordShown;

    private String token;
    private boolean isSimple;

    @OnClick({R.id.iv_que, R.id.textView_que})
    void onQues() {
        gotoWebView(Common.getCenterUrl() + "#question18");
    }

    @OnClick(R.id.imageView_question)
    void onQuesFee() {
        gotoWebView(Common.getCenterUrl() + "#question14");
    }

    @OnClick(R.id.imageView_eye)
    void onEye() {
        passwordShown = !passwordShown;
        if (passwordShown) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivEye.setImageDrawable(getResources().getDrawable(R.drawable.open));
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivEye.setImageDrawable(getResources().getDrawable(R.drawable.close));
        }
    }

    @OnClick(R.id.imageView_scan)
    void onScan() {
        scanCode();
    }

    @OnClick(R.id.button_done)
    void onTrade() {
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        if (!checkInputs(tvValue, etGas, etPrice, etAddress, etPassword)) {
            return;
        }
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        param.put("token", token);
        param.put("from", address);
        param.put("to", etAddress.getText().toString());

        param.put("password", etPassword.getText().toString());
        param.put("value", tvValue.getText().toString());

        if (checkBox.isChecked()) {
            param.put("gasprice", etPrice.getText().toString());
            param.put("gaslimit", etGas.getText().toString());
        } else {
            param.put("gasprice", currentGasPrice + "");
            param.put("gaslimit", gasLimit + "");
        }

        if (token.equalsIgnoreCase("eth")) {
            request.requestPost(Constant.SEND_ETH_URL, param, this);
        } else {
            request.requestPost(Constant.SEND_ERC_URL, param, this);
        }
        showWaiting();
    }

    protected void scanDone(String barcode) {
        if (!checkAddress(barcode)) {
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(barcode);
            String bitpa = jsonObject.getString("id");
            etAddress.setText(bitpa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        ButterKnife.bind(this);

        setupView();
        setupData();
    }

    private void setupData() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.ESTIMATEGAS_URL, null, this);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        if (url.contains(Constant.ESTIMATEGAS_URL)) {
            try {
                gasLimit = jsonObject.getDouble("gasLimit");
                UserInfoManager.getInst().gasLimited = gasLimit;
                etGas.setText(gasLimit + "");

                gasPrice = jsonObject.getDouble("gasPrice");
                UserInfoManager.getInst().gasPrice = gasPrice;
                etPrice.setText(gasPrice + "");
                currentGasPrice = gasPrice;

                Double d = new Double(gasPrice / Common.s_ether * gasLimit);
                String format = String.format("%f", d);

                tvSeek.setText(format + " ether");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(Constant.SEND_ETH_URL) || url.contains(Constant.SEND_ERC_URL)) {
            //showToastMessage(getResources().getString(R.string.transfer_succeed));
            try {
                String tx = jsonObject.getString("tx");
                transferSucceed(tx);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(Constant.QUERY_TRANSCTION_URL)) {
            JSONObject js = null;
            try {
                js = jsonObject.getJSONObject("data");
                gotoDetails(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void transferSucceed(String tx) {
        //queryTransaction
        showWaiting();
        Map<String, String> param = new HashMap<>();
        param.put("txhash", tx);
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.QUERY_TRANSCTION_URL + "?txhash=" + tx, null, this);
    }

    private void gotoDetails(JSONObject jsonObject) {
        Intent intent = new Intent(this, RecordDetailsActivity.class);
        intent.putExtra("token", jsonObject.toString());
        startActivity(intent);
        finish();
    }

    private void setupView() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        setTitle(token + getString(R.string.transfer));
        showBackButton();

        rlAdvance.setVisibility(View.GONE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rlAdvance.setVisibility(View.VISIBLE);
                    rlSimple.setVisibility(View.GONE);

                } else {
                    rlAdvance.setVisibility(View.GONE);
                    rlSimple.setVisibility(View.VISIBLE);

                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                double value = 1100 * progress / 100.0;
                if (progress == 0) {
                    progress = 1;
                }
                double fee = gasPrice * progress;
                // etSimpleFee.setText(fee+"");
                Double d = new Double(fee / Common.s_ether * gasLimit);
                String format = String.format("%f", d);
                tvSeek.setText(format + " ether");
                currentGasPrice = fee;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
