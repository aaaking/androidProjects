package com.crl.zzh.swipebackactivity

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams

/**
 * Created by Eric on 15/3/3.
 */
open class SwipeBackActivity : AppCompatActivity(), SwipeBackLayout.SwipeBackListener {

    var swipeBackLayout: SwipeBackLayout? = null
        private set
    private var ivShadow: ImageView? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(container)
        val view = LayoutInflater.from(this).inflate(layoutResID, null)
        swipeBackLayout!!.addView(view)
    }

    private val container: View
        get() {
            val container = RelativeLayout(this)
            swipeBackLayout = SwipeBackLayout(this)
            swipeBackLayout!!.setDragEdge(DEFAULT_DRAG_EDGE)
            swipeBackLayout!!.setOnSwipeBackListener(this)
            ivShadow = ImageView(this)
            ivShadow!!.setBackgroundColor(Color.parseColor("#99FFFFFF"))
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            container.addView(ivShadow, params)
            container.addView(swipeBackLayout)
            return container
        }

    fun setEnableSwipe(enableSwipe: Boolean) {
        swipeBackLayout!!.setEnablePullToBack(enableSwipe)
    }

    fun setDragEdge(dragEdge: SwipeBackLayout.DragEdge) {
        swipeBackLayout!!.setDragEdge(dragEdge)
    }

    fun setDragDirectMode(dragDirectMode: SwipeBackLayout.DragDirectMode) {
        swipeBackLayout!!.setDragDirectMode(dragDirectMode)
    }

    override fun onViewPositionChanged(fractionAnchor: Float, fractionScreen: Float) {
        ivShadow!!.alpha = 1 - fractionScreen
    }

    companion object {

        private val DEFAULT_DRAG_EDGE = SwipeBackLayout.DragEdge.LEFT
    }

}