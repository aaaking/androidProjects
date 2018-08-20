package com.example.jeliu.bipawallet.Network;

import org.web3j.crypto.Credentials;

/**
 * Created by 周智慧 on 2018/8/20.
 */
public interface IWallet {
    void onWalletResult(Credentials credentials);
}
