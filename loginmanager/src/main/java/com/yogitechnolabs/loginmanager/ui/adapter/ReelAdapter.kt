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
        val videoContainer: FrameLayout = view.findViewById(R.id.videoContainer)
        val descView: TextView = view.findViewById(R.id.reelDescription)
        val likeBtn: ImageButton = view.findViewById(R.id.btnLike)
        val commentBtn: ImageButton = view.findViewById(R.id.btnComment)
        val shareBtn: ImageButton = view.findViewById(R.id.btnShare)
        var player: ExoPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reel_item_layout, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val reel = items[position]
        val context = holder.view.context

        holder.descView.text = reel.description

        // Like toggle
        holder.likeBtn.setImageResource(
            if (reel.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline
        )
        holder.likeBtn.setOnClickListener {
            reel.isLiked = !reel.isLiked
            holder.likeBtn.setImageResource(
                if (reel.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline
            )
            onAction(ReelAction.LIKE, reel)
        }

        holder.commentBtn.setOnClickListener { onAction(ReelAction.COMMENT, reel) }
        holder.shareBtn.setOnClickListener { onAction(ReelAction.SHARE, reel) }

        // ExoPlayer setup (release old one if any)
        holder.player?.release()
        holder.player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        // PlayerView setup
        val playerView = PlayerView(context)
        playerView.useController = false
        playerView.player = holder.player
        playerView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

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
