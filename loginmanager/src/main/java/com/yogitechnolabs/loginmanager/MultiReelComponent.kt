package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yogitechnolabs.loginmanager.model.ReelItem
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAdapter

class MultiReelComponent(
    private val activity: Activity,
    private val reels: List<ReelItem>,
    private val onAction: (ReelItem, String) -> Unit
) {

    private lateinit var viewPager: ViewPager2
    private lateinit var reelAdapter: ReelAdapter

    fun getView(): ViewPager2 {
        viewPager = ViewPager2(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT // 👈 ensure full screen
            )

            // 👇 Set vertical scrolling
            orientation = ViewPager2.ORIENTATION_VERTICAL

            // 👇 Disable clipping to make full screen feel smooth
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
        }

        reelAdapter = ReelAdapter(reels, onAction)
        viewPager.adapter = reelAdapter

        // 👇 Ensure only one reel fully visible per page
        (viewPager.getChildAt(0) as RecyclerView).apply {
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }

        // 👇 Auto play/pause logic on page change
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                reelAdapter.pauseAllPlayers()
                reelAdapter.playPlayerAt(position)
            }
        })

        return viewPager
    }
}
