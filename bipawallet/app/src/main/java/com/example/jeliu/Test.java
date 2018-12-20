//import com.example.jeliu.bipawallet.Model.HZToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static int GG = 0;

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
        System.out.println("test set size:" + fs.size() + "\n" + fs.toArray()[0].toString());
        //
        String fff = "fs@";
        String[] aaa = fff.split("@");
        System.out.println(aaa.length + "   " + Arrays.toString(aaa));
        //
        String ll = "[{\"a\":     \"av\", \"a\":\"av\"}]";
        System.out.println(ll.replaceAll("\\[|\\{|\\]|\\}|\\ |\\\"", ""));
        for (String i : ll.replaceAll("\\[|\\{|\\]|\\}|\\ |\\\"", "").split(",")) {
            String[] fsf = i.split(":");
            String type = fsf[0];
            String value = fsf[1];
            System.out.println(Arrays.toString(fsf));
            System.out.println(value);
        }

        HZWallet fa = new HZWallet("a", "aN");
        HZWallet fb = fa;//new HZWallet("b", "bN");
        fb = fa;
        fb.name = "faf";
        fa = null;
        System.out.println(fb.toString() + fa + "");

        ArrayList<String> fffs = new ArrayList<>();
        ((ArrayList<String>) fffs.clone()).add("fs");
        System.out.println(fffs.toString());
    }

    public void sayHello(Human huamn) {
        System.out.println("Human");
    }

    public void sayHello(Man huamn) {
        System.out.println("Man");
    }

    public void sayHello(Woman huamn) {
        System.out.println("Woman");
    }

    public static void testSys(String value) {
        value = value.trim();

        Pattern pattern = Pattern.compile("^([0-9]+)\\.?([0-9]*)([ ][a-zA-Z0-9]{1,7})?$");//\\s(\\w)$");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            String beforeDotVal = matcher.group(1), afterDotVal = matcher.group(2);
            System.out.println("---------------");
            System.out.println(beforeDotVal);
            System.out.println(afterDotVal);
            System.out.println(Double.valueOf(beforeDotVal + "." + afterDotVal));
        } else {
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
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof HZWallet)) {
//            return false;
//        }
//        return address != null && name != null && address.equals(((HZWallet) o).address) && name.equals(((HZWallet) o).name);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "address: " + address + " name: " + name;
    }
}

class Human {
    public void sayHello() {
        System.out.println("Human");
    }
}

class Man extends Human {
    public void sayHello() {
        System.out.println("Man");
    }
}

class Woman extends Human {
    public void sayHello() {
        System.out.println("Woman");
    }
}
