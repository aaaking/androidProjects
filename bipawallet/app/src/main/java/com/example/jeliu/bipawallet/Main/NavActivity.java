package com.example.jeliu.bipawallet.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Asset.AddNewAttentionActivity;
import com.example.jeliu.bipawallet.Asset.CreateWalletActivity;
import com.example.jeliu.bipawallet.Asset.ImportWalletActivity;
import com.example.jeliu.bipawallet.Asset.ManageWalletActivity;
import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Common.BottomNavigationViewHelper;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.FragmentFactory;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Common.PriceManager;
import com.example.jeliu.bipawallet.Fragment.AssetFragment;
import com.example.jeliu.bipawallet.Fragment.ContactsFragment;
import com.example.jeliu.bipawallet.Fragment.MineFragment;
import com.example.jeliu.bipawallet.Fragment.RecordFragment;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Splash.WelcomeActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.contracts.Wxc;
import com.example.jeliu.bipawallet.ui.PushEosActionDialog;
import com.example.jeliu.bipawallet.ui.CallEthFuncDialog;
import com.example.jeliu.bipawallet.ui.IPayEosResult;
import com.example.jeliu.bipawallet.ui.PayEosWindow;
import com.example.jeliu.bipawallet.ui.WalletTypeDialog;
import com.example.jeliu.bipawallet.util.LogUtil;
import com.example.jeliu.bipawallet.util.Util;
import com.example.jeliu.eos.CreateEosWalletAC;
import com.example.jeliu.eos.ImportEosWalletAC;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_EOS;
import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;
import static com.example.jeliu.bipawallet.util.ThreadUtilKt.Execute;

/**
 * Created by liuming on 06/05/2018.
 */

