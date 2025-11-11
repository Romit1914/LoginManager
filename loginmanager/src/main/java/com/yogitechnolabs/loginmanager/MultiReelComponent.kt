package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yogitechnolabs.loginmanager.model.ReelItem
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAction
import com.yogitechnolabs.loginmanager.ui.adapter.ReelAdapter

object MultiReelComponent {

    private var reelAdapter: ReelAdapter? = null
    private var viewPager: ViewPager2? = null
    private var isFullscreenEnabled = false

    fun show(
        activity: Activity,
        reels: List<ReelItem>,
        onAction: (action: ReelAction, reel: ReelItem) -> Unit
    ) {
        dismiss()

        enableFullscreen(activity)

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

        viewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pauseAllPlayersExcept(position)
            }
        })

        rootView.addView(viewPager)

        // Start playing first reel after layout ready
        viewPager!!.post {
            pauseAllPlayersExcept(0)
        }
    }

    private fun pauseAllPlayersExcept(positionToPlay: Int) {
        val recycler = viewPager?.getChildAt(0) as? RecyclerView ?: return
        for (i in 0 until recycler.childCount) {
            val holder = recycler.findViewHolderForAdapterPosition(i)
            if (holder is ReelAdapter.ReelViewHolder) {
                if (holder.bindingAdapterPosition == positionToPlay) {
                    holder.player?.play()
                } else {
                    holder.player?.pause()
                }
            }
        }
    }

    private fun enableFullscreen(activity: Activity) {
        if (isFullscreenEnabled) return
        isFullscreenEnabled = true

        val window = activity.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        }
    }

    private fun disableFullscreen(activity: Activity) {
        if (!isFullscreenEnabled) return
        isFullscreenEnabled = false

        val window = activity.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    fun dismiss(activity: Activity? = null) {
        pauseAllPlayersExcept(-1) // pause all players on dismiss
        viewPager?.let { vp ->
            val rootView = vp.parent as? ViewGroup
            rootView?.removeView(vp)
        }
        reelAdapter = null
        viewPager = null

        activity?.let {
            disableFullscreen(it)
        }
    }
}
