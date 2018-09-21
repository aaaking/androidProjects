package com.example.jeliu.bipawallet.Asset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Fragment.ExportPrivateKeyFragment;
import com.example.jeliu.bipawallet.Model.HZToken;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Splash.WelcomeActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;
import com.example.jeliu.bipawallet.bipacredential.BipaCredential;
import com.example.jeliu.bipawallet.bipacredential.BipaWalletFile;
import com.example.jeliu.bipawallet.util.CacheConstantKt;
import com.example.jeliu.eos.crypto.ec.EosPrivateKey;
import com.example.jeliu.eos.crypto.ec.EosPublicKey;
import com.example.jeliu.eos.data.EoscDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_EOS;
import static com.example.jeliu.bipawallet.ui.WalletTypeDialogKt.WALLET_ETH;
import static com.example.jeliu.bipawallet.util.ThreadUtilKt.Execute;

/**
 * Created by liuming on 06/05/2018.
 */

public class WalletNameActivity extends BaseActivity {
    @Inject
    EoscDataManager mDataManager;
    HZWallet wallet;
    int showType = ExportPrivateKeyFragment.TYPE_SHOW_ETH_PK;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    @BindView(R.id.textview_name)
    TextView tvName;

    @BindView(R.id.textview_address)
    TextView tvAddress;

    @BindView(R.id.textView_money)
    TextView tvMoney;

    @BindView(R.id.rl_keystore)
    View rl_keystore;

    @BindView(R.id.imageView_profile)
    ImageView ivProfile;

    private String walletAddress;

    @OnClick({R.id.rl_private_key, R.id.rl_keystore, R.id.button_delete})
    void onClick(View view) {
//        if (true) {
//            ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
//            fragment.setShowType(showType);
//            if (showType == ExportPrivateKeyFragment.TYPE_SHOW_EOS_PK) {
//                fragment.setEosKeys(null);
//            } else {
//                fragment.setPrivateKey("挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp");
//                fragment.setSafePrivateKey("挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp挨耳光hi爱国好哇诶过后瓦尔好狗好娃儿规划范围安排好更怕我会kgeawghpawehrgp");
//            }
//            fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
//            return;
//        }
        if (view.getId() == R.id.rl_private_key) {
            showType = showType == ExportPrivateKeyFragment.TYPE_SHOW_EOS_PK ? showType : ExportPrivateKeyFragment.TYPE_SHOW_ETH_PK;
            showInputPassword(0);
        } else if (view.getId() == R.id.rl_keystore) {
            showType = ExportPrivateKeyFragment.TYPE_SHOW_ETH_KS;
            showInputPassword(1);
        } else {
            showInputPassword(2);
        }
    }

