package com.example.jeliu.bipawallet.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by liuming on 19/05/2018.
 */

public class StepPagerAdapter extends FragmentPagerAdapter {
    private List<StepFragment> stepList;

    public StepPagerAdapter(FragmentManager fm, List<StepFragment> stepList) {
        super(fm);
        this.stepList = stepList;
    }

    @Override
    public Fragment getItem(int position) {
        return stepList.get(position);
    }

    @Override
    public int getCount() {
        return stepList.size();
    }
}