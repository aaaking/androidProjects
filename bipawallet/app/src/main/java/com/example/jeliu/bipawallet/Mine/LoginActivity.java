package com.example.jeliu.bipawallet.Mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.Common;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 05/05/2018.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.spinner)
    Spinner spinner;

    private Timer timer;
    private int timerCount;

    @BindView(R.id.textView_send)
    TextView tvSend;

    @BindView(R.id.button_send)
    Button btnSend;

    @OnClick({R.id.imageView_quest, R.id.textView_forget}) void onQuest() {
        gotoWebView(Common.getCenterUrl() + "#question16");
    }

    @OnClick(R.id.button_send) void onSend() {
        startTimer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setTitle(getString(R.string.login));
        showBackButton();

        setupView();
    }

    private void setupView() {
        String country[] = null;
        if (UserInfoManager.getInst().getLanguage() == 1) {
            country = getResources().getStringArray(R.array.english_country);
        } else {
            country = getResources().getStringArray(R.array.chinese_country);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, country);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);
        //tvSend.setText("20"+getResources().getString(R.string.send_code));
        tvSend.setText(String.format(getResources().getString(R.string.send_code), 20));
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    -- timerCount;
                    tvSend.setText(String.format(getResources().getString(R.string.send_code), timerCount));
                    if (timerCount == 0) {
                        btnSend.setEnabled(true);

                        tvSend.setText(String.format(getResources().getString(R.string.send_code), 20));
                        timer.cancel();
                    }
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

    private void startTimer() {
        timerCount = 20;
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        CounterTimerTask task = new CounterTimerTask();
        timer.schedule(task,100, 1000);
        btnSend.setEnabled(false);
    }

    private class CounterTimerTask extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }
}
