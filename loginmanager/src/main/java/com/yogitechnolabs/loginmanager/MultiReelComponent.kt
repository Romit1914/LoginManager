package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yogitechnolabs.loginmanager.model.ReelItem
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAction
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAdapter

object MultiReelComponent {

    private var currentDialog: AlertDialog? = null
    private var reelAdapter: ReelAdapter? = null
    private var viewPager: ViewPager2? = null
    private var currentPage = 0

    fun show(
        activity: Activity,
        reels: List<ReelItem>,
        onAction: (action: ReelAction, reel: ReelItem) -> Unit
    ) {
        val builder = AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        viewPager = ViewPager2(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // ✅ Vertical scrolling (like TikTok / YouTube Shorts)
            orientation = ViewPager2.ORIENTATION_VERTICAL
        }

        reelAdapter = ReelAdapter(reels, onAction)
        viewPager!!.adapter = reelAdapter

        // ✅ Disable overscroll glow effect
        (viewPager!!.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        // ✅ Detect page change to pause/play
        viewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                reelAdapter?.let { adapter ->
                    // Pause all
                    pauseAllPlayers()
                    // Play current
                    playPlayerAt(position)
                }
            }
        })

        builder.setView(viewPager)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()

        currentDialog?.window?.setBackgroundDrawableResource(android.R.color.black)
        currentDialog?.window?.decorView?.setPadding(0, 0, 0, 0)
        currentDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // ✅ Auto play first reel after showing dialog
        viewPager!!.post {
            playPlayerAt(0)
        }
    }

    private fun pauseAllPlayers() {
        reelAdapter?.let { adapter ->
            val recycler = (viewPager?.getChildAt(0) as? RecyclerView) ?: return
            for (i in 0 until recycler.childCount) {
                val holder = recycler.findViewHolderForAdapterPosition(i)
                if (holder is com.yogitechnolabs.loginmanager.ui.adapter.ReelAdapter.ReelViewHolder) {
                    holder.player?.pause()
                }
            }
        }
    }

    private fun playPlayerAt(position: Int) {
        reelAdapter?.let { adapter ->
            val recycler = (viewPager?.getChildAt(0) as? RecyclerView) ?: return
            val holder =
                recycler.findViewHolderForAdapterPosition(position) as? com.yogitechnolabs.loginmanager.ui.adapter.ReelAdapter.ReelViewHolder
            holder?.player?.play()
        }
    }

    fun dismiss() {
        try {
            pauseAllPlayers()
        } catch (_: Exception) {
        }
        currentDialog?.dismiss()
        reelAdapter = null
        viewPager = null
        currentDialog = null
    }
}
