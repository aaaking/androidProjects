package com.crl.zzh.customrefreshlayout.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Outline
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import com.crl.zzh.customrefreshlayout.BaseActivity
import com.crl.zzh.customrefreshlayout.R
import com.crl.zzh.customrefreshlayout.util.ScreenUtil
import kotlinx.android.synthetic.main.toolbar.*
import android.view.ViewOutlineProvider
import kotlinx.android.synthetic.main.ac_test.*


class TestAC : BaseActivity() {
    companion object {
        fun start(activity: Activity) {
            var intent = Intent(activity, TestAC::class.java)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
//        window.attributes.alpha = 0.6f
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_test)
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(resources.getColor(R.color.red_normal))
        testSwipeback()
        viewD.setClipToOutline(true)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val viewOutlineProvider = object : ViewOutlineProvider() {
//                override fun getOutline(view: View, outline: Outline) {
//                    // 可以指定圆形，矩形，圆角矩形，path
//                    outline.setOval(0, 0, view.width, view.height)
//                }
//            }
//            viewD.setOutlineProvider(viewOutlineProvider)
//        }
    }

    lateinit var decorView: View
    fun testSwipeback() {
        // 获得decorView
        decorView = window.decorView
    }

    var downX = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {// 当按下时
            // 获得按下时的X坐标
            downX = event.x

        } else if (event.action == MotionEvent.ACTION_MOVE) {// 当手指滑动时
            // 获得滑过的距离
            val moveDistanceX = event.x - downX
            if (moveDistanceX > 0) {// 如果是向右滑动
                decorView.x = moveDistanceX // 设置界面的X到滑动到的位置
            }

        } else if (event.action == MotionEvent.ACTION_UP) {// 当抬起手指时
            // 获得滑过的距离
            val moveDistanceX = event.x - downX
            if (moveDistanceX > ScreenUtil.screenWidth / 2) {
                // 如果滑动的距离超过了手机屏幕的一半, 结束当前Activity
                finish()
            } else { // 如果滑动距离没有超过一半
                // 恢复初始状态
                decorView.setX(0f)
            }
        }
        return super.onTouchEvent(event)
    }

    fun onClick(view: View) {
//        Toast.makeText(this, "${view.z}  ${view.translationZ}", Toast.LENGTH_SHORT).show()
//        val zAnim = ObjectAnimator.ofFloat(view, "translationZ", 0f, 265f)
//        zAnim.setInterpolator(OvershootInterpolator())
//        zAnim.setDuration(2000)
//        zAnim.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val viewOutlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    // 可以指定圆形，矩形，圆角矩形，path
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            view.setOutlineProvider(viewOutlineProvider)
        }
    }
}