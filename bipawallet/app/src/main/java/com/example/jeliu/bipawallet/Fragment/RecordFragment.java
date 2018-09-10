package com.example.jeliu.bipawallet.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Base.RecordAdapter;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.HZWalletManager;
import com.example.jeliu.bipawallet.Common.ListWithSectionAdapter;
import com.example.jeliu.bipawallet.Fragment.StepFragment;
import com.example.jeliu.bipawallet.Fragment.StepPagerAdapter;
import com.example.jeliu.bipawallet.Model.HZWallet;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.Record.RecordDetailsActivity;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * Created by liuming on 05/05/2018.
 */

public class RecordFragment extends BaseFragment implements PopupMenu.OnMenuItemClickListener {

    enum Status {
        status_none,
        status_outpay,
        status_succeed,
        status_failed,
        status_proceeding
    }

    @BindView(R.id.ll_select)
    LinearLayout llSelect;

    @BindView(R.id.viewPager)
    ViewPager pager;

    @BindView(R.id.indicatorLayout)
    LinearLayout indicatorLayout;

    @BindView(R.id.textView_sel)
    TextView tvSelect;

    @BindView(R.id.listview)
    ListView listview;
    private RecordAdapter recordAdapter;
    private ListWithSectionAdapter listWithSectionAdapter;

    private StepPagerAdapter adapter;
    private int currentItem;
    private JSONArray transactions;
    private Status status = Status.status_none;
    private int filterdYear = 0;
    private int filterdMonth = 0;
    private int filterdDay = 0;
    List<StepFragment> fragmentList;
    List<String> datas = new ArrayList<>();
    private int mLastSelect = -1;
    private boolean justStart = false;

    private HashMap<String, ArrayList<JSONObject>> filteredData = new HashMap<>();

    @OnClick(R.id.ll_select) void doSelect() {
        onSelect(llSelect);
    }

    @OnClick(R.id.imageView_date) void onFiterDate() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        if (filterdYear != 0 && filterdMonth != 0 && filterdDay != 0) {
            yy = filterdYear;
            mm = filterdMonth;
            dd = filterdDay;
        }

        DatePickerDialog datePicker = new DatePickerDialog(RecordFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                filterdYear = year;
                filterdMonth = monthOfYear;
                filterdDay = dayOfMonth;
                dofilter();
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    private int selectedIndicator = R.drawable.point_select, indicator = R.drawable.point;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, null);
        ButterKnife.bind(this, view);

        initAdapter();
        notifyIndicator();
        setupView();

