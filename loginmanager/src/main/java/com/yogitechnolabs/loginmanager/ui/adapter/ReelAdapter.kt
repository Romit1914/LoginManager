package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.model.ReelItem

class ReelAdapter(
    private val items: List<ReelItem>,
    private val onAction: (action: ReelAction, reel: ReelItem) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    private var recyclerView: RecyclerView? = null

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike = view.findViewById<View>(R.id.btnLike)
        val btnComment = view.findViewById<View>(R.id.btnComment)
        val btnShare = view.findViewById<View>(R.id.btnShare)
        var player: ExoPlayer? = null
    }

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    override fun onDetachedFromRecyclerView(rv: RecyclerView) {
        recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reel_item_layout, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val context = holder.view.context
        val reel = items[position]

        // Player setup
        holder.player?.release()
        val player = ExoPlayer.Builder(context).build()
        holder.player = player
        holder.playerView.player = player
        holder.playerView.useController = false

        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = false

        // Actions
        holder.btnLike.setOnClickListener { onAction(ReelAction.LIKE, reel) }
        holder.btnComment.setOnClickListener { onAction(ReelAction.COMMENT, reel) }
        holder.btnShare.setOnClickListener { onAction(ReelAction.SHARE, reel) }
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
    }

    /** Autoplay only visible reel */
    fun playVisibleVideo(layoutManager: LinearLayoutManager) {
        val center = layoutManager.findFirstCompletelyVisibleItemPosition()
        stopAllPlayers()
        if (center != RecyclerView.NO_POSITION) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(center) as? ReelViewHolder
            holder?.player?.playWhenReady = true
        }
    }

    fun pauseInvisibleVideos(layoutManager: LinearLayoutManager) {
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        for (i in 0 until itemCount) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            if (i < first || i > last) holder?.player?.playWhenReady = false
        }
    }

    fun stopAllPlayers() {
        for (i in 0 until itemCount) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            holder?.player?.playWhenReady = false
        }
    }

    fun releaseAllPlayers() {
        for (i in 0 until itemCount) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            holder?.player?.release()
        }
    }
}

enum class ReelAction { LIKE, COMMENT, SHARE }
