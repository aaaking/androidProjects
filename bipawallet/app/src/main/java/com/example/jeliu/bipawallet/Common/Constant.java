package com.example.jeliu.bipawallet.Common;

/**
 * Created by liuming on 12/05/2018.
 */

public class Constant {
    public static String BASE_URL = "https://wallet.bipa.io";//"http://192.168.1.131:16679";////http://47.52.224.7:16679";

    public static String CREATE_ACCOUNT_URL = BASE_URL + "/createAccount";
    //announcement
    public static String ANNOUNCEMENT_URL = BASE_URL + "/announcement";
    //tokenlist
    public static String TOKENLIST_URL = BASE_URL + "/tokenlist";

    //
    public static String IMPORT_KEYSTORE_URL = BASE_URL + "/importAccountByKS";
    //importAccountByP
    public static String IMPORT_PRIVATE_KEY_URL = BASE_URL + "/importAccountByPK";

    //balance
    public static String BALANCE_URL = BASE_URL + "/balance";
    public static String GET_SALT_IV = BASE_URL + "/getSaltIV";
    //estimateGas
    public static String ESTIMATEGAS_URL = BASE_URL + "/estimateGas";
    public static String GET_BINARY_URL = BASE_URL + "/getBinary";

    //sendETHToken
    public static String SEND_ETH_URL = BASE_URL + "/sendETHToken";

    //sendERC20Token
    public static String SEND_ERC_URL = BASE_URL + "/sendERC20Token";

    public static String SEND_BY_WEB3J = BASE_URL + "/sendTokenByWeb3j";

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

    public static String ADDRESS_WXC = "0x99c3a52653fa192606bfd8b9c276028022feb40e";

    //eos
    public static String TAG_EOS_WALLET = "tag-eos-wallet";//"038f4b0fc8ff18a4f0842a8f0564611f6e96e8535901dd45e43ac8691a1c4dca” //"https://nodes.get-scatter.com";//aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906
    public static String EOS_URL = "http://193.93.219.219:8888";//"https://nodes.get-scatter.com";//"http://192.168.1.120:8888";//
    public static final boolean DEFAULT_SKIP_SIGNING = false;

    public static final String DEFAULT_SERVANT_ACCOUNT = "eosio";

    public static final String  DEFAULT_WALLET_NAME = "default";
    public static final boolean DEFAULT_SAVE_PASSWORD= true;

    public static final String SAMPLE_PRIV_KEY_FOR_TEST = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";

    public static final String EOSIO_SYSTEM_ACCOUNT = "eosio";
    public static final String EOSIO_TOKEN_CONTRACT = "eosio.token";
    public static final int TX_EXPIRATION_IN_MILSEC = 30000;

    public static final String DEFAULT_SYMBOL_STRING = "SYS";
    public static final int DEFAULT_SYMBOL_PRECISION = 0;//4;
    public static final int SYMBOL_PRECISION = 4;

    public static final String EOS_SYMBOL_STRING = "EOS";
    public static final String EOS_NAME_REGEX = "^[1-5a-z]{12}$";
    public static final String MARKET_EOS_PRICE = "https://api.coinmarketcap.com/v2/ticker/1765";

    public static final String KEY_EOS_CONTRACT = "eos_contract";
    public static final String KEY_EOS_ACTION = "eos_action";
    public static final String KEY_EOS_DATA_JSON = "eos_data_json";
    public static final String KEY_EOS_PERMISSION = "eos_permission";
}
