package com.example.jeliu.bipawallet.Common;

import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Fragment.AssetFragment;
import com.example.jeliu.bipawallet.Fragment.ContactsFragment;
import com.example.jeliu.bipawallet.Fragment.MineFragment;
import com.example.jeliu.bipawallet.Fragment.RecordFragment;
import com.example.jeliu.bipawallet.R;

/**
 * Created by liuming on 05/05/2018.
 */

public class FragmentFactory {
    private static BaseFragment mAsset;
    private static BaseFragment mRecord;
    private static BaseFragment mContact;
    private static BaseFragment mMe;

    public static BaseFragment getInstanceByIndex(int index) {
        BaseFragment fragment = null;
        switch (index) {
            case 0:
                if (mAsset != null) {
                    return mAsset;
                }
                fragment = new AssetFragment();
                mAsset = fragment;
                break;
            case 1:
                if (mRecord != null) {
                    return mRecord;
                }
                fragment = new RecordFragment();
                mRecord = fragment;
                break;
            case 2:
                if (mContact != null) {
                    return mContact;
                }
                fragment = new ContactsFragment();
                mContact = fragment;
                break;
            case 3:
                if (mMe != null) {
                    return mMe;
                }
                fragment = new MineFragment();
                mMe = fragment;
                break;
        }
        return fragment;
    }
}