        //String address = UserInfoManager.getInst().getCurrentWalletAddress();
        //loadData(address);
        return view;
    }

    private void setupView() {
        recordAdapter = new RecordAdapter(getActivity());

        listWithSectionAdapter = new ListWithSectionAdapter(getActivity(), true, 0);
        listview.setAdapter(listWithSectionAdapter);
    }

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    public void onSelect(View ancor) {
        PopupMenu pop = new PopupMenu(getActivity(), ancor);
        pop.getMenuInflater().inflate(R.menu.popup, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all:
                status = Status.status_none;
                tvSelect.setText(getString(R.string.choose));
                break;
            case R.id.outpay:
                status = Status.status_outpay;
                tvSelect.setText(getString(R.string.outpay));
                break;
            case R.id.succeed:
                status = Status.status_succeed;
                tvSelect.setText(getString(R.string.succeed));
                break;
            case R.id.fail:
                status = Status.status_failed;
                tvSelect.setText(getString(R.string.fail));
                break;
            case R.id.processing:
                status = Status.status_proceeding;
                tvSelect.setText(getString(R.string.processing));
                break;
        }
        dofilter();
        return false;
    }

    private boolean filterStatus(JSONObject jsonObject) {
        boolean display = false;
        if (status == Status.status_outpay || status == Status.status_none) {
            return true;
        }
        if (status != Status.status_none) {
            try {
                int s = jsonObject.getInt("status");
                if (s == 1 && status == Status.status_succeed) {
                    display = true;
                } else if (s == 0 && status == Status.status_failed) {
                    display = true;
                } else if (s == 2 && status == Status.status_proceeding) {
                    display = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return display;
    }

    private void dofilter() {
        listWithSectionAdapter.clearSection();

        Set<String> keys = filteredData.keySet();
        for (String key : keys) {
            ArrayList<JSONObject> org = filteredData.get(key);
            ArrayList<JSONObject> tmp = new ArrayList<>();
            for (JSONObject jsonObject : org) {
                boolean display = false;
                if (filterdYear != 0) {
                    long time = 0;
                    try {
                        time = jsonObject.getLong("time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    Date tempDate = new Date(time);
                    cal.setTime(tempDate);

                    int dy = cal.get(Calendar.YEAR);
                    int dm = cal.get(Calendar.MONTH);
                    int dd = cal.get(Calendar.DAY_OF_MONTH);
                    if (filterdYear == dy && filterdMonth == dm && filterdDay == dd) {
                        display = filterStatus(jsonObject);
                    }
                } else {
                    display = filterStatus(jsonObject);
                }
                if (display) {
                    tmp.add(jsonObject);
                }
            }
            if (tmp.size() > 0) {
                RecordAdapter listViewAdapter = new RecordAdapter(getActivity());
                listViewAdapter.setContent(tmp);
                listWithSectionAdapter.addSection(key, listViewAdapter);
            }
        }
        listWithSectionAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        currentItem = 0;
        justStart = true;
        pager.invalidate();
        if (datas.size() == 0) {
            HashMap<String, String> wallets = UserInfoManager.getInst().getWallets();
            String address = UserInfoManager.getInst().getCurrentWalletAddress();
            HZWallet currentWallet = HZWalletManager.getInst().getWallet(address);
            StepFragment fragment = new StepFragment();
            fragment.init(address, currentWallet.name);
            fragmentList = new ArrayList<>();
            fragmentList.add(fragment);
            //datas.add(address + "______" + currentWallet.name);

            Iterator iter = wallets.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                if (address.equalsIgnoreCase((String)key)) {
                    continue;
                }
                Object val = entry.getValue();
                fragment = new StepFragment();

                String splits[] = ((String)val).split("______");
                if (splits.length == 2) {
                    fragment.init((String)key, splits[0]);
                    //datas.add(key + "______" + splits[0]);
                }

                fragmentList.add(fragment);
            }
        } else {
            fragmentList = new ArrayList<>();
            for (String val : datas) {
                String splits[] = (val).split("______");
                if (splits.length == 2) {
                    StepFragment fragment = new StepFragment();
                    fragment.init(splits[0], splits[1]);
                    fragmentList.add(fragment);
                }
            }
        }


        adapter = new StepPagerAdapter(getChildFragmentManager(), fragmentList);
        pager.setAdapter(adapter);
        //pager.setCurrentItem(0);
        final int pos = 0;

        pager.postDelayed(new Runnable() {

            @Override
            public void run() {
                pager.setCurrentItem(pos);
            }
        }, 100);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == currentItem) {
                    return;
                }
                if (justStart) {
                    justStart = false;
                    if (position == mLastSelect) {
                        return;
                    }
                }
                currentItem = position;
                resetFilter();
                notifyIndicator();
                mLastSelect = currentItem;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void resetFilter() {
        status = Status.status_none;
        tvSelect.setText(getString(R.string.choose));
        filterdYear = 0;
        filterdMonth = 0;
        filterdDay = 0;
    }

    public void notifyIndicator() {
        if (indicatorLayout.getChildCount() > 0)
            indicatorLayout.removeAllViews();

        HashMap<String, String> wallets = UserInfoManager.getInst().getWallets();
        for (int i = 0; i < wallets.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setPadding(8, 8, 8, 8);
            int drawable = indicator;
            if (i == currentItem)
                drawable = selectedIndicator;

            imageView.setImageResource(drawable);
            indicatorLayout.addView(imageView);
        }

        StepFragment fragment = fragmentList.get(currentItem);
        String address = fragment.getAddress();
        UserInfoManager.getInst().setCurrentWalletAddress(address);
        refreshStepFragment();
        if (address != null) {
            showWaiting();
            loadData(address);
            loadBalance(address);
        }
    }

    private void refreshStepFragment() {
        StepFragment fragment = fragmentList.get(currentItem);
        double total = 0;
        if (transactions == null) {
            return;
        }

        for (int i = transactions.length() - 1; i >= 0 ; -- i) {
            try {
                JSONObject jsonObject = transactions.getJSONObject(i);
                double value = jsonObject.getDouble("value");
                total += value;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        fragment.setOutpay(total);
    }

    private void loadBalance(String address) {
        if (address != null) {
            HZHttpRequest request = new HZHttpRequest();
            request.requestGet(Constant.BALANCE_URL + "?address="+address, null, this);
        }
    }

    private void loadData(String address) {
        if (address != null) {
            HZHttpRequest request = new HZHttpRequest();
            request.requestGet(Constant.TRANSCTION_URL + "?address="+address, null, this);
        }
    }
//    String address = UserInfoManager.getInst().getCurrentWalletAddress();
//        if (address != null) {
//        showWaiting();
//        HZHttpRequest request = new HZHttpRequest();
//        request.requestGet(Constant.BALANCE_URL + "?address="+address, null, this);
//    }
    @Override
    public boolean onSuccess(JSONObject jsonObject, String url) {
        if (!super.onSuccess(jsonObject, url)) {
            return true;
        }
        if (url.contains(Constant.TRANSCTION_URL)) {
            try {
                transactions = jsonObject.getJSONArray("txhashs");
                refresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (url.contains(Constant.BALANCE_URL)) {
            try {
                JSONArray balance = jsonObject.getJSONArray("balance");
                String address = UserInfoManager.getInst().getCurrentWalletAddress();
                HZWalletManager.getInst().updateWalletInfo(address, balance);
                StepFragment fragment = fragmentList.get(currentItem);
                fragment.refreshData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public class JsObjectComparator implements Comparator<String>
    {
        public int compare(String left, String right) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            try {
                Date dl = df.parse(left);
                Date dr = df.parse(right);
                return dr.compareTo(dl);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
//
    private void refresh() {
        if (transactions == null || transactions.length() == 0) {
            filteredData.clear();
            listWithSectionAdapter.clearSection();
            listWithSectionAdapter.notifyDataSetChanged();
            refreshStepFragment();
            return;
        }
        filteredData.clear();
        for (int i = transactions.length() - 1; i >= 0 ; -- i) {
            try {
                JSONObject jsonObject = transactions.getJSONObject(i);
                long time = jsonObject.getLong("time");
                Date date = new Date(time);

                String s = DateFormat.getDateInstance(DateFormat.LONG).format(date);
                ArrayList<JSONObject> array = filteredData.get(s);
                if (array == null) {
                    array = new ArrayList<>();
                    array.add(jsonObject);
                    filteredData.put(s, array);
                } else {
                    array.add(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Set<String> keys = filteredData.keySet();

        List<String> list = new ArrayList<String>(keys);
        Collections.sort(list, new JsObjectComparator());

        for (String key : list) {
            RecordAdapter listViewAdapter = new RecordAdapter(getActivity());
            listViewAdapter.setContent(sortArray(filteredData.get(key)));
            listWithSectionAdapter.addSection(key, listViewAdapter);
        }
        listWithSectionAdapter.notifyDataSetChanged();
        refreshStepFragment();
    }

    private ArrayList<JSONObject> sortArray(ArrayList<JSONObject> array) {
        ArrayList<Long> times = new ArrayList<>();
        int length = array.size();
        for (int i = 0; i < length; ++ i) {
            JSONObject obj = array.get(i);
            try {
                long time = obj.getLong("time");
                times.add(time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(times);
        Collections.reverse(times);
        ArrayList<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < length; ++ i) {
            long time = times.get(i);
            for (int j = 0; j < length; ++ j) {
                JSONObject obj = array.get(j);
                try {
                    long tr = obj.getLong("time");
                    if (tr == time) {
                        result.add(obj);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}