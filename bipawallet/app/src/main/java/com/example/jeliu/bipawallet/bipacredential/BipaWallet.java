package com.example.jeliu.bipawallet.bipacredential;

import org.spongycastle.crypto.generators.SCrypt;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Hash;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 周智慧 on 2018/8/29.
 */
public class BipaWallet {
    public static byte[] generateDerivedScryptKey(byte[] password, byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }

    public static byte[] performCipherOperation(int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (NoSuchPaddingException e) {
            return throwCipherException(e);
        } catch (NoSuchAlgorithmException e) {
            return throwCipherException(e);
        } catch (InvalidAlgorithmParameterException e) {
            return throwCipherException(e);
        } catch (InvalidKeyException e) {
            return throwCipherException(e);
        } catch (BadPaddingException e) {
            return throwCipherException(e);
        } catch (IllegalBlockSizeException e) {
            return throwCipherException(e);
        }
    }

    private static byte[] throwCipherException(Exception e) throws CipherException {
        throw new CipherException("Error performing cipher operation", e);
    }

    public static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];
        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);
        return Hash.sha3(result);
    }

//    public static ECKeyPair decrypt(String password, WalletFile walletFile) throws CipherException {
//        WalletFile.Crypto crypto = walletFile.getCrypto();
//
//        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
//        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
//        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());
//
//        byte[] derivedKey;
//
//        WalletFile.KdfParams kdfParams = crypto.getKdfparams();
//        if (kdfParams instanceof WalletFile.ScryptKdfParams) {
//            WalletFile.ScryptKdfParams scryptKdfParams =
//                    (WalletFile.ScryptKdfParams) crypto.getKdfparams();
//            int dklen = scryptKdfParams.getDklen();
//            int n = scryptKdfParams.getN();
//            int p = scryptKdfParams.getP();
//            int r = scryptKdfParams.getR();
//            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
//            derivedKey = generateDerivedScryptKey(
//                    password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
//        } else {
//            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
//        }
//
//        byte[] derivedMac = generateMac(derivedKey, cipherText);
//
//        if (!Arrays.equals(derivedMac, mac)) {
//            throw new CipherException("Invalid password provided");
//        }
//
//        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
//        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
//        return ECKeyPair.create(privateKey);
//    }
}
