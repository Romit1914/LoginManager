package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
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
        val builder = AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val recyclerView = RecyclerView(activity)

        // ✅ LayoutManager: Vertical full-page scroll
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        // ✅ SnapHelper for snapping one full video at a time
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // ✅ Adapter setup
        reelAdapter = ReelAdapter(reels, onAction)
        recyclerView.adapter = reelAdapter

        // ✅ Scroll listener for play/pause control
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // play only the visible video
                    reelAdapter?.playVisibleVideo(layoutManager)
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // pause current while dragging
                    reelAdapter?.stopAllPlayers()
                }
            }
        })

        builder.setView(recyclerView)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()
        currentDialog?.window?.decorView?.setPadding(0, 0, 0, 0)
        currentDialog?.window?.setLayout(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

        // ✅ Start first video automatically
        recyclerView.post {
            reelAdapter?.playVisibleVideo(layoutManager)
        }
    }

    fun dismiss() {
        try {
            reelAdapter?.releaseAllPlayers()
        } catch (_: Exception) { }
        currentDialog?.dismiss()
        reelAdapter = null
        currentDialog = null
    }
}
