package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yogitechnolabs.loginmanager.model.ReelItem
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAction
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAdapter

object MultiReelComponent {

    private var reelAdapter: ReelAdapter? = null
    private var viewPager: ViewPager2? = null

    fun show(
        activity: Activity,
        reels: List<ReelItem>,
        onAction: (action: ReelAction, reel: ReelItem) -> Unit
    ) {
        // Agar already dikha rahe hain to remove karo pehle
        dismiss()

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        viewPager = ViewPager2(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            orientation = ViewPager2.ORIENTATION_VERTICAL
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setBackgroundColor(Color.BLACK)
        }

        reelAdapter = ReelAdapter(reels, onAction)
        viewPager!!.adapter = reelAdapter

        // Page change callback
        viewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pauseAllPlayers()
                playPlayerAt(position)
            }
        })

        // Add viewPager on top of root layout
        rootView.addView(viewPager)

        // Auto play first reel
        viewPager!!.post {
            playPlayerAt(0)
        }
    }

    private fun pauseAllPlayers() {
        val recycler = viewPager?.getChildAt(0) as? RecyclerView ?: return
        for (i in 0 until recycler.childCount) {
            val holder = recycler.findViewHolderForAdapterPosition(i)
            if (holder is ReelAdapter.ReelViewHolder) {
                holder.player?.pause()
            }
        }
    }

    private fun playPlayerAt(position: Int) {
        val recycler = viewPager?.getChildAt(0) as? RecyclerView ?: return
        val holder = recycler.findViewHolderForAdapterPosition(position) as? ReelAdapter.ReelViewHolder
        holder?.player?.play()
    }

    fun dismiss() {
        pauseAllPlayers()
        viewPager?.let { vp ->
            val rootView = vp.parent as? ViewGroup
            rootView?.removeView(vp)
        }
        reelAdapter = null
        viewPager = null
    }
}

