import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Test {
    public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static void main(String args[]) {
        HashMap<String, String> fs = new HashMap<>();
        String pwd = "1";
        KeySpec spec = new PBEKeySpec(pwd.toCharArray(), "bipa_salt".getBytes(), 10, 256);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = keyFactory.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();
            System.out.print(enc.encodeToString(hash));
            System.out.print(enc.encodeToString(hash).length());
//            System.out.print(Numeric.toHexStringNoPrefix(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}