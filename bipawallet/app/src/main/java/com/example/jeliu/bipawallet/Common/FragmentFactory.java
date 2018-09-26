package com.example.jeliu.bipawallet.Common;

import com.example.jeliu.bipawallet.Fragment.AssetFragment;
import com.example.jeliu.bipawallet.Fragment.ContactsFragment;
import com.example.jeliu.bipawallet.Fragment.MineFragment;
import com.example.jeliu.bipawallet.Fragment.RecordFragment;

/**
 * Created by liuming on 05/05/2018.
 */

public class FragmentFactory {

    public static AssetFragment createAssetFrg() {
        AssetFragment fragment = new AssetFragment();
        return fragment;
    }

    public static RecordFragment createRecordFrg() {
        RecordFragment fragment = new RecordFragment();
        return fragment;
    }

    public static ContactsFragment createContactsFragment() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    public static MineFragment createMineFragment() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }
}
