package com.example.jeliu.bipawallet.Common;

/**
 * Created by liuming on 12/05/2018.
 */

public class Constant {
    //network
    //http://47.52.224.7
    //https://wallet.bipa.io
    public static String BASE_URL = "http://47.52.224.7:16679";

    public static String CREATE_ACCOUNT_URL = BASE_URL + "/createAccount";
    //announcement
    public static String ANNOUNCEMENT_URL = BASE_URL + "/announcement";
    //tokenlist
    public static String TOKENLIST_URL = BASE_URL + "/tokenlist";

    //
    public static String IMPORT_KEYSTORE_URL = BASE_URL + "/importAccountByKS";
    //importAccountByPK
    public static String IMPORT_PRIVATE_KEY_URL = BASE_URL + "/importAccountByPK";

    //balance
    public static String BALANCE_URL = BASE_URL + "/balance";
    //estimateGas
    public static String ESTIMATEGAS_URL = BASE_URL + "/estimateGas";

    //sendETHToken
    public static String SEND_ETH_URL = BASE_URL + "/sendETHToken";

    //sendERC20Token
    public static String SEND_ERC_URL = BASE_URL + "/sendERC20Token";

    //queryTransction
    public static String QUERY_TRANSCTION_URL = BASE_URL + "/queryTransaction";

//    HTTP GET /transctions
    public static String TRANSCTION_URL = BASE_URL + "/transactions";

    public static String GET_PRIVATEKEY = BASE_URL + "/getPrivatekey";

    public static String GET_KEYSTORE = BASE_URL + "/getKeystore";
//sendRawTransaction
    public static String SEND_RAW_TRANSACTION = BASE_URL + "/sendRawTransaction";
    //utility

    //apkVersion
    public static String APKVERSION = BASE_URL + "/apkVersion";

    //delAccount
    public static String DELETEACCOUNT = BASE_URL + "/delAccount";

    //coinPriceURLs
    public static String COIN_PRICE_URLS = BASE_URL + "/coinPriceURLs";

    //usd2cnyPrice

    public static String USD_CNY_PRICE = BASE_URL + "/usd2cnyPrice";
    public static String BALANCE_CHART = BASE_URL + "/balanceChart";

    //balanceChart

    public static int create_wallet_request_code = 101;
    public static int import_wallet_request_code = 102;
    public static int manage_wallet_request_code = 103;
}