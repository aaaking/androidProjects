//import com.example.jeliu.bipawallet.Model.HZToken;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
        Date date = new Date();

        String s = DateFormat.getDateInstance(DateFormat.LONG).format(date);
        System.out.println("ssssssss: " + s);

        String FULL = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        System.out.println("FULL: " + FULL);

        String MEDIUM = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        System.out.println("MEDIUM: " + MEDIUM);

        String SHORT = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        System.out.println("SHORT: " + SHORT);

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
