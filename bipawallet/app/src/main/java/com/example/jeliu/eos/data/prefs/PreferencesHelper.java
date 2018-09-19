/*
 * Copyright (c) 2017-2018 PlayerOne.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.example.jeliu.eos.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.eos.di.ApplicationContext;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by swapnibble on 2017-08-21.
 */
@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "eosc_pref";

    private static final String PREF_WALLET_DIR_NAME= "eos_wallets";

    private static final String PREF_NODEOS_HOST = "eosd.host";
    private static final String PREF_NODEOS_PORT = "eosd.port";
    private static final String PREF_NODEOS_SCHEME = "eosd.scheme";

    private static final String PREF_PREFIX_WALLET_PW = "wallet.pw.";
    private static final String PREF_SAVE_PASS_FOR_TESTING = "wallet.save.pw";
    private static final String PREF_DEFAULT_ACCOUNT_CREATOR= "account.default.creator";


    private static final String PREF_SKIP_SIGNING = "signing.skip";

    private static final String PREF_CORE_SYMBOL_STRING = "core.sym.str";
    private static final String PREF_CORE_SYMBOL_PRECISION = "core.sym.precision";

    private final SharedPreferences mPrefs;
    private final File mWalletDirFile;

    // cache values
    private Boolean mSkipSigning;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context ) {
        mPrefs = context.getSharedPreferences( PREF_FILE_NAME, Context.MODE_PRIVATE);

        mWalletDirFile = new File( context.getFilesDir(), PREF_WALLET_DIR_NAME);
        mWalletDirFile.mkdirs();
    }

    public void clear() {
        mPrefs.edit().clear().apply();
    }



    public File getWalletDirFile() {
        return mWalletDirFile;
    }

    public String getCoreSymbolString(){
        return mPrefs.getString( PREF_CORE_SYMBOL_STRING, Constant.EOS_SYMBOL_STRING);
    }

    public int getCoreSymbolPrecision(){
        return mPrefs.getInt( PREF_CORE_SYMBOL_PRECISION, Constant.DEFAULT_SYMBOL_PRECISION );
    }

    public void putCoreSymbolInfo( String symbolStr, int symbolPrecision ){
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString(PREF_CORE_SYMBOL_STRING, symbolStr);
        editor.putInt(PREF_CORE_SYMBOL_PRECISION, symbolPrecision );

        editor.apply();
    }

    public void putNodeosConnInfo(String scheme, String host, Integer port) {
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString(PREF_NODEOS_SCHEME, scheme);
        editor.putString(PREF_NODEOS_HOST, host );
        editor.putInt(PREF_NODEOS_PORT, port );

        editor.apply();
    }

    public void putDefaultAccountCreator( String account ){
        mPrefs.edit().putString( PREF_DEFAULT_ACCOUNT_CREATOR, account ).commit();
    }

    public String getDefaultAccountCreator( ) {
        return mPrefs.getString( PREF_DEFAULT_ACCOUNT_CREATOR, "" );
    }

    public void putSavePasswordOption( boolean save ) {
        mPrefs.edit().putBoolean( PREF_SAVE_PASS_FOR_TESTING, save ).apply();
    }

    public boolean getSavePasswordOption(){
        return mPrefs.getBoolean( PREF_SAVE_PASS_FOR_TESTING, Constant.DEFAULT_SAVE_PASSWORD );
    }

    private String getSavedPasswdKeyFor( String walletName ) {
        return PREF_PREFIX_WALLET_PW + walletName;
    }

    public void putWalletPassword(String walletName, String password ) {
        String key = getSavedPasswdKeyFor( walletName );

        // critical data! commit immediately
        if (TextUtils.isEmpty( password ))  {
            mPrefs.edit().remove( key ).commit();
        }
        else {
            mPrefs.edit().putString(key, password).commit();
        }
    }

    public String getWalletPassword( String walletName) {
        return mPrefs.getString( getSavedPasswdKeyFor( walletName ), "" );
    }

    public boolean shouldSkipSigning() {
        if ( null != mSkipSigning ) {
            return mSkipSigning;
        }

        mSkipSigning = mPrefs.getBoolean( PREF_SKIP_SIGNING, Constant.DEFAULT_SKIP_SIGNING );
        return mSkipSigning;
    }


    public void putSkipSigning(boolean skipSigning ){
        mPrefs.edit().putBoolean(PREF_SKIP_SIGNING, skipSigning ).apply();
        mSkipSigning = skipSigning;
    }
}
