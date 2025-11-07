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

    fun show(
        activity: Activity,
        reels: List<ReelItem>,
        onAction: (action: ReelAction, reel: ReelItem) -> Unit
    ) {
        val builder = AlertDialog.Builder(activity)
        val recyclerView = RecyclerView(activity)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ReelAdapter(reels, onAction)
        builder.setView(recyclerView)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()
        currentDialog?.window?.setLayout(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    fun dismiss() {
        currentDialog?.dismiss()
        currentDialog = null
    }
}
