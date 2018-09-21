package com.example.jeliu.bipawallet.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Asset.TransferActivity;
import com.example.jeliu.bipawallet.Asset.WithdrawActivity;
import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Common.AttentionsManager;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Common.PriceChangedListener;
import com.example.jeliu.bipawallet.Common.PriceManager;
import com.example.jeliu.bipawallet.Model.HZToken;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.contracts.Wxc;
import com.example.jeliu.bipawallet.ui.IPayEosSuccess;
import com.example.jeliu.bipawallet.ui.PayEosWindow;
import com.example.jeliu.bipawallet.util.LogUtil;
import com.example.jeliu.bipawallet.util.Util;
import com.example.jeliu.eos.data.EoscDataManager;
import com.example.jeliu.eos.ui.base.RxCallbackWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_EOS;
import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;
import static com.example.jeliu.bipawallet.util.ThreadUtilKt.Execute;

/**
 * Created by liuming on 05/05/2018.
 */

public class AssetFragment extends BaseFragment implements PriceChangedListener {
    PayEosWindow mPayEosWindow;
    String moneyStr = "0";
    @Inject
    EoscDataManager mDataManager;
    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.textView_address)
    TextView tvAddress;

    @BindView(R.id.textView_money)
    TextView tvMoney;

    @BindView(R.id.imageView_look)
    ImageView ivLook;

    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    @BindView(R.id.imageView_profile)
    ImageView ivProfile;

    private String payAddress;
    private String payToken;
    private String serialNum;
    private String uid;
    private double payValue;
    double gasLimit;
    double gasPrice;
    double currentGasPrice;

    @OnClick({R.id.imageView_scan, R.id.imageView_look})
    void onScan(View view) {
        if (view.getId() == R.id.imageView_scan) {
            Intent intent = new Intent(getActivity(), WithdrawActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.imageView_look) {
            moneyShow = !moneyShow;
            if (!moneyShow) {
                ivLook.setImageDrawable(getResources().getDrawable(R.drawable.hide));
                tvMoney.setText("***");
            } else {
                ivLook.setImageDrawable(getResources().getDrawable(R.drawable.display));
                tvMoney.setText(moneyStr);
            }
        }
    }

    private AmountAdapter adapter;
    private boolean moneyShow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        }
        View view = inflater.inflate(R.layout.fragment_asset, null);
        ButterKnife.bind(this, view);
        setupView();
        refresh();
        PriceManager.getInst().addListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        PriceManager.getInst().removeListener(this);
        super.onDestroy();
    }

    public void refresh() {
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (!isAdded() || address == null || address.trim().length() <= 0) {
            return;
        }
        ivLook.setImageDrawable(getResources().getDrawable(R.drawable.display));
        moneyStr = "0";
        tvAddress.setText(address);
        tvName.setText(UserInfoManager.getInst().getCurrentWalletName());
        tvMoney.setText(moneyStr);
        HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (wallet != null) {
            ivProfile.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
            if (WalletUtils.isValidAddress(address)) {
                loadEthData(address);
            } else if (wallet.type == WALLET_EOS) {
                if (getActivity() instanceof BaseActivity && mDataManager != null) {
                    showWaiting();
                    ((BaseActivity) getActivity()).addDisposable(mDataManager
                            .readAccountInfo(address)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new RxCallbackWrapper<JsonObject>(getActivity()) {
                                @Override
                                public void onNext(JsonObject jsonObject) {
                                    super.onNext(jsonObject);
                                    hideWaiting();
                                    if (jsonObject != null) {
                                        JsonElement jsonElement = jsonObject.get("core_liquid_balance");
                                        if (jsonElement != null) {
                                            moneyStr = jsonElement.getAsString().replace(" ", "").replace("EOS", "");
                                            tvMoney.setText(moneyStr);
                                            wallet.balance = moneyStr;
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    adapter.notifyDataSetChanged();
                                    LogUtil.INSTANCE.i(e.toString());
                                    hideWaiting();
                                }
                            }));
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setupView() {
        adapter = new AmountAdapter(getActivity());
        listView.setAdapter(adapter);
        adapter.setContents(AttentionsManager.getInst().getAttentions());
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String address = UserInfoManager.getInst().getCurrentWalletAddress();
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            if (wallet == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), TransferActivity.class);
            String attentionName = wallet.type == WALLET_ETH ? AttentionsManager.getInst().getAttentions().get(i) : wallet.type == WALLET_EOS ? "eos" : "";
            boolean find = false;
            List<HZToken> tokens = wallet.tokenList;
            for (HZToken token : tokens) {
                if (token.token.equalsIgnoreCase(attentionName)) {
                    find = true;
                    intent.putExtra("token", token.token);
                    intent.putExtra("value", token.value);
                    break;
                }
            }
            if (!find) {
                intent.putExtra("token", attentionName);
                intent.putExtra("value", "0");
            }
            startActivity(intent);
        });
    }

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    public void loadEthData(String address) {
//        loadBalance();
        if (WalletUtils.isValidAddress(address)) {
            showWaiting();
            HZHttpRequest request = new HZHttpRequest();
            request.requestGet(Constant.BALANCE_URL + "?address=" + address, null, this);
        }
    }

//    private void loadBalance() {
//        if (Common.mCredentials != null && Common.mCredentials.getAddress() != null) {
//            try {
//                EthGetBalance ethGetBalance = Common.getWeb3j().ethGetBalance(Common.mCredentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
//                BigInteger wei = ethGetBalance.getBalance();
//                BigDecimal balance = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
//                String address = UserInfoManager.getInst().getCurrentWalletAddress();
////                HZWalletManager.getInst().updateWalletInfo(address, "");
//                adapter.notifyDataSetChanged();
//                updateUI(address);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setContents(AttentionsManager.getInst().getAttentions());
        }
    }

    public void gotoPay(String scanCode) {
        LogUtil.INSTANCE.i("zzh-scanCode", scanCode);
        try {
            JSONObject jsonObject = new JSONObject(scanCode);
            payToken = jsonObject.getString("token");
            uid = jsonObject.optString("uid");
            serialNum = jsonObject.optString("serialNum");
            payAddress = jsonObject.getString("id");
            payValue = jsonObject.getDouble("value");
            if (WalletUtils.isValidAddress(payAddress)) {
                loadGas();
            } else if (payToken.toLowerCase().equals("eos") && getActivity() instanceof BaseActivity) {
                mPayEosWindow = new PayEosWindow(jsonObject, (BaseActivity) getActivity(), data -> {
                    final JSONObject js = new JSONObject();
                    try {
                        js.put("tx", data);
                        sendToPlatformAfterPay(js, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToastMessage(e.toString());
                    }
                });
                mPayEosWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToastMessage(getResources().getString(R.string.scan_error));
        }
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        hideWaiting();
        if (url.contains(Constant.BALANCE_URL)) {//{"code":0,"msg":"suc","balance":[{"token":"eth","value":18.679879996},{"token":"wxc","value":100}]}
            if (!super.onSuccess(jsonObject, url)) {
                return true;
            }
            try {
                JSONArray balance = jsonObject.getJSONArray("balance");
                String address = UserInfoManager.getInst().getCurrentWalletAddress();
                HZWalletManager.getInst().updateWalletInfo(address, balance);
                adapter.notifyDataSetChanged();
                updateUI(address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(Constant.ESTIMATEGAS_URL)) {
            if (!super.onSuccess(jsonObject, url)) {
                return true;
            }
            try {
                gasLimit = jsonObject.getDouble("gasLimit");
                UserInfoManager.getInst().gasLimited = gasLimit;
                gasPrice = jsonObject.getDouble("gasPrice");
                UserInfoManager.getInst().gasPrice = gasPrice;
                currentGasPrice = gasPrice;
                showPay();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(Constant.SEND_ETH_URL) || url.contains(Constant.SEND_ERC_URL)) {
            if (super.onSuccess(jsonObject, url)) {
                sendToPlatformAfterPay(jsonObject, url);
            } else {
                Common.showPayFailed(getActivity(), llRoot, payValue + "", payAddress);
            }
        }
        return false;
    }

    public void sendToPlatformAfterPay(JSONObject jsonObject, String url) {
        hideWaiting();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String time = String.valueOf(new Date().getTime());
            String tx = jsonObject.getString("tx");
            Common.showPaySucceed(getActivity(), llRoot, tx);
            HZHttpRequest request = new HZHttpRequest();
            Map<String, String> param = new HashMap<>();
            String address = UserInfoManager.getInst().getCurrentWalletAddress();
            param.put("from", address);
            param.put("to", payAddress);
            param.put("value", payValue + "");
            param.put("gasprice", currentGasPrice + "");
            param.put("gaslimit", gasLimit + "");
            param.put("token", payToken);
            param.put("type", "2");
            param.put("uid", uid);
            param.put("serialNumber", tx);
            //these for another api call
            param.put("hash", tx);
            param.put("serialNum", serialNum);
            param.put("time", time);
            md5.update((time + "bipa321" + tx).getBytes());
            param.put("sign", Util.bytesToHex(md5.digest()));
            // wallet node server
            request.requestPost(Constant.SEND_BY_WEB3J, param, this);
            if (uid != null && uid.trim().length() > 0) {
                request.requestPost("game.bipa.io/api/charge/platform", param, this);
            }
//            request.requestPost("http://192.168.1.212:1111/orders", param, this);
            LogUtil.INSTANCE.i("zzh", param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(String address) {
        HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (wallet != null) {
            double total = 0;
            for (HZToken token : wallet.tokenList) {
                double value = token.value;
                total += value;
            }
            moneyStr = String.valueOf(total);
            tvMoney.setText(moneyStr);
        }
    }

    private void loadGas() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.ESTIMATEGAS_URL, null, this);
    }


    private void showPay() {

        int gravity = Gravity.BOTTOM;

//        LayoutInflater inflater = getActivity().getLayoutInflater();//getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.layout_popup_pay, null);
        TextView tvMoney = popupView.findViewById(R.id.textView_money);
        tvMoney.setText(payValue + "");

        TextView tvAddress = popupView.findViewById(R.id.tv_address);
        tvAddress.setText(payAddress);

        TextView tvPay = popupView.findViewById(R.id.tv_pay);
        tvPay.setText(payToken);
        final TextView tvSeek = popupView.findViewById(R.id.textView_seek);
        Double d = new Double(gasPrice / Common.s_ether * gasLimit);
        String format = String.format("%f", d);
        tvSeek.setText(format + " ether");

        SeekBar seekBar;
        seekBar = popupView.findViewById(R.id.seekBar);
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

        Button btnPay = popupView.findViewById(R.id.button_pay);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // popupWindow.dismiss();
                return true;
            }
        });
        ImageView ivBack = popupView.findViewById(R.id.imageView_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                showInputPassword();
            }
        });

        popupWindow.showAtLocation(llRoot, gravity, 0, 0);
    }

    private void showInputPassword() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View textEntryView = inflater.inflate(R.layout.layout_input_password, null);
        final EditText etPassword = (EditText) textEntryView.findViewById(R.id.editText_password);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setView(textEntryView);
        builder.setPositiveButton(getString(R.string.ok),
                (dialog, whichButton) -> {
                    String password = etPassword.getText().toString();
                    doPay(password);
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        builder.show();
    }

    private void doPay(final String password) {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        Map<String, String> param = new HashMap<>();
        final String address = UserInfoManager.getInst().getCurrentWalletAddress();
        param.put("from", address);
        param.put("to", payAddress);
        param.put("password", password);
        param.put("value", payValue + "");
        param.put("gasprice", currentGasPrice + "");
        param.put("gaslimit", gasLimit + "");
        param.put("token", payToken);
        final HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (payToken.equalsIgnoreCase("eth")) {
//            request.requestPost(Constant.SEND_ETH_URL, param, this);
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    EthGetTransactionCount ethGetTransactionCount = Common.getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    RawTransaction rawTransaction =
                            RawTransaction.createEtherTransaction(nonce, Convert.toWei(String.valueOf(currentGasPrice), Convert.Unit.GWEI).toBigInteger(),
                                    new BigDecimal(gasLimit).toBigInteger(), payAddress, Convert.toWei(new BigDecimal(payValue), Convert.Unit.ETHER).toBigInteger());
                    // sign & send our transaction
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = Common.getWeb3j().ethSendRawTransaction(hexValue).send();//EthSendTransaction
                    String tx = ethSendTransaction.getTransactionHash();
                    LogUtil.INSTANCE.i("zzh", "https://rinkeby.etherscan.io/tx/" + tx);
                    if (tx == null) {
                        throw new Exception("transfer fail because txhash null");
                    }
                    final JSONObject js = new JSONObject();
                    js.put("tx", tx);
                    getActivity().runOnUiThread(() -> sendToPlatformAfterPay(js, Constant.SEND_ETH_URL));
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed_transfer) + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    hideWaiting();
                    Looper.loop();
                }
                hideWaiting();
            });
        } else if (payToken.equalsIgnoreCase("wxc")) {
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    Wxc contractWxc = Wxc.load(Constant.ADDRESS_WXC, Common.getWeb3j(), credentials, Convert.toWei(String.valueOf(currentGasPrice), Convert.Unit.GWEI).toBigInteger(), new BigDecimal(gasLimit).toBigInteger());
                    BigInteger decimal = contractWxc.decimals().send();
                    BigInteger rawValue = new BigInteger("10").pow(decimal.intValue());
                    TransactionReceipt transferReceipt = contractWxc.transfer(payAddress, rawValue).send();
                    String tx = transferReceipt.getTransactionHash();
                    LogUtil.INSTANCE.i("zzh", "https://rinkeby.etherscan.io/tx/" + tx);
                    if (tx == null) {
                        throw new Exception("transfer fail because txhash null");
                    }
                    final JSONObject js = new JSONObject();
                    js.put("tx", tx);
                    getActivity().runOnUiThread(() -> sendToPlatformAfterPay(js, Constant.SEND_ERC_URL));
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed_transfer) + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    hideWaiting();
                    Looper.loop();
                }
                hideWaiting();
            });
        } else {
            request.requestPost(Constant.SEND_ERC_URL, param, this);
        }
    }

    @Override
    public void priceChanged() {
        adapter.notifyDataSetChanged();
    }

    public class AmountAdapter extends BaseAdapter {
        protected Context context;
        protected LayoutInflater inflater;
        private List<String> contents = new ArrayList<>();

        public AmountAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        public void setContents(List<String> contents) {
            this.contents.clear();
            this.contents.addAll(contents);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            HZWallet wallet = HZWalletManager.getInst().getWallet(UserInfoManager.getInst().getCurrentWalletAddress());
            if (wallet == null || wallet.type == WALLET_ETH) {
                return contents.size();
            } else {
                return 1;
            }
        }

        @Override
        public Object getItem(int i) {
            return contents.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            UnitListHolder holder = null;
            if (null == view) {
                view = inflater.inflate(R.layout.layout_amount_item, null);
                holder = new UnitListHolder();
                holder.tvName = (TextView) view.findViewById(R.id.textView_name);
                holder.tvMoney = (TextView) view.findViewById(R.id.textView_money);
                holder.tvAbout = (TextView) view.findViewById(R.id.textView_about);
                view.setTag(holder);
            } else {
                holder = (UnitListHolder) view.getTag();
            }
            String address = UserInfoManager.getInst().getCurrentWalletAddress();
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            String attentionName = wallet != null && wallet.type == WALLET_EOS ? "eos" : contents.get(i);
            holder.tvName.setText(attentionName);
            holder.tvMoney.setText("0");
            holder.tvAbout.setText("0");
            if (wallet != null) {
                if (wallet.type == WALLET_ETH) {
                    for (HZToken token : wallet.tokenList) {
                        if (token.token.equalsIgnoreCase(attentionName)) {
                            holder.tvName.setText(token.token);
                            holder.tvMoney.setText(token.value + "");
                            holder.tvAbout.setText(PriceManager.getInst().tokenPrice(token.token, token.value) + "");
                            break;
                        }
                    }
                } else if (wallet.type == WALLET_EOS) {
                    holder.tvMoney.setText(wallet.balance);
                    holder.tvAbout.setText(PriceManager.getInst().getEosAssets(wallet.balance) + "");
                }
            }
            return view;
        }

        class UnitListHolder {
            public TextView tvName;
            public TextView tvMoney;
            public TextView tvAbout;
        }
    }
}
