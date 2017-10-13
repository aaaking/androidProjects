package com.crl.zzh.customrefreshlayout.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.crl.zzh.customrefreshlayout.BaseActivity
import com.crl.zzh.customrefreshlayout.R
import com.crl.zzh.customrefreshlayout.Util.ScreenUtil
import com.crl.zzh.swipebackactivity.SwipeBackActivity
import com.crl.zzh.swipebackactivity.SwipeBackLayout
import kotlinx.android.synthetic.main.ac_test_coordinator.*
import kotlinx.android.synthetic.main.toolbar.*

class CoordinatorTest : SwipeBackActivity() {
    companion object {
        fun start(activity: Activity) {
            var intent = Intent(activity, CoordinatorTest::class.java)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_test_coordinator)
        toolbar.setBackgroundColor(resources.getColor(R.color.blue_auxiliary_text_color))
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            Snackbar.make(it, "Replace with your action", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        }
        list.setLayoutManager(LinearLayoutManager(this))
        list.adapter = CardAdapter()
        testInteractScroll()
        setDragEdge(SwipeBackLayout.DragEdge.TOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var localLayoutParams: WindowManager.LayoutParams = getWindow().getAttributes()
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags);
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
    override fun onDestroy() {
        app_bar_layout.removeOnOffsetChangedListener(offsetChangedListener)
        super.onDestroy()
    }

    lateinit var offsetChangedListener: AppBarLayout.OnOffsetChangedListener
    var mOriginalHeight = 0
    var mFinalHeight = 0
    var mTotalMove = 0f
    fun testInteractScroll() {
        //scroll_contailer
        //rl_club_info  tv_club_info_name    tv_club_info_card
        //ll_club_info_creator  tv_club_info_creator tv_club_info_creator_title
        mOriginalHeight = ScreenUtil.dp2px(this, 233f)
        mFinalHeight = ScreenUtil.dp2px(this, 48f)
        mTotalMove = (mOriginalHeight - mFinalHeight).toFloat()
        offsetChangedListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            var percentage = Math.abs(verticalOffset / mTotalMove)//已经移动的百分比例
            if (percentage >= 1) {
                percentage = 1f
//                app_bar_layout.setBackgroundColor(resources.getColor(R.color.transparent))
//                scroll_contailer.setBackgroundColor(resources.getColor(R.color.transparent))
//                rl_club_info.setBackgroundColor(resources.getColor(R.color.transparent))
//                ll_club_info_creator.setBackgroundColor(resources.getColor(R.color.transparent))
            }
            Log.i("percentage", "percentage: $percentage")
            val paddingTop = (0 + (mTotalMove - 0) * percentage).toInt()
            scroll_contailer.setPadding(scroll_contailer.paddingLeft, paddingTop, scroll_contailer.paddingRight, 0)
            setEnableSwipe(percentage <= 0)
        }
        app_bar_layout.addOnOffsetChangedListener(offsetChangedListener)
    }
}

internal class CardAdapter : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.row_empty_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card_view: CardView
        var tv_position: TextView

        init {
            card_view = itemView.findViewById<View>(R.id.card_view) as CardView
            card_view.isClickable = true
            tv_position = itemView.findViewById<View>(R.id.tv_position) as TextView
        }

        fun bind(position: Int) {
            tv_position.text = "$position"
            card_view.setOnClickListener { v -> Toast.makeText(v.context, "点击: " + position, Toast.LENGTH_SHORT).show() }
        }
    }

    companion object {
        val ITEM_COUNT = 64
    }
}