    private void deleteWalletImp() {
        UserInfoManager.getInst().deleteWallet(walletAddress);
        JSONArray jsonArray = UserInfoManager.getInst().getJsonWallets();
        if (jsonArray.length() == 0) {
            Intent intent = new Intent(WalletNameActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    //flag 0 : export private key
    // 1 : export key store
    // 2 : delete
    private void showInputPassword(final int flag) {
        final View textEntryView = LayoutInflater.from(this).inflate(R.layout.layout_input_password, null);
        final EditText etPassword = (EditText) textEntryView.findViewById(R.id.editText_password);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("");
        builder.setView(textEntryView);
        builder.setPositiveButton(getString(R.string.ok),
                (dialog, whichButton) -> {
                    String password = etPassword.getText().toString();
                    if (flag == 0) {
                        exportPrivateKey(password);
                    } else if (flag == 1) {
                        exportKeystore(password);
                    } else if (flag == 2) {
                        deleteWallet(password);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                (dialog, whichButton) -> {
                    //setTitle("");
                });
        builder.show();
    }

    private void exportPrivateKey(final String password) {
        showWaiting();
        ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
        fragment.setShowType(showType);
        if (showType == ExportPrivateKeyFragment.TYPE_SHOW_EOS_PK) {
            hideWaiting();

            mDataManager.getWalletManager().unlock(wallet.name, password);
            if (mDataManager.getWalletManager().isLocked(wallet.name)) {
                showToastMessage("invalid password");
                return;
            }
            ArrayList fs = new ArrayList<EosPrivateKey>();
            Map<EosPublicKey, String> keys = mDataManager.getWalletManager().listKeys(wallet.name);
            for (Map.Entry<EosPublicKey, String> entry : keys.entrySet()) {
                fs.add(new EosPrivateKey(entry.getValue()));
            }
            fragment.setEosKeys(fs);
            fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
        } else {
            Execute(() -> {
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                    if (credentials != null) {
                        //
                        SharedPreferences sp = CacheConstantKt.getSAppContext().getSharedPreferences(BipaCredential.SP_SAFE_BIPA, 0);
                        Gson gson = new Gson();
                        String storedHashMapString = sp.getString(credentials.getAddress().toLowerCase().substring(2), "");
                        java.lang.reflect.Type type = new TypeToken<BipaWalletFile>() {
                        }.getType();
                        BipaWalletFile bipaWalletFile = gson.fromJson(storedHashMapString, type);
                        String safePK = BipaCredential.getSafePK(bipaWalletFile, password);

                        fragment.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
                        fragment.setSafePrivateKey(safePK);
                        fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
                    }
                } catch (Exception e) {
                    hideWaiting();
                    Looper.prepare();
                    Toast.makeText(WalletNameActivity.this, "异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                hideWaiting();
            });
        }
//        HZHttpRequest request = new HZHttpRequest();
//        Map<String, String> param = new HashMap<>();
//        param.put("address", walletAddress);
//        param.put("password", password);
//        request.requestPost(Constant.GET_PRIVATEKEY, param, this);
    }

    private void exportKeystore(final String password) {
        showWaiting();
        Execute(() -> {
            try {
                Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                File file = new File(Common.WALLET_PATH + File.separator + wallet.fileName);
                if (credentials == null || !file.exists() || file.length() <= 0) {
                    return;
                }
                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                //
                SharedPreferences sp = CacheConstantKt.getSAppContext().getSharedPreferences(BipaCredential.SP_SAFE_BIPA, 0);
//                    Gson gson = new Gson();
                String storedHashMapString = sp.getString(credentials.getAddress().toLowerCase().substring(2), "");
//                    java.lang.reflect.Type type = new TypeToken<BipaWalletFile>() {
//                    }.getType();
//                    BipaWalletFile bipaWalletFile = gson.fromJson(storedHashMapString, type);

                ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
                fragment.setShowType(showType);
                fragment.setPrivateKey(text.toString());
                fragment.setSafePrivateKey(storedHashMapString);
                fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
            } catch (Exception e) {
                hideWaiting();
                Looper.prepare();
                Toast.makeText(WalletNameActivity.this, "异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            hideWaiting();
        });
//        HZHttpRequest request = new HZHttpRequest();
//        Map<String, String> param = new HashMap<>();
//        param.put("password", password);
//        param.put("address", walletAddress);
//        request.requestPost(Constant.GET_KEYSTORE, param, this);
    }

    private void deleteWallet(final String password) {
        showWaiting();
        Execute(() -> {
            try {
                Credentials credentials = WalletUtils.loadCredentials(password, Common.WALLET_PATH + File.separator + wallet.fileName);
                File file = new File(Common.WALLET_PATH + File.separator + wallet.fileName);
                if (credentials == null || !file.exists() || file.length() <= 0) {
                    return;
                }
                SharedPreferences sp = CacheConstantKt.getSAppContext().getSharedPreferences(BipaCredential.SP_SAFE_BIPA, 0);
                SharedPreferences.Editor localEditor = sp.edit();
                localEditor.remove(credentials.getAddress().toLowerCase().substring(2));
                localEditor.apply();

                file.delete();
                deleteWalletImp();
            } catch (Exception e) {
                hideWaiting();
                Looper.prepare();
                Toast.makeText(WalletNameActivity.this, "异常：" + e.toString(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            hideWaiting();
        });
//        HZHttpRequest request = new HZHttpRequest();
//        Map<String, String> param = new HashMap<>();
//        param.put("address", walletAddress);
//        param.put("password", password);
//        request.requestPost(Constant.DELETEACCOUNT, param, this);
    }

    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
//        if (url.contains(Constant.GET_PRIVATEKEY)) {
//            ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
//            try {
//                String privateKey = jsonObject.getString("privatekey");
//                fragment.setPrivateKey(privateKey, false);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
//        } else if (url.contains(Constant.GET_KEYSTORE)) {
//            ExportPrivateKeyFragment fragment = new ExportPrivateKeyFragment();
//            try {
//                String privateKey = jsonObject.getString("keystore");
//                fragment.setPrivateKey(privateKey, true);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            fragment.show(getSupportFragmentManager(), "ExportPrivateKey");
//        } else if (url.contains(Constant.DELETEACCOUNT)) {
//            deleteWalletImp();
//        } else
        if (url.contains(Constant.BALANCE_URL)) {
            try {
                JSONArray balance = jsonObject.getJSONArray("balance");
                HZWalletManager.getInst().updateWalletInfo(walletAddress, balance);
                updateUI(walletAddress, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.acivity_wallet_name);
        ButterKnife.bind(this);
        setupView();
    }

    private void updateUI(String address, boolean shouldRefresh) {
        if (wallet != null && wallet.type == WALLET_ETH) {
            if (wallet.tokenList.size() == 0 && shouldRefresh) {
                showWaiting();
                HZHttpRequest request = new HZHttpRequest();
                request.requestGet(Constant.BALANCE_URL + "?address=" + address, null, this);
            } else {
                double total = 0;
                for (HZToken token : wallet.tokenList) {
                    double value = token.value;
                    total += value;
                }
                tvMoney.setText("" + total);
            }
        }
    }

    void setupView() {
        String name = getIntent().getStringExtra("name");
        setTitle(name);
        walletAddress = getIntent().getStringExtra("address");
        wallet = HZWalletManager.getInst().getWallet(walletAddress);
        if (wallet != null && wallet.type == WALLET_EOS) {
            showType = ExportPrivateKeyFragment.TYPE_SHOW_EOS_PK;
        }
        showBackButton();
        rl_keystore.setVisibility(wallet == null || wallet.type == WALLET_ETH ? View.VISIBLE : wallet.type == WALLET_EOS ? View.INVISIBLE : View.VISIBLE);
        if (wallet != null) {
            ivProfile.setImageDrawable(getResources().getDrawable(UserInfoManager.getInst().getProfile(wallet.profileIndex)));
        }
        updateUI(walletAddress, true);
        tvName.setText(name);
        tvAddress.setText(walletAddress);
    }
}
