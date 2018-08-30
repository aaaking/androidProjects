package com.example.jeliu.bipawallet.bipacredential;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.jeliu.bipawallet.Application.HZApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * https://blog.csdn.net/DrkCore/article/details/69931654
 * Created by 周智慧 on 2018/8/28.
 */
public class BipaCredential {
    public static final String SP_SAFE_BIPA = "s_bipa";
    private static final int IV_SIZE = 16;
    private static final int KEY_SIZE = 32;
    private static final int N_LIGHT = 1 << 12;
    private static final int P_LIGHT = 6;

    private static final int N_STANDARD = 1 << 18;
    private static final int P_STANDARD = 1;

    private static final int R = 8;
    private static final int DKLEN = 32;

    public static String getSaltIV(String pwd) {
        String result = "93eccc213675ba2045b0452f931f84f49906c25e033d408e91b050dbd61e02b1";//pwd="1"
        KeySpec spec = new PBEKeySpec(pwd.toCharArray(), "bipa_salt".getBytes(), 10, 256);
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = keyFactory.generateSecret(spec).getEncoded();
            Log.i("zzh-getSaltIV", Numeric.toHexStringNoPrefix(hash));
            result = Numeric.toHexStringNoPrefix(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static SafePK encryptToSafePK(String pwd, String pk) {
        Log.i("zzh-pk", pk);
        SafePK safePK = new SafePK();
        ECKeyPair keyPair = ECKeyPair.create(new BigInteger(pk, 16));
        String seed = getSaltIV(pwd);
        byte[] iv = seed.substring(0, 16).getBytes();
        byte[] salt = seed.substring(0, 32).getBytes();
        safePK.s_iv = Numeric.toHexStringNoPrefix(iv);
        safePK.s_salt = Numeric.toHexStringNoPrefix(salt);
        byte[] privateKeyBytes = Numeric.toBytesPadded(keyPair.getPrivateKey(), KEY_SIZE);
        byte[] encryptedData = encryptData(privateKeyBytes, iv, deriveKeySecurely(pwd, KEY_SIZE, salt));
        safePK.s_pk = Numeric.toHexStringNoPrefix(encryptedData);
        return safePK;
    }

    public static void encryptPK(String pwd, Credentials credentials) {
        try {
            SafePK safePK = encryptToSafePK(pwd, credentials.getEcKeyPair().getPrivateKey().toString(16));
            Log.i("zzh-safePK-to-encrypt", safePK.s_pk);
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger(safePK.s_pk, 16));
            WalletFile walletFile = Wallet.createLight(pwd, keyPair);//its address is wrong
            walletFile.setAddress(credentials.getAddress().substring(2).toLowerCase());
            SharedPreferences sp = HZApplication.getInst().getSharedPreferences(SP_SAFE_BIPA, 0);
            SharedPreferences.Editor localEditor = sp.edit();
            BipaWalletFile bipaWalletFile = new BipaWalletFile();
            BipaWalletFile.duplicateToBipa(bipaWalletFile, walletFile);
//            bipaWalletFile.s_iv = safePK.s_iv;
//            bipaWalletFile.s_salt = safePK.s_salt;
            Gson gson = new Gson();
            String jsonStr = gson.toJson(bipaWalletFile);
            localEditor.putString(credentials.getAddress().substring(2).toLowerCase(), jsonStr);
            localEditor.apply();
        } catch (Exception e) {
            Log.i("zzh-encryptPK-err", e.toString());
        }
    }

    public static void decryptPK(String address, String pwd) {
        try {
            SharedPreferences sp = HZApplication.getInst().getSharedPreferences(SP_SAFE_BIPA, 0);
            Gson gson = new Gson();
            String storedHashMapString = sp.getString(address.toLowerCase(), "");
            java.lang.reflect.Type type = new TypeToken<BipaWalletFile>() {
            }.getType();
            BipaWalletFile bipaWalletFile = gson.fromJson(storedHashMapString, type);
            WalletFile walletFile = new WalletFile();
            BipaWalletFile.duplicateToEth(walletFile, bipaWalletFile);
            ECKeyPair keyPair = Wallet.decrypt(pwd, walletFile);
            String safePK = keyPair.getPrivateKey().toString(16);
            Log.i("zzh-safePK-decrypted", safePK);
            ///////
            String seed = getSaltIV(pwd);
            byte[] iv = seed.substring(0, 16).getBytes();
            byte[] salt = seed.substring(0, 32).getBytes();
            byte[] datas = retrieveData(pwd, Numeric.hexStringToByteArray(safePK), iv, salt);
            ECKeyPair pair = ECKeyPair.create(datas);
            Log.i("zzh-PK-decrypted", pair.getPrivateKey().toString(16));
        } catch (Exception e) {
            Log.i("zzh-decryptPK-err", e.toString());
        }
    }

    public static String getPK(BipaWalletFile bipaWalletFile, String safePK, String pwd) {
        String seed = getSaltIV(pwd);
        byte[] iv = seed.substring(0, 16).getBytes();
        byte[] salt = seed.substring(0, 32).getBytes();
        String pk = "";
        byte[] datas = retrieveData(pwd, Numeric.hexStringToByteArray(safePK), iv, salt);
        ECKeyPair pair = ECKeyPair.create(datas);
        Log.i("zzh-PK-decrypted", pair.getPrivateKey().toString(16));
        return pair.getPrivateKey().toString(16);
    }

    public static String getSafePK(BipaWalletFile bipaWalletFile, String pwd) {
        String safePK = "";
        try {
            WalletFile walletFile = new WalletFile();
            BipaWalletFile.duplicateToEth(walletFile, bipaWalletFile);
            ECKeyPair keyPair = Wallet.decrypt(pwd, walletFile);
            safePK = keyPair.getPrivateKey().toString(16);
            Log.i("zzh-getSafePK-decrypted", safePK);
        } catch (Exception e) {
            Log.i("zzh-getSafePK-err", e.toString());
        }
        return safePK;
    }

    private static byte[] encryptData(byte[] data, byte[] iv, SecretKey key) {
        return encryptOrDecrypt(data, key, iv, true);
    }

    private static byte[] encryptOrDecrypt(byte[] data, SecretKey key, byte[] iv, boolean isEncrypt) {
        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("This is unconceivable!", e);
        }
    }

