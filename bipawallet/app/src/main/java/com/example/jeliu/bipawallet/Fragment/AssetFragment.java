package com.example.jeliu.bipawallet.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Asset.TransferActivity;
import com.example.jeliu.bipawallet.Asset.WithdrawActivity;
import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Common.AttentionsManager;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Common.PriceChangedListener;
import com.example.jeliu.bipawallet.Common.PriceManager;
import com.example.jeliu.bipawallet.Model.HZToken;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.util.LogUtil;
import com.example.jeliu.eos.data.EoscDataManager;
import com.example.jeliu.eos.ui.base.RxCallbackWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_EOS;
import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;

/**
 * Created by liuming on 05/05/2018.
 */

public class AssetFragment extends BaseFragment implements PriceChangedListener {
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
        }
        return false;
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
