package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
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
        val btnPlayPause = view.findViewById<ImageView>(R.id.btnPlayPause)
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

        // Full-screen reel
        view.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            parent.measuredHeight.takeIf { it > 0 } ?: ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ReelViewHolder(view)
    }

    @OptIn(UnstableApi::class)
    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val context = holder.view.context
        val reel = items[position]

        holder.player?.release()

        val player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }

        holder.player = player
        holder.playerView.player = player
        holder.playerView.useController = false
        holder.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        holder.playerView.setKeepContentOnPlayerReset(true)

        // --- Actions ---
        holder.btnLike.setOnClickListener { onAction(ReelAction.LIKE, reel) }
        holder.btnComment.setOnClickListener { onAction(ReelAction.COMMENT, reel) }
        holder.btnShare.setOnClickListener { onAction(ReelAction.SHARE, reel) }

        // --- Screen tap toggle (except buttons) ---
        holder.playerView.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                holder.btnPlayPause.visibility = View.VISIBLE
                holder.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                player.play()
                holder.btnPlayPause.visibility = View.GONE
            }
        }

        // --- Update visibility automatically ---
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                holder.btnPlayPause.visibility = if (isPlaying) View.GONE else View.VISIBLE
            }
        })
    }


    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
        holder.player = null
    }

    fun playVisibleVideo(layoutManager: LinearLayoutManager) {
        val center = layoutManager.findFirstCompletelyVisibleItemPosition()
        stopAllPlayers()
        if (center != RecyclerView.NO_POSITION) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(center) as? ReelViewHolder
            holder?.player?.play()
        }
    }

    fun stopAllPlayers() {
        for (i in 0 until itemCount) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            holder?.player?.pause()
        }
    }

    fun releaseAllPlayers() {
        for (i in 0 until itemCount) {
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            holder?.player?.release()
            holder?.player = null
        }
    }
}

enum class ReelAction { LIKE, COMMENT, SHARE }
