package com.example.jeliu.bipawallet.Asset;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Record.RecordDetailsActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.contracts.Wxc;
import com.example.jeliu.bipawallet.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.jeliu.bipawallet.util.ThreadUtilKt.Execute;

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
        final String address = UserInfoManager.getInst().getCurrentWalletAddress();
        param.put("token", token);
        param.put("from", address);
        final String payAddress = etAddress.getText().toString();
        param.put("to", payAddress);
        final String password = etPassword.getText().toString();
        param.put("password", password);
        final String payValue = tvValue.getText().toString();
        param.put("value", payValue);
        final String price = checkBox.isChecked() ? etPrice.getText().toString() : String.valueOf(currentGasPrice);
        final String limit = checkBox.isChecked() ? etGas.getText().toString() : String.valueOf(gasLimit);
        param.put("gasprice", price);
        param.put("gaslimit", limit);
        final HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (token.equalsIgnoreCase("eth")) {
//            request.requestPost(Constant.SEND_ETH_URL, param, this);
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    EthGetTransactionCount ethGetTransactionCount = Common.getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    RawTransaction rawTransaction =
                            RawTransaction.createEtherTransaction(nonce, Convert.toWei(String.valueOf(price), Convert.Unit.GWEI).toBigInteger(),
                                    new BigDecimal(limit).toBigInteger(), payAddress, Convert.toWei(new BigDecimal(payValue), Convert.Unit.ETHER).toBigInteger());
                    // sign & send our transaction
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = Common.getWeb3j().ethSendRawTransaction(hexValue).send();//EthSendTransaction
                    final String tx = ethSendTransaction.getTransactionHash();
                    LogUtil.INSTANCE.i("zzh", "https://rinkeby.etherscan.io/tx/" + tx);
                    if (tx == null) {
                        throw new Exception("transfer fail because txhash null");
                    }
                    final JSONObject js = new JSONObject();
                    js.put("tx", tx);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            transferSucceed(tx);
                        }
                    });
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(TransportActivity.this, getResources().getString(R.string.failed_transfer) + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    hideWaiting();
                    Looper.loop();
                }
            });
        } else if (token.equalsIgnoreCase("wxc")) {
//            request.requestPost(Constant.SEND_ERC_URL, param, this);
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    Wxc contractWxc = Wxc.load(Constant.ADDRESS_WXC, Common.getWeb3j(), credentials, Convert.toWei(String.valueOf(price), Convert.Unit.GWEI).toBigInteger(), new BigDecimal(limit).toBigInteger());
                    BigInteger decimal = contractWxc.decimals().send();
                    BigInteger rawValue = new BigInteger("10").pow(decimal.intValue());
                    TransactionReceipt transferReceipt = contractWxc.transfer(payAddress, rawValue).send();
                    final String tx = transferReceipt.getTransactionHash();
                    LogUtil.INSTANCE.i("zzh", "https://rinkeby.etherscan.io/tx/" + tx);
                    if (tx == null) {
                        throw new Exception("transfer fail because txhash null");
                    }
                    final JSONObject js = new JSONObject();
                    js.put("tx", tx);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            transferSucceed(tx);
                        }
                    });
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(TransportActivity.this, getResources().getString(R.string.failed_transfer) + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    hideWaiting();
                    Looper.loop();
                }
            });
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
        } else if (url.contains(Constant.SEND_BY_WEB3J)) {
            queryTransaction(jsonObject.optString("tx"));
        } else if (url.contains(Constant.QUERY_TRANSCTION_URL)) {
            LogUtil.INSTANCE.i(url + ":  " + jsonObject.toString());
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

    private void queryTransaction(String tx) {
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        param.put("txhash", tx);
        request.requestGet(Constant.QUERY_TRANSCTION_URL + "?txhash=" + tx, null, this);
    }

    private void transferSucceed(String tx) {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        param.put("from", UserInfoManager.getInst().getCurrentWalletAddress());
        param.put("to", etAddress.getText().toString());
        param.put("value", tvValue.getText().toString());
        param.put("hash", tx);
        param.put("token", token);
        request.requestPost(Constant.SEND_BY_WEB3J, param, this);
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
