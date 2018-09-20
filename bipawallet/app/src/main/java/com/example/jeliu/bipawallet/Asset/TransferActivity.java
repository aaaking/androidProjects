package com.example.jeliu.bipawallet.Asset;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Base.RecordAdapter;
import com.example.jeliu.bipawallet.Common.ChartDataNode;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Common.PriceManager;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by liuming on 09/05/2018.
 */

public class TransferActivity extends BaseActivity {
    @BindView(R.id.textView_amount)
    TextView tvAmount;

    @BindView(R.id.textView_price)
    TextView tvPrice;

    @BindView(R.id.textView_about)
    TextView tvAbout;

    @BindView(R.id.textView_increase)
    TextView tvIncrease;

    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.imageView_up)
    ImageView imageviewUp;

    private String token;
    private RecordAdapter adapter;
    private JSONArray transactions;
    private JSONArray chartsData;
    private ArrayList<ChartDataNode> realChartData = new ArrayList<>();

    private LineChartView chart;
    private LineChartData data;

    private int numberOfPoints = 5;


    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = true;


    @OnClick({R.id.rl_transfer, R.id.rl_redraw})
    void onClick(View view) {
        if (view.getId() == R.id.rl_transfer) {
            Intent intent = new Intent(TransferActivity.this, TransportActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (view.getId() == R.id.rl_redraw) {
            Intent intent = new Intent(TransferActivity.this, WithdrawActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);

        setupView();
        String address = UserInfoManager.getInst().getCurrentWalletAddress();
        if (WalletUtils.isValidAddress(address)) {
            showWaiting();
            setupData(address);
            loadChartsData(address);
        }

        chart = (LineChartView)findViewById(R.id.chart);
        chart.setInteractive(true);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.setValueSelectionEnabled(hasLabelForSelected);

    }

    private void initChart() {

    }

    private void setupView() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        double value = intent.getDoubleExtra("value", 0);
      //  tvMoney.setText(value+"");
       // tvAbout.setText(PriceManager.getInst().tokenPrice(token, value)+"");
        setTitle(token);
        showBackButton();

        adapter = new RecordAdapter(this);
        listView.setAdapter(adapter);
    }

    private void setupData(String address) {
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.TRANSCTION_URL + "?address="+address, null, this);
    }

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
        } else if (url.contains(Constant.BALANCE_CHART)) {
            try {
                chartsData = jsonObject.getJSONArray("chart");
                generateData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void loadChartsData(String address) {
        HZHttpRequest request = new HZHttpRequest();
        request.requestGet(Constant.BALANCE_CHART + "?address="+address, null, this);
    }

    private void refresh() {
        if (transactions == null || transactions.length() == 0) {
            return;
        }
        ArrayList<JSONObject> data = new ArrayList<>();
        for (int i = 0; i < transactions.length(); ++ i) {
            try {
                JSONObject jsonObject = transactions.getJSONObject(i);
                if (jsonObject != null) {
                    String tmp = jsonObject.getString("token");
                    if (tmp.equalsIgnoreCase(token)) {
                        data.add(jsonObject);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.setContent(data);
    }

    private String[] initDays() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);

        String[] months = new String[5];

        calendar.add(Calendar.DAY_OF_MONTH, -5);
        for (int i = 0; i < 5; ++ i) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            int mm = calendar.get(Calendar.MONTH)+1;
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            String date = mm + "." + dd;
            months[i] = date;
        }
        return months;
    }


    private int getSign() {
        int[] sign = new int[]{-1, 1};
        return sign[Math.round((float) Math.random())];
    }

    private void generateData() {
        if (chartsData == null) {
            List<Line> lines = new ArrayList<Line>();
            data = new LineChartData(lines);
            chart.setLineChartData(data);
            return;
        }
        realChartData.clear();
        int length = chartsData.length();
        for (int i = 0; i < length; ++ i) {
            try {
                JSONObject jsonObject = chartsData.getJSONObject(i);
                long time = jsonObject.getLong("time");
                if (time < 1000) {//ignore wrong test data
                    continue;
                }
                //Date date = new Date(time);
                int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
                String s = DateUtils.formatDateTime(this, time, flags);

               // String s = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
                ChartDataNode node = new ChartDataNode();
                JSONArray balance = jsonObject.getJSONArray("balance");
                if (balance != null) {

                    int blength = balance.length();
                    for (int j = 0; j < blength; ++ j) {
                        JSONObject jbnode = balance.getJSONObject(j);
                        String t = jbnode.getString("token");
                        if (t.equalsIgnoreCase(this.token)) {
                            node.token = t;
                            node.price = jbnode.getDouble("price");
                            node.value = jbnode.getDouble("value");
                            node.time = time;
                            node.formattedTime = s;
                            realChartData.add(node);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(realChartData, new Comparator<ChartDataNode>() {
            @Override
            public int compare(ChartDataNode a, ChartDataNode b) {
                if (a.time < b.time) {
                    return -1;
                }
                if (a.time > b.time) {
                    return 1;
                }
                return 0;
            }
        });

        if (realChartData.size() > 0) {
            List<Line> lines = new ArrayList<Line>();

            List<PointValue> values = new ArrayList<PointValue>();
            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            for (int j = Math.max(0, realChartData.size() - numberOfPoints); j < realChartData.size(); ++j) {
                ChartDataNode node = realChartData.get(j);
                values.add(new PointValue(j, (float) node.price));
                axisValues.add(new AxisValue(j).setLabel(node.formattedTime));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[0]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            //line.setHasGradientToTransparent(hasGradientToTransparent);
//            if (pointsHaveDifferentColor){
//                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//            }
            lines.add(line);

            data = new LineChartData(lines);

            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            axisX.setValues(axisValues);
            axisX.setName(getString(R.string.s_date));
            axisY.setName(getString(R.string.title_asset));

            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);

            Axis axisYRight = new Axis().setHasLines(true);
            axisYRight.setName(" ");
            data.setAxisYRight(axisYRight);


            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);
        } else {
            List<Line> lines = new ArrayList<Line>();
            data = new LineChartData(lines);
            chart.setLineChartData(data);
        }
        updateUI(0);
    }

    private void updateUI(int index) {
        if (index < realChartData.size()) {
            ChartDataNode node = realChartData.get(index);
            tvAmount.setText(String.format("%.06f", node.value));
            tvPrice.setText(PriceManager.getInst().formatPrice(1, node.price));
            tvAbout.setText(PriceManager.getInst().formatPrice(node.value, node.price));
            if (index > 0) {
                ChartDataNode pre = realChartData.get(index - 1);
                double rate = (node.price - pre.price) * 100 /pre.price;

                imageviewUp.setVisibility(View.VISIBLE);
                if (rate > 0) {
                    tvIncrease.setTextColor(Color.GREEN);
                    tvIncrease.setText(String.format("%.02f%%", rate));
                    imageviewUp.setImageDrawable(getResources().getDrawable(R.drawable.up));
                } else {
                    if (rate < 0) {
                        tvIncrease.setTextColor(Color.RED);
                        tvIncrease.setText(String.format("%.02f%%", -rate));
                        imageviewUp.setImageDrawable(getResources().getDrawable(R.drawable.down));
                    } else {
                        imageviewUp.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            //Toast.makeText(TransferActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
            updateUI(pointIndex);
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
}
