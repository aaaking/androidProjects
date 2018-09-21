package com.example.jeliu.bipawallet.Model;

import java.util.ArrayList;
import java.util.List;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;

/**
 * Created by liuming on 02/07/2018.
 */

public class HZWallet {
    public int type = WALLET_ETH;
    public String name;
    public String balance = "0";//balance except eth
    public String fileName;
    public String address;
    public int profileIndex;
    public List<HZToken> tokenList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof HZWallet && address != null && address.equals(((HZWallet) o).address);
    }

    @Override
    public int hashCode() {
        return (address + type).hashCode();
    }
}
