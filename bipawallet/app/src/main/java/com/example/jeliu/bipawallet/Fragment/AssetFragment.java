package com.example.jeliu.bipawallet.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 05/05/2018.
 */

public class AssetFragment extends BaseFragment implements PriceChangedListener {
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
                String address = UserInfoManager.getInst().getCurrentWalletAddress();
                if (address != null) {
                    updateUI(address);
                }
            }
        }
    }

    private AmountAdapter adapter;
    private boolean moneyShow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset, null);

        ButterKnife.bind(this, view);
        setupView();
        loadData();
        PriceManager.getInst().addListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        PriceManager.getInst().removeListener(this);
        super.onDestroy();
    }

    public void refresh() {
        if (!isAdded()) {
            return;
        }
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {
            tvAddress.setText(address);
            String name = UserInfoManager.getInst().getCurrentWalletName();
            tvName.setText(name);
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            if (wallet != null) {
                ivProfile.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
            }
            loadData();
        } else {
            tvAddress.setText("");
            tvName.setText("");
            ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.default_boy));
        }
        ivLook.setImageDrawable(getResources().getDrawable(R.drawable.display));
    }

    private void setupView() {
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {
            tvAddress.setText(address);
            String name = UserInfoManager.getInst().getCurrentWalletName();
            tvName.setText(name);
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            if (wallet != null) {
                ivProfile.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
            }
        }

        adapter = new AmountAdapter(getActivity());
        listView.setAdapter(adapter);
        adapter.setContents(AttentionsManager.getInst().getAttentions());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TransferActivity.class);
//                try {
                String attentionName = AttentionsManager.getInst().getAttentions().get(i);
                boolean find = false;
                String address = UserInfoManager.getInst().getCurrentWalletAddress();
                HZWallet wallet = HZWalletManager.getInst().getWallet(address);
                if (wallet != null) {
                    List<HZToken> tokens = wallet.tokenList;
                    for (HZToken token : tokens) {
                        if (token.token.equalsIgnoreCase(attentionName)) {
                            find = true;
                            intent.putExtra("token", token.token);
                            intent.putExtra("value", token.value);
                            break;
                        }
                    }
                }
                if (!find) {
                    intent.putExtra("token", attentionName);
                    intent.putExtra("value", "0");
                }
                startActivity(intent);
            }
        });
    }

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    public void loadData() {
//        loadBalance();
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {
            showWaiting();
            HZHttpRequest request = new HZHttpRequest();
            request.requestGet(Constant.BALANCE_URL + "?address=" + address, null, this);
        }
    }

    private void loadBalance() {
        if (Common.mCredentials != null && Common.mCredentials.getAddress() != null) {
            try {
                EthGetBalance ethGetBalance = Common.getWeb3j().ethGetBalance(Common.mCredentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger wei = ethGetBalance.getBalance();
                BigDecimal balance = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
                String address = UserInfoManager.getInst().getCurrentWalletAddress();
//                HZWalletManager.getInst().updateWalletInfo(address, "");
                adapter.notifyDataSetChanged();
                updateUI(address);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setContents(AttentionsManager.getInst().getAttentions());
        }
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {

        } else {
            tvAddress.setText("");
            tvName.setText("");
        }
    }

    public void gotoPay(String scanCode) {
        try {
            JSONObject jsonObject = new JSONObject(scanCode);
            payToken = jsonObject.getString("token");
            uid = jsonObject.optString("uid");
            payAddress = jsonObject.getString("id");
            payValue = jsonObject.getDouble("value");
            loadGas();

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
            String tx = jsonObject.getString("tx");
            Common.showPaySucceed(getActivity(), llRoot, tx);
            if (uid == null || uid.trim().length() <= 0) {
                return;
            }
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
            request.requestPost("game.bipa.io/api/charge/platform", param, this);
//            request.requestPost("http://192.168.1.212:9999/charge/platform", param, this);
        } catch (JSONException e) {
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
            tvMoney.setText("" + total);
        }
    }

    private void loadGas() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.ESTIMATEGAS_URL, null, this);
    }


    private void showPay() {

        int gravity = Gravity.BOTTOM;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_popup_pay, null);
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
        boolean focusable = true;
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
        final View textEntryView = inflater.inflate(
                R.layout.layout_input_password, null);
        final EditText etPassword = (EditText) textEntryView.findViewById(R.id.editText_password);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setView(textEntryView);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String password = etPassword.getText().toString();
                        doPay(password);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //setTitle("");
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
        if (payToken.equalsIgnoreCase("eth")) {
//            request.requestPost(Constant.SEND_ETH_URL, param, this);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HZWallet wallet = HZWalletManager.getInst().getWallet(address);
                    try {
                        Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                        EthGetTransactionCount ethGetTransactionCount = Common.getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
                        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                        RawTransaction rawTransaction =
                                RawTransaction.createEtherTransaction(nonce, ManagedTransaction.GAS_PRICE,
                                        new BigDecimal(gasLimit).toBigInteger(), payAddress, Convert.toWei(new BigDecimal(payValue), Convert.Unit.ETHER).toBigInteger());
                        // sign & send our transaction
                        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                        String hexValue = Numeric.toHexString(signedMessage);
                        EthSendTransaction ethSendTransaction = Common.getWeb3j().ethSendRawTransaction(hexValue).send();//EthSendTransaction
                        Log.i("zzh", "https://rinkeby.etherscan.io/tx/" + ethSendTransaction.getTransactionHash());
                        final JSONObject js = new JSONObject();
                        js.put("tx", ethSendTransaction.getTransactionHash());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendToPlatformAfterPay(js, Constant.SEND_ETH_URL);
                            }
                        });
                    } catch (Exception e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        hideWaiting();
                        Looper.loop();
                    }
                    hideWaiting();
                }
            });
            thread.start();
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
            return contents.size();
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
            String attentionName = contents.get(i);
            boolean find = false;
            String address = UserInfoManager.getInst().getCurrentWalletAddress();
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            if (wallet != null) {
                List<HZToken> tokens = wallet.tokenList;
                for (HZToken token : tokens) {
                    if (token.token.equalsIgnoreCase(attentionName)) {
                        find = true;
                        holder.tvName.setText(token.token);
                        holder.tvMoney.setText(token.value + "");
                        holder.tvAbout.setText(PriceManager.getInst().tokenPrice(token.token, token.value) + "");
                        break;
                    }
                }
            }
            if (find) {
            } else {
                holder.tvName.setText(attentionName);
                holder.tvMoney.setText("0");
                holder.tvAbout.setText("0");
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
