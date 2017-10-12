package com.crl.zzh.customrefreshlayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.crl.zzh.customrefreshlayout.Util.ScreenUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            Snackbar.make(it, "Replace with your action", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        }
        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        //
        list.setLayoutManager(LinearLayoutManager(this))
        list.adapter = CardAdapter()
        testInteractScroll()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.menu_setting) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_camera -> {}
            R.id.nav_gallery -> {}
            R.id.nav_slideshow -> {}
            R.id.nav_manage -> {}
            R.id.nav_share -> {}
            R.id.nav_send -> {}
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
                app_bar_layout.setBackgroundColor(resources.getColor(R.color.transparent))
                scroll_contailer.setBackgroundColor(resources.getColor(R.color.transparent))
                rl_club_info.setBackgroundColor(resources.getColor(R.color.transparent))
                ll_club_info_creator.setBackgroundColor(resources.getColor(R.color.transparent))
            }
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
