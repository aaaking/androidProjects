//import com.example.jeliu.bipawallet.Model.HZToken;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Test {
    public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static void main(String args[]) {
        List<HZWallet> walletList = new ArrayList<>();
        HZWallet a = new HZWallet("a", "a");
        HZWallet b = new HZWallet("a", "b");
        walletList.add(a);
        System.out.println(walletList.contains(b));
        System.out.println(a == b);
        System.out.println(a.equals(b));


        walletList.remove(b);
        walletList.add(b);
        System.out.println(walletList.get(0).name);

        HashSet fs = new HashSet();
        fs.add(a);
        fs.add(b);
        System.out.println(fs.toString());
        //
        System.out.println(String.valueOf(2.222));
        System.out.println(String.valueOf(2.00));

        //
        String reg = "[1-5a-z]{12}";
        System.out.println(Pattern.matches(reg, ""));
        System.out.println(Pattern.matches(reg, "123"));
        System.out.println(Pattern.matches(reg, "   123456789012  "));
        System.out.println(Pattern.matches(reg, "aaaki ng3 551 1"));
        System.out.println(Pattern.matches(reg, "aaa ing3 511"));
        System.out.println(Pattern.matches(reg, "aaaking3551111111"));
        System.out.println(Pattern.matches(reg, "aaaking35511"));
        System.out.println(Pattern.matches(reg, "aaaking35512"));
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

class HZWallet {
    public String name;
    public String fileName;
    public String address;
    public int profileIndex;
    public String privateKey;
    public String keyStore;

    public HZWallet(String address, String name) {
        this.address = address;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof HZWallet && address != null && address.equals(((HZWallet) o).address);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : super.hashCode();
    }
}
