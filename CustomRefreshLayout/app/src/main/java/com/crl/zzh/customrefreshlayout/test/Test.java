package com.crl.zzh.customrefreshlayout.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.crl.zzh.customrefreshlayout.R;

/**
 * Created by 周智慧 on 27/11/2017.
 */

public class Test extends Activity {
    public static void start(Context context) {
        context.startActivity(new Intent(context, Test.class));
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.w("sunzn", "TouchEventActivity | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.w("sunzn", "TouchEventActivity | onTouchEvent --> " + TouchEventUtil.getTouchAction(event.getAction()));
        return super.onTouchEvent(event);
    }
}
