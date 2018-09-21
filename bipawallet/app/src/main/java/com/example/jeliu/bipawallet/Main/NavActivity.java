package com.example.jeliu.bipawallet.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.jeliu.bipawallet.Asset.AddNewAttentionActivity;
import com.example.jeliu.bipawallet.Asset.CreateWalletActivity;
import com.example.jeliu.bipawallet.Asset.ImportWalletActivity;
import com.example.jeliu.bipawallet.Asset.ManageWalletActivity;
import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Common.BottomNavigationViewHelper;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.FragmentFactory;
import com.example.jeliu.bipawallet.Common.PriceManager;
import com.example.jeliu.bipawallet.Fragment.AssetFragment;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Splash.WelcomeActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.ui.WalletTypeDialog;
import com.example.jeliu.eos.CreateEosWalletAC;
import com.example.jeliu.eos.ImportEosWalletAC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;

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

        BaseFragment fragment = FragmentFactory.createAssetFrg();
        switchFragment(R.id.content, null, fragment, null);
        requestPermission();
        tryPay();
    }

    public void tryPay() {
        if (UserInfoManager.getInst().isEmptyWallet()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        } else {
            if (getIntent().getData() != null && getIntent().getData().getQueryParameter("params") != null) {
//            if (getIntent().getStringExtra("js") != null) {
//                Uri js = Uri.parse(getIntent().getStringExtra("js"));
                scanDone(getIntent().getData().getQueryParameter("params"));
            }
        }
    }

    protected void initView() {

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
                           // holder.tvName.setText(jsonObj.getString(key));

                            BaseFragment fragment = FragmentFactory.getInstanceByIndex(0);
                            if (fragment instanceof AssetFragment) {
                                AssetFragment assetFragment = (AssetFragment)fragment;
                                assetFragment.refresh();
                            }
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
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            }
        });
    }

    protected void scanDone(String barcode) {
        drawerLayout.closeDrawers();
        if (!checkAddress(barcode)) {
            return;
        }
        BaseFragment fragment = FragmentFactory.getInstanceByIndex(currentIndex);
        if (fragment instanceof AssetFragment) {
            ((AssetFragment) fragment).gotoPay(barcode);
        }
    }

    private void refreshWallets() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (UserInfoManager.getInst().getJsonWallets().length() > 5) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (200*metrics.density));
            params.setMargins(0, (int)(40*metrics.density), 0, (int)(10*metrics.density));
            listView.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int)(40*metrics.density), 0, (int)(10*metrics.density));
            listView.setLayoutParams(params);
        }

        JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
        adapter.setContent(jsonArray);
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (address == null || address.length() == 0 && jsonArray.length() > 0) {
            HashMap<String, String> wallets = UserInfoManager.getInst().getWallets();
            for (String key : wallets.keySet()) {
                UserInfoManager.getInst().setCurrentWalletAddress(key);
                BaseFragment fragment = FragmentFactory.getInstanceByIndex(0);
                if (fragment instanceof AssetFragment) {
                    AssetFragment assetFragment = (AssetFragment)fragment;
                    assetFragment.refresh();
                }
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

            BaseFragment pre = FragmentFactory.getInstanceByIndex(currentIndex);
            int index = 0;
            switch (item.getItemId()) {
                case R.id.navigation_asset:
                    index = 0;
                    break;
                case R.id.navigation_record:
                    index = 1;
                    break;
                case R.id.navigation_contact:
                    index = 2;
                    break;
                case R.id.navigation_mine:
                    index = 3;
                    break;
            }
            if (index == currentIndex) {
                return true;
            }
            currentIndex = index;
            hideToolbar(currentIndex != 0);
            BaseFragment fragment = FragmentFactory.getInstanceByIndex(currentIndex);
            switchFragment(R.id.content, pre,
                    fragment, null);
            return true;
        }

    };

    private void hideToolbar(boolean hide) {
        if (!hide ) {
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
        if (from == null) {
        } else {
            transaction.remove(from);
        }
        if (to != null) {
            transaction.add(id, to, tag);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void createWallet() {
        WalletTypeDialog dialog = new WalletTypeDialog();
        dialog.setCallback(walletType -> {
            Intent i = new Intent(NavActivity.this, walletType == WALLET_ETH ? CreateWalletActivity.class : CreateEosWalletAC.class);
            startActivityForResult(i, Constant.create_wallet_request_code);
        });
        dialog.show(getSupportFragmentManager(), "WalletTypeDialog-create");
    }

    private void importWallet() {
        WalletTypeDialog dialog = new WalletTypeDialog();
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
        if(requestCode == Constant.create_wallet_request_code || requestCode == Constant.import_wallet_request_code || requestCode == Constant.manage_wallet_request_code) {
            if (resultCode == RESULT_OK) {
                if(requestCode == Constant.create_wallet_request_code || requestCode == Constant.import_wallet_request_code) {
                    BaseFragment fragment = FragmentFactory.getInstanceByIndex(0);
                    if (fragment instanceof AssetFragment) {
                        AssetFragment assetFragment = (AssetFragment)fragment;
                        assetFragment.refresh();
                    }
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
}
