package com.example.jeliu.bipawallet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Model.HZToken;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuming on 19/05/2018.
 */

public class StepFragment extends BaseFragment {
    private String address;
    private String name;

    @BindView(R.id.textView_name)
    TextView tvName;

    @BindView(R.id.textView_address)
    TextView tvAddress;

    @BindView(R.id.tv_inpay)
    TextView tvInpay;

    @BindView(R.id.tv_outpay)
    TextView tvOutpay;

    @BindView(R.id.textView_money)
    TextView tvMoney;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, null);
        ButterKnife.bind(this, view);
        tvName.setText(name);
        tvAddress.setText(address);
        setupData();
        return view;
    }

    public void init(String address, String name) {
        this.address = address;
        this.name = name;
    }

    private void setupData() {
        refreshData();
    }

    public void refreshData() {
        HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (wallet != null && tvMoney != null) {
            double total = 0;
            for (HZToken token : wallet.tokenList) {
                double value = token.value;
                total += value;
            }
            tvMoney.setText(""+ total);
        }
    }

    public String getAddress() {
        return  address;
    }

    public void setOutpay(double outpay) {
        if (!isAdded()) {
            return;
        }
        tvOutpay.setText(String.format("%.02f", outpay));
    }
}
