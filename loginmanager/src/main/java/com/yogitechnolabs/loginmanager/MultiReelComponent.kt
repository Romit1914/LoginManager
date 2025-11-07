package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
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
        val builder = AlertDialog.Builder(activity)
        val recyclerView = RecyclerView(activity)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        reelAdapter = ReelAdapter(reels, onAction)
        recyclerView.adapter = reelAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    reelAdapter?.playVisibleVideo(layoutManager)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    reelAdapter?.stopAllPlayers()
                }
            }
        })

        builder.setView(recyclerView)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()
        currentDialog?.window?.setLayout(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

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
