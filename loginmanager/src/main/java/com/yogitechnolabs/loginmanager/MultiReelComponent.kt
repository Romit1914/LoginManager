package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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
        val recyclerView = RecyclerView(activity)
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        reelAdapter = ReelAdapter(reels, onAction)
        recyclerView.adapter = reelAdapter

        // Scroll listener for autoplay/pause
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    reelAdapter?.playVisibleVideo(layoutManager)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                reelAdapter?.pauseInvisibleVideos(layoutManager)
            }
        })

        // Create full-screen dialog
        val builder = AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        builder.setView(recyclerView)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()
        currentDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Auto play first video
        recyclerView.post {
            reelAdapter?.playVisibleVideo(layoutManager)
        }
    }

    fun dismiss() {
        reelAdapter?.releaseAllPlayers()
        currentDialog?.dismiss()
        currentDialog = null
    }
}
