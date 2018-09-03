package com.example.jeliu.bipawallet.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuming on 02/07/2018.
 */

public class HZWallet {
    public String name;
    public String fileName;
    public String address;
    public int profileIndex;
    public List<HZToken> tokenList = new ArrayList<>();
    public String privateKey;
    public String keyStore;

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof HZWallet && address != null && address.equals(((HZWallet) o).address);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : super.hashCode();
    }
}
