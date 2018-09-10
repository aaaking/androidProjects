package com.example.jeliu.bipawallet.bipacredential;

import android.text.TextUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;

/**
 * Created by 周智慧 on 2018/8/29.
 */
public class BipaWalletFile implements Serializable {

    public static void duplicateToEth(WalletFile walletFile, BipaWalletFile bipaWalletFile) {
        walletFile.setAddress(bipaWalletFile.address);
        walletFile.setId(bipaWalletFile.id);
        walletFile.setVersion(bipaWalletFile.version);

        WalletFile.Crypto crypto = new WalletFile.Crypto();
        crypto.setCipher(bipaWalletFile.crypto.cipher);
        crypto.setCiphertext(bipaWalletFile.crypto.ciphertext);

        WalletFile.CipherParams cipherParams = new WalletFile.CipherParams();
        cipherParams.setIv(bipaWalletFile.crypto.cipherparams.iv);
        crypto.setCipherparams(cipherParams);

        crypto.setKdf(bipaWalletFile.crypto.kdf);

        WalletFile.ScryptKdfParams kdfParams = new WalletFile.ScryptKdfParams();
        kdfParams.setDklen(bipaWalletFile.crypto.kdfparams.dklen);
//        kdfParams.setC(bipaWalletFile.crypto.kdfparams.c);
//        kdfParams.setPrf(bipaWalletFile.crypto.kdfparams.prf);
        kdfParams.setN(bipaWalletFile.crypto.kdfparams.n);
        kdfParams.setP(bipaWalletFile.crypto.kdfparams.p);
        kdfParams.setR(bipaWalletFile.crypto.kdfparams.r);
        kdfParams.setSalt(bipaWalletFile.crypto.kdfparams.salt);
        crypto.setKdfparams(kdfParams);

        crypto.setMac(bipaWalletFile.crypto.mac);

        walletFile.setCrypto(crypto);
    }
    public static void duplicateToBipa(BipaWalletFile bipaWalletFile, WalletFile walletFile) {
        bipaWalletFile.address = walletFile.getAddress();
        bipaWalletFile.id = walletFile.getId();
        bipaWalletFile.version = walletFile.getVersion();

        BipaWalletFile.Crypto crypto = new BipaWalletFile.Crypto();
        crypto.cipher = walletFile.getCrypto().getCipher();
        crypto.ciphertext = walletFile.getCrypto().getCiphertext();

        BipaWalletFile.CipherParams cipherParams = new BipaWalletFile.CipherParams();
        cipherParams.iv = walletFile.getCrypto().getCipherparams().getIv();
        crypto.cipherparams = (cipherParams);

        crypto.kdf = walletFile.getCrypto().getKdf();

        BipaWalletFile.ScryptKdfParams kdfParams = new BipaWalletFile.ScryptKdfParams();
        kdfParams.dklen = ((WalletFile.ScryptKdfParams) walletFile.getCrypto().getKdfparams()).getDklen();
        kdfParams.n = ((WalletFile.ScryptKdfParams) walletFile.getCrypto().getKdfparams()).getN();
        kdfParams.p = ((WalletFile.ScryptKdfParams) walletFile.getCrypto().getKdfparams()).getP();
        kdfParams.r = ((WalletFile.ScryptKdfParams) walletFile.getCrypto().getKdfparams()).getR();
        kdfParams.salt = ((WalletFile.ScryptKdfParams) walletFile.getCrypto().getKdfparams()).getSalt();
        crypto.kdfparams = (kdfParams);

        crypto.mac = walletFile.getCrypto().getMac();

        bipaWalletFile.crypto = (crypto);
    }
    public String address;
    public BipaWalletFile.Crypto crypto;
    public String id;
    public String miss_mac;
    public int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BipaWalletFile)) {
            return false;
        }
        BipaWalletFile that = (BipaWalletFile) o;
        if (address != null ? !address.equals(that.address) : that.address != null) {
            return false;
        }
        if (crypto != null ? !crypto.equals(that.crypto) : that.crypto != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return version == that.version;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (crypto != null ? crypto.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + version;
        return result;
    }

    public static class Crypto implements Serializable {
        public String cipher;
        public String ciphertext;
        public String kdf;
        public String mac;
        private BipaWalletFile.CipherParams cipherparams;
        private BipaWalletFile.ScryptKdfParams kdfparams;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BipaWalletFile.Crypto)) {
                return false;
            }
            BipaWalletFile.Crypto that = (BipaWalletFile.Crypto) o;
            if (cipher != null ? !cipher.equals(that.cipher) : that.cipher != null) {
                return false;
            }
            if (ciphertext != null ? !ciphertext.equals(that.ciphertext) : that.ciphertext != null) {
                return false;
            }
            if (cipherparams != null ? !cipherparams.equals(that.cipherparams) : that.cipherparams != null) {
                return false;
            }
            if (kdf != null ? !kdf.equals(that.kdf) : that.kdf != null) {
                return false;
            }
            if (kdfparams != null ? !kdfparams.equals(that.kdfparams) : that.kdfparams != null) {
                return false;
            }
            return mac != null ? mac.equals(that.mac) : that.mac == null;
        }

        @Override
        public int hashCode() {
            int result = cipher != null ? cipher.hashCode() : 0;
            result = 31 * result + (ciphertext != null ? ciphertext.hashCode() : 0);
            result = 31 * result + (cipherparams != null ? cipherparams.hashCode() : 0);
            result = 31 * result + (kdf != null ? kdf.hashCode() : 0);
            result = 31 * result + (kdfparams != null ? kdfparams.hashCode() : 0);
            result = 31 * result + (mac != null ? mac.hashCode() : 0);
            return result;
        }
    }

    public static class CipherParams implements Serializable {
        public String iv;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BipaWalletFile.CipherParams)) {
                return false;
            }
            BipaWalletFile.CipherParams that = (BipaWalletFile.CipherParams) o;
            return iv != null ? iv.equals(that.iv) : that.iv == null;
        }

        @Override
        public int hashCode() {
            int result = iv != null ? iv.hashCode() : 0;
            return result;
        }

    }

    public static class KdfParams implements Serializable {
        public int dklen;
        public int c;
        public String prf;
        public String salt;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BipaWalletFile.KdfParams)) {
                return false;
            }
            BipaWalletFile.KdfParams that = (BipaWalletFile.KdfParams) o;
            if (dklen != that.dklen) {
                return false;
            }
            if (c != that.c) {
                return false;
            }
            if (prf != null ? !prf.equals(that.prf) : that.prf != null) {
                return false;
            }
            return salt != null ? salt.equals(that.salt) : that.salt == null;
        }

        @Override
        public int hashCode() {
            int result = dklen;
            result = 31 * result + c;
            result = 31 * result + (prf != null ? prf.hashCode() : 0);
            result = 31 * result + (salt != null ? salt.hashCode() : 0);
            return result;
        }
    }

    public static class ScryptKdfParams implements Serializable {
        public int dklen;
        public int n;
        public int p;
        public int r;
        public String salt;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BipaWalletFile.ScryptKdfParams)) {
                return false;
            }
            BipaWalletFile.ScryptKdfParams that = (BipaWalletFile.ScryptKdfParams) o;
            if (dklen != that.dklen) {
                return false;
            }
            if (n != that.n) {
                return false;
            }
            if (p != that.p) {
                return false;
            }
            if (r != that.r) {
                return false;
            }
            return salt != null ? salt.equals(that.salt) : that.salt == null;
        }

        @Override
        public int hashCode() {
            int result = dklen;
            result = 31 * result + n;
            result = 31 * result + p;
            result = 31 * result + r;
            result = 31 * result + (salt != null ? salt.hashCode() : 0);
            return result;
        }
    }

    public static WalletFile generateMissingWallet(ECKeyPair keyPair, String pwd, byte[] salt, byte[] iv) {
        try {
            byte[] derivedKey = BipaWallet.generateDerivedScryptKey(pwd.getBytes(Charset.forName("UTF-8")), salt, BipaCredential.N_LIGHT, BipaCredential.R, BipaCredential.P_LIGHT, BipaCredential.DKLEN);
            byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
            byte[] privateKeyBytes = Numeric.toBytesPadded(keyPair.getPrivateKey(), BipaCredential.KEY_SIZE);
            byte[] cipherText = BipaWallet.performCipherOperation(Cipher.ENCRYPT_MODE, iv, encryptKey, privateKeyBytes);
            byte[] mac = BipaWallet.generateMac(derivedKey, cipherText);
            return BipaWallet.createWalletFile(keyPair, cipherText, iv, salt, mac, BipaCredential.N_LIGHT, BipaCredential.P_LIGHT);
        } catch (CipherException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WalletFile findMissingWallet(String safePK, String pwd, String saltIVSeed) {
        WalletFile walletFile = new WalletFile();
        walletFile.setAddress("missing");

        if (TextUtils.isEmpty(safePK) || safePK.length() <= 64 || saltIVSeed == null || saltIVSeed.trim().length() < 32) {
            return walletFile;
        }

//        String seed = BipaCredential.getSaltIV(pwd);
        byte[] iv = saltIVSeed.substring(0, 16).getBytes();
        byte[] salt = saltIVSeed.substring(0, 32).getBytes();

        WalletFile.Crypto crypto = new WalletFile.Crypto();
        crypto.setCipher(BipaWallet.CIPHER);
        crypto.setCiphertext(safePK.substring(0, 64));
        walletFile.setCrypto(crypto);

        WalletFile.CipherParams cipherParams = new WalletFile.CipherParams();
        cipherParams.setIv(Numeric.toHexStringNoPrefix(iv));
        crypto.setCipherparams(cipherParams);

        crypto.setKdf(BipaWallet.SCRYPT);
        WalletFile.ScryptKdfParams kdfParams = new WalletFile.ScryptKdfParams();
        kdfParams.setDklen(BipaCredential.DKLEN);
        kdfParams.setN(BipaCredential.N_LIGHT);
        kdfParams.setP(BipaCredential.P_LIGHT);
        kdfParams.setR(BipaCredential.R);
        kdfParams.setSalt(Numeric.toHexStringNoPrefix(salt));
        crypto.setKdfparams(kdfParams);

        crypto.setMac(safePK.substring(64));
        walletFile.setCrypto(crypto);
        walletFile.setId(UUID.randomUUID().toString());
        walletFile.setVersion(BipaWallet.CURRENT_VERSION);
        return walletFile;
    }
}
