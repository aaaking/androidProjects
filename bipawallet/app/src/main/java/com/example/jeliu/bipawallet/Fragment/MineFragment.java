package com.example.jeliu.bipawallet.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Mine.AboutActivity;
import com.example.jeliu.bipawallet.Mine.LoginActivity;
import com.example.jeliu.bipawallet.Mine.MessageCenterActivity;
import com.example.jeliu.bipawallet.Mine.SettingActivity;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.Webview.WebviewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 05/05/2018.
 */

public class MineFragment extends BaseFragment {

    @BindView(R.id.textView_wallet_name)
    TextView tvWalletName;

    @BindView(R.id.textView_name)
    TextView tvName;

    @BindView(R.id.imageView_wallet_pic)
    ImageView ivProfile;

    @OnClick(R.id.rl_mine) void gotoLogin() {
        // TODO call server...
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
    }
    //rl_help
    @OnClick(R.id.rl_help) void gotoHelp() {
        // TODO call server...
        Intent i = new Intent(getActivity(), WebviewActivity.class);
        i.putExtra("url", Common.getCenterUrl());
        startActivity(i);
    }

    @OnClick(R.id.rl_system) void gotoSetting() {
        // TODO call server...
        Intent i = new Intent(getActivity(), SettingActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.rl_about) void gotoAbout() {
        // TODO call server...
        Intent i = new Intent(getActivity(), AboutActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.rl_message) void gotoMessage() {
        Intent i = new Intent(getActivity(), MessageCenterActivity.class);
        startActivity(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null);
        ButterKnife.bind(this, view);
        setupView();
        return view;
    }

    private void setupView() {
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address != null) {
            tvWalletName.setText(UserInfoManager.getInst().getCurrentWalletName());
            tvName.setText(address);
            HZWallet wallet = HZWalletManager.getInst().getWallet(address);
            if (wallet != null) {
                ivProfile.setImageDrawable(getActivity().getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
            }
        }
    }

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }
}
