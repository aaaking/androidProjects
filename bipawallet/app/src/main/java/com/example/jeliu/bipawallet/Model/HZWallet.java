package com.example.jeliu.bipawallet.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuming on 02/07/2018.
 */

public class HZWallet {
    public String name;
    public String address;
    public int profileIndex;
    public List<HZToken> tokenList = new ArrayList<>();
    public String privateKey;
    public String keyStore;
}