public class NavActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView ivAdd;

    private ListView listView;
    private HeaderAdapter adapter;

    private FragmentManager fragmentManager;
    private int currentIndex = 0;

    private ListView menuList;
    private MenuAdapter menuAdapter;

    private AssetFragment mAsset;
    private RecordFragment mRecord;
    private ContactsFragment mContact;
    private MineFragment mMe;
    private BaseFragment mCurFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ivAdd = toolbar.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdd();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt("CurrentIndex", 0);
        }
        setupDrawer();
        setupNav();

        fragmentManager = getSupportFragmentManager();

        mAsset = FragmentFactory.createAssetFrg();
        mCurFrg = mAsset;
        switchFragment(R.id.content, null, mAsset, null);
        requestPermission();
        tryPay();
        LogUtil.INSTANCE.i("zzh-----------", "nav oncreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.INSTANCE.i("zzh-----------", "nav onNewIntent");
        tryPay();
    }

    protected void initView() {
    }

    String jsParams = null;

    public void tryPay() {
        if (UserInfoManager.getInst().isEmptyWallet()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        } else {
            if (getIntent().getData() != null && getIntent().getData().getQueryParameter("params") != null) {
//            if (getIntent().getStringExtra("js") != null) {
//                Uri js = Uri.parse(getIntent().getStringExtra("js"));
                jsParams = getIntent().getData().getQueryParameter("params");
                scanDone(jsParams);
            }
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("CurrentIndex", currentIndex);
    }

    private void setupListview(NavigationView view) {
//        View headerLayout = view.getHeaderView(0);
//        if (headerLayout == null) {
//            return;
//        }

        listView = view.findViewById(R.id.listview_account);
        adapter = new HeaderAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
                if (i < jsonArray.length()) {
                    try {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Iterator<?> iterator = jsonObj.keys();// 应用迭代器Iterator 获取所有的key值
                        while (iterator.hasNext()) { // 遍历每个key
                            String key = (String) iterator.next();
                            UserInfoManager.getInst().setCurrentWalletAddress(key);
                            drawerLayout.closeDrawers();
                            if (mAsset == null) {
                                mAsset = FragmentFactory.createAssetFrg();
                            }
                            mAsset.refresh();
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        refreshWallets();

        menuList = view.findViewById(R.id.menuList);
        menuAdapter = new MenuAdapter(this);
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getString(R.string.scan));
            jsonObject.put("photo", R.drawable.scan);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getString(R.string.manage));
            jsonObject.put("photo", R.drawable.manage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getString(R.string.create));
            jsonObject.put("photo", R.drawable.create);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getString(R.string.importWallet));
            jsonObject.put("photo", R.drawable.export);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);

        menuList.setAdapter(menuAdapter);
        menuAdapter.setContent(jsonArray);
        menuList.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (i == 0) {
                // Handle the camera action
                scanCode();
            } else if (i == 1) {
                manageWallet();
            } else if (i == 2) {
                createWallet();
            } else if (i == 3) {
                importWallet();
            }
        });
    }

    protected void scanDone(String barcode) {
        drawerLayout.closeDrawers();
        if (!checkAddress(barcode)) {
            return;
        }
//        BaseFragment fragment = FragmentFactory.getInstanceByIndex(currentIndex);
//        if (fragment instanceof AssetFragment) {
//            ((AssetFragment) fragment).gotoPay(barcode);
//        }
        gotoPay(barcode);
    }

    private void refreshWallets() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (UserInfoManager.getInst().getJsonWallets().length() > 5) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (200 * metrics.density));
            params.setMargins(0, (int) (40 * metrics.density), 0, (int) (10 * metrics.density));
            listView.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int) (40 * metrics.density), 0, (int) (10 * metrics.density));
            listView.setLayoutParams(params);
        }

        JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
        adapter.setContent(jsonArray);
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address == null || address.length() == 0 && jsonArray.length() > 0) {
            HashMap<String, String> wallets = UserInfoManager.getInst().getWallets();
            for (String key : wallets.keySet()) {
                UserInfoManager.getInst().setCurrentWalletAddress(key);
                if (mAsset == null) {
                    mAsset = FragmentFactory.createAssetFrg();
                }
                mAsset.refresh();
                break;
            }
        }
    }

    protected void onResume() {
        super.onResume();
        if (PriceManager.getInst().usd2rmb == 1) {
            PriceManager.getInst().setup();
        }
        refreshWallets();
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupNav() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationViewHelper.disableShiftMode(navigation);

        setupListview(navigationView);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int index = 0;
            BaseFragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_asset:
                    index = 0;
                    if (mAsset == null) {
                        mAsset = FragmentFactory.createAssetFrg();
                    }
                    fragment = mAsset;
                    break;
                case R.id.navigation_record:
                    index = 1;
                    if (mRecord == null) {
                        mRecord = FragmentFactory.createRecordFrg();
                    }
                    fragment = mRecord;
                    break;
                case R.id.navigation_contact:
                    if (mContact == null) {
                        mContact = FragmentFactory.createContactsFragment();
                    }
                    fragment = mContact;
                    index = 2;
                    break;
                case R.id.navigation_mine:
                    if (mMe == null) {
                        mMe = FragmentFactory.createMineFragment();
                    }
                    fragment = mMe;
                    index = 3;
                    break;
            }
            if (index == currentIndex) {
                return true;
            }
            currentIndex = index;
            hideToolbar(currentIndex != 0);
            switchFragment(R.id.content, mCurFrg, fragment, null);
            return true;
        }

    };

    private void hideToolbar(boolean hide) {
        if (!hide) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.syncState();
            toolbar.setVisibility(View.VISIBLE);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerToggle.syncState();
            toolbar.setVisibility(View.GONE);
        }
    }

    private void switchFragment(int id, BaseFragment from, BaseFragment to, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (from != null) {
            transaction.remove(from);
        }
        if (to != null) {
            transaction.add(id, to, tag);
        }
        mCurFrg = to;
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void createWallet() {
        WalletTypeDialog dialog = new WalletTypeDialog();
        Bundle fs = new Bundle();
        fs.putString("aaaa", "aaaaV");
        dialog.setArguments(fs);
        dialog.setCallback(walletType -> {
            Intent i = new Intent(NavActivity.this, walletType == WALLET_ETH ? CreateWalletActivity.class : CreateEosWalletAC.class);
            startActivityForResult(i, Constant.create_wallet_request_code);
        });
        dialog.show(getSupportFragmentManager(), "WalletTypeDialog-create");
    }

    private void importWallet() {
        WalletTypeDialog dialog = new WalletTypeDialog();
        Bundle fs = new Bundle();
        fs.putString("aaaa", "aaaaV");
        dialog.setArguments(fs);
        dialog.setCallback(walletType -> {
            Intent i = new Intent(NavActivity.this, walletType == WALLET_ETH ? ImportWalletActivity.class : ImportEosWalletAC.class);
            startActivityForResult(i, Constant.import_wallet_request_code);
        });
        dialog.show(getSupportFragmentManager(), "WalletTypeDialog-import");
    }

    private void manageWallet() {
        Intent i = new Intent(NavActivity.this, ManageWalletActivity.class);
        //manage_wallet_request_code
        //startActivity(i);
        startActivityForResult(i, Constant.manage_wallet_request_code);
    }

    private void onAdd() {
        Intent i = new Intent(NavActivity.this, AddNewAttentionActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get selected images from selector
        if (requestCode == Constant.create_wallet_request_code || requestCode == Constant.import_wallet_request_code || requestCode == Constant.manage_wallet_request_code) {
            if (resultCode == RESULT_OK) {
                if (requestCode == Constant.create_wallet_request_code || requestCode == Constant.import_wallet_request_code) {
                    if (mAsset == null) {
                        mAsset = FragmentFactory.createAssetFrg();
                    }
                    mAsset.refresh();
                }

                refreshWallets();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    PayEosWindow mPayEosWindow;
    private String payAddress;
    private String payToken;
    private int chain_type;
    private String serialNum;
    private String uid;
    private double payValue;
    double gasLimit;
    double gasPrice;
    double currentGasPrice;
    //below is to call eth function
    private String func_name;
    private String inputs;
    private String eth_contract_binary;
    //below is to push eos action
    private String eos_contract;
    private String eos_permission;

    public void gotoPay(String scanCode) {
        LogUtil.INSTANCE.i("zzh-scanCode", scanCode);
        final String address = UserInfoManager.getInst().getCurrentWalletAddress();
        try {
            JSONObject jsonObject = new JSONObject(scanCode);
            payToken = jsonObject.getString("token");
            chain_type = jsonObject.optInt("chain_type", WALLET_ETH);
            uid = jsonObject.optString("uid");
            serialNum = jsonObject.optString("serialNum");
            payAddress = jsonObject.getString("id");
            payValue = jsonObject.getDouble("value");
            //call contract function
            eos_contract = jsonObject.optString(Constant.KEY_EOS_CONTRACT);
            func_name = jsonObject.optString(Constant.KEY_EOS_ACTION);
            inputs = jsonObject.optString(Constant.KEY_EOS_DATA_JSON);
            eos_permission = jsonObject.optString(Constant.KEY_EOS_PERMISSION);
            if (chain_type == WALLET_ETH && WalletUtils.isValidAddress(payAddress) && payToken.toLowerCase().equals("eth")) {
                if (!WalletUtils.isValidAddress(address)) {
                    Toast.makeText(this, R.string.current_wallet_not_eth, Toast.LENGTH_LONG).show();
                } else if (!TextUtils.isEmpty(func_name)) {
                    queryContratcBinaryCode();
                } else {
                    loadGas();
                }
            } else if (chain_type == WALLET_EOS && payToken.toLowerCase().equals("eos")) {
                IPayEosResult callback = new IPayEosResult() {
                    @Override
                    public void payEosSuccess(@NotNull Object data) {
                        final JSONObject js = new JSONObject();
                        try {
                            js.put("tx", data);
                            sendToPlatformAfterPay(js, null, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToastMessage(e.toString());
                        }
                    }
                    @Override
                    public void payError(@NotNull String error) {
                        Common.showPayFailed(NavActivity.this, findViewById(R.id.container), payValue + "", payAddress);
                    }
                };
                if (TextUtils.isEmpty(eos_contract) || TextUtils.isEmpty(func_name)) {
                    mPayEosWindow = new PayEosWindow(jsonObject, this, callback);
                    findViewById(R.id.container).post(() -> mPayEosWindow.showAtLocation(findViewById(R.id.container), Gravity.BOTTOM, 0, 0));
                } else {
                    PushEosActionDialog dialog = new PushEosActionDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_EOS_CONTRACT, eos_contract);
                    bundle.putString(Constant.KEY_EOS_ACTION, func_name);
                    bundle.putString(Constant.KEY_EOS_DATA_JSON, inputs);
                    bundle.putString(Constant.KEY_EOS_PERMISSION, eos_permission);
                    dialog.setArguments(bundle);
                    dialog.setCallback(callback);
                    findViewById(R.id.container).post(() -> dialog.show(getSupportFragmentManager(), "PushEosActionDialog"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToastMessage(getResources().getString(R.string.scan_error));
        }
    }

    private void loadGas() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.ESTIMATEGAS_URL, null, this);
    }

    private void queryContratcBinaryCode() {
        showWaiting();
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.GET_BINARY_URL + "?address=" + payAddress, null, this);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        hideWaiting();
        if (url.contains(Constant.ESTIMATEGAS_URL)) {
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
        } else if (url.contains(Constant.GET_BINARY_URL)) {
            if (!super.onSuccess(jsonObject, url)) {
                return true;
            }
            eth_contract_binary = jsonObject.optString("msg");
            showCallEthFuncDialog();
        }
        return true;
    }

    private void showCallEthFuncDialog() {
        CallEthFuncDialog dialog = new CallEthFuncDialog(this);
        dialog.setFuncName(func_name).setConrtactAddress(payAddress).setFuncParams(inputs).setBinary(eth_contract_binary).show();
    }

    private void showPay() {
        int gravity = Gravity.BOTTOM;
        View popupView = getLayoutInflater().inflate(R.layout.layout_popup_pay, null);
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
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // popupWindow.dismiss();
                return true;
            }
        });
        ImageView ivBack = popupView.findViewById(R.id.imageView_back);
        ivBack.setOnClickListener(view -> popupWindow.dismiss());
        btnPay.setOnClickListener(view -> {
            popupWindow.dismiss();
            showInputPassword();
        });
        findViewById(R.id.container).post(() -> popupWindow.showAtLocation(findViewById(R.id.container), gravity, 0, 0));
    }

    private void showInputPassword() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View textEntryView = inflater.inflate(R.layout.layout_input_password, null);
        final EditText etPassword = (EditText) textEntryView.findViewById(R.id.editText_password);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setView(textEntryView);
        builder.setPositiveButton(getString(R.string.ok),
                (dialog, whichButton) -> {
                    String password = etPassword.getText().toString();
                    doPay(password);
                });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
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
        final HZWallet wallet = HZWalletManager.getInst().getWallet(address);
        if (payToken.equalsIgnoreCase("eth")) {
//            request.requestPost(Constant.SEND_ETH_URL, param, this);
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    EthGetTransactionCount ethGetTransactionCount = Common.getWeb3j().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    RawTransaction rawTransaction =
                            RawTransaction.createEtherTransaction(nonce, Convert.toWei(String.valueOf(currentGasPrice), Convert.Unit.GWEI).toBigInteger(),
                                    new BigDecimal(gasLimit).toBigInteger(), payAddress, Convert.toWei(new BigDecimal(payValue), Convert.Unit.ETHER).toBigInteger());
                    // sign & send our transaction
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = Common.getWeb3j().ethSendRawTransaction(hexValue).send();//EthSendTransaction
                    String tx = ethSendTransaction.getTransactionHash();
                    LogUtil.INSTANCE.i("zzh", "https://rinkeby.etherscan.io/tx/" + tx);
                    if (tx == null) {
                        Common.showPayFailed(this, findViewById(R.id.container), payValue + "", payAddress);
                        throw new Exception("transfer fail because txhash null");
                    }
                    final JSONObject js = new JSONObject();
                    js.put("tx", tx);
                    runOnUiThread(() -> sendToPlatformAfterPay(js, Constant.SEND_ETH_URL, true));
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(this, getResources().getString(R.string.failed_transfer) + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    hideWaiting();
                    Looper.loop();
                }
                hideWaiting();
            });
        } else if (payToken.equalsIgnoreCase("wxc")) {
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    Wxc contractWxc = Wxc.load(Constant.ADDRESS_WXC, Common.getWeb3j(), credentials, Convert.toWei(String.valueOf(currentGasPrice), Convert.Unit.GWEI).toBigInteger(), new BigDecimal(gasLimit).toBigInteger());
                    BigInteger decimal = contractWxc.decimals().send();
                    BigInteger rawValue = new BigInteger("10").pow(decimal.intValue());
                    TransactionReceipt transferReceipt = contractWxc.transfer(payAddress, rawValue).send();
                    String tx = transferReceipt.getTransactionHash();
                    LogUtil.INSTANCE.i("zzh", "https://rinkeby.etherscan.io/tx/" + tx);
                    if (tx == null) {
                        Common.showPayFailed(this, findViewById(R.id.container), payValue + "", payAddress);
                        throw new Exception("transfer fail because txhash null");
                    }
                    final JSONObject js = new JSONObject();
                    js.put("tx", tx);
                    runOnUiThread(() -> sendToPlatformAfterPay(js, Constant.SEND_ERC_URL, true));
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(this, this.getResources().getString(R.string.failed_transfer) + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    hideWaiting();
                    Looper.loop();
                }
                hideWaiting();
            });
        } else {
            request.requestPost(Constant.SEND_ERC_URL, param, this);
        }
    }

    public void sendToPlatformAfterPay(JSONObject jsonObject, String url, boolean showSuccDialog) {
        hideWaiting();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String time = String.valueOf(new Date().getTime());
            String tx = jsonObject.getString("tx");
            if (showSuccDialog) {
                Common.showPaySucceed(this, findViewById(R.id.container), tx, null);
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
            param.put("chain_type", String.valueOf(chain_type));
            param.put("type", "2");
            param.put("uid", uid);
            param.put("serialNumber", tx);
            //these for another api call
            param.put("hash", tx);
            param.put("serialNum", serialNum);
            param.put("time", time);
            md5.update((time + "bipa321" + tx).getBytes());
            param.put("sign", Util.bytesToHex(md5.digest()));
            // wallet node server
            request.requestPost(Constant.SEND_BY_WEB3J, param, this);
            if (uid != null && uid.trim().length() > 0) {
                request.requestPost("http://game.bipa.io/api/charge/platform", param, this);
//                request.requestPost("http://192.168.1.212:9999/charge/platform", param, this);
            }
            request.requestPost("http://192.168.1.212:1111/orders", param, this);
            LogUtil.INSTANCE.i("zzh", param.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
