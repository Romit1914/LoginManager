package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
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
        val recyclerView = RecyclerView(activity)

        // ✅ Full-page vertical scrolling
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        // ✅ SnapHelper: show one reel at a time
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.setHasFixedSize(false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(1)

        // ✅ Adapter setup
        reelAdapter = ReelAdapter(reels, onAction)
        recyclerView.adapter = reelAdapter

        // ✅ Scroll listener: play only visible reel
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> reelAdapter?.playVisibleVideo(layoutManager)
                    RecyclerView.SCROLL_STATE_DRAGGING -> reelAdapter?.stopAllPlayers()
                }
            }
        })

        builder.setView(recyclerView)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()

        // ✅ Ensure dialog covers full screen perfectly
        currentDialog?.window?.decorView?.setPadding(0, 0, 0, 0)
        currentDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        currentDialog?.window?.decorView?.post {
            recyclerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            recyclerView.requestLayout()
            reelAdapter?.notifyDataSetChanged()
            reelAdapter?.playVisibleVideo(layoutManager)
        }

        // ✅ Auto play first visible video
        recyclerView.post {
            reelAdapter?.playVisibleVideo(layoutManager)
        }
    }

    fun dismiss() {
        try {
            reelAdapter?.releaseAllPlayers()
        } catch (_: Exception) {
        }
        currentDialog?.dismiss()
        reelAdapter = null
        currentDialog = null
    }
}