    /**
     * Example use of a key derivation function, derivating a key securely from a password.
     */
    private static SecretKey deriveKeySecurely(String password, int keySizeInBytes, byte[] salt) {
        // Use this to derive the key from the password:
//        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), retrieveSalt(), 100 /* iterationCount */, keySizeInBytes * 8 /* key size in bits */);
        try {
//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
//            return new SecretKeySpec(keyBytes, "AES");
            byte[] derivedKey = BipaWallet.generateDerivedScryptKey(password.getBytes(Charset.forName("UTF-8")), salt, N_LIGHT, R, P_LIGHT, DKLEN);
            byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
            return new SecretKeySpec(encryptKey, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Deal with exceptions properly!", e);
        }
    }

    private static byte[] retrieveSalt() {
        // Salt must be at least the same size as the key.
        byte[] salt = new byte[KEY_SIZE];
        // Create a random salt if encrypting for the first time, and save it for future use.
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        return salt;
    }

    private static byte[] retrieveIv() {
        byte[] iv = new byte[IV_SIZE];
        // Ideally your data should have been encrypted with a random iv. This creates a random iv
        // if not present, in order to encrypt our mock data.
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(iv);
        return iv;
    }

    private static byte[] decryptData(byte[] data, byte[] iv, SecretKey key) {
        return encryptOrDecrypt(data, key, iv, false);
    }

    /**
     * Retrieve encrypted data using a password. If data is stored with an insecure key, re-encrypt
     * with a secure key.
     */
    public static byte[] retrieveData(String pwd, byte[] encryptedData, byte[] iv, byte[] salt) {
        SecretKey secureKey = deriveKeySecurely(pwd, KEY_SIZE, salt);
        byte[] decryptedData = decryptData(encryptedData, iv, secureKey);
        return (decryptedData);
    }
}
