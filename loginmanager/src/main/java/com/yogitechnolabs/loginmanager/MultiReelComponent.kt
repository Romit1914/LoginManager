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

    fun show(
        activity: Activity,
        reels: List<ReelItem>,
        onAction: (action: ReelAction, reel: ReelItem) -> Unit
    ) {
        val builder =
            AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val viewPager = ViewPager2(activity)

        viewPager.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        reelAdapter = ReelAdapter(reels, onAction)
        viewPager.adapter = reelAdapter

        // Register page change callback to play/pause videos
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var previousPosition = -1
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Pause previous
                if (previousPosition != -1) {
                    val prevHolder = getViewHolderAtPosition(viewPager, previousPosition)
                    prevHolder?.player?.pause()
                }
                // Play current
                val currentHolder = getViewHolderAtPosition(viewPager, position)
                currentHolder?.player?.play()
                previousPosition = position
            }
        })

        builder.setView(viewPager)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()

        currentDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        currentDialog?.window?.decorView?.setPadding(0, 0, 0, 0)
        currentDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Start playing first video when layout is ready
        currentDialog?.window?.decorView?.post {
            reelAdapter?.notifyDataSetChanged()
            val firstHolder = getViewHolderAtPosition(viewPager, 0)
            firstHolder?.player?.play()
        }
    }

    fun dismiss() {
        try {
            reelAdapter?.let { adapter ->
                // Release all players by iterating visible holders
                // Since ViewPager2 recycles views, we can just try visible holders
                for (i in 0 until adapter.itemCount) {
                    val holder = currentDialog?.window?.decorView?.findViewById<ViewPager2>(android.R.id.content)
                        ?.let { vp ->
                            (vp.getChildAt(0) as? RecyclerView)?.findViewHolderForAdapterPosition(i)
                        } as? ReelAdapter.ReelViewHolder?
                    holder?.player?.release()
                }
            }
        } catch (_: Exception) {
        }
        currentDialog?.dismiss()
        reelAdapter = null
        currentDialog = null
    }

    private fun getViewHolderAtPosition(viewPager: ViewPager2, position: Int): ReelAdapter.ReelViewHolder? {
        // ViewPager2 holds a RecyclerView internally as its first child
        val recyclerView = viewPager.getChildAt(0) as? RecyclerView ?: return null
        return recyclerView.findViewHolderForAdapterPosition(position) as? ReelAdapter.ReelViewHolder
    }
}
