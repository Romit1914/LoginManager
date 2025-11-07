package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.yogitechnolabs.loginmanager.R

import com.yogitechnolabs.loginmanager.model.ReelItem

class ReelAdapter(
    private val items: List<ReelItem>,
    private val onAction: (action: ReelAction, reel: ReelItem) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val videoContainer = view.findViewById<FrameLayout>(R.id.videoContainer)
        val descView = view.findViewById<TextView>(R.id.reelDescription)
        val likeBtn = view.findViewById<ImageButton>(R.id.btnLike)
        val commentBtn = view.findViewById<ImageButton>(R.id.btnComment)
        val shareBtn = view.findViewById<ImageButton>(R.id.btnShare)
        var player: ExoPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reel_item_layout, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val reel = items[position]
        holder.descView.text = reel.description

        // Like toggle
        holder.likeBtn.setImageResource(if (reel.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline)
        holder.likeBtn.setOnClickListener {
            reel.isLiked = !reel.isLiked
            holder.likeBtn.setImageResource(if (reel.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline)
            onAction(ReelAction.LIKE, reel)
        }

        holder.commentBtn.setOnClickListener { onAction(ReelAction.COMMENT, reel) }
        holder.shareBtn.setOnClickListener { onAction(ReelAction.SHARE, reel) }

        // Video setup
        holder.player?.release()
        holder.player = ExoPlayer.Builder(holder.view.context).build()
        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        holder.player?.setMediaItem(mediaItem)
        holder.player?.prepare()
        holder.player?.playWhenReady = true

        val playerView = PlayerView(holder.view.context)
        playerView.player = holder.player
        holder.videoContainer.removeAllViews()
        holder.videoContainer.addView(playerView)
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
        holder.player = null
    }
}

enum class ReelAction { LIKE, COMMENT, SHARE }
