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
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.model.ReelItem

class ReelAdapter(
    private val items: List<ReelItem>,
    private val onAction: (action: ReelAction, reel: ReelItem) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    private var currentPlayingIndex = -1
    private var recyclerView: RecyclerView? = null

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val videoContainer: FrameLayout = view.findViewById(R.id.videoContainer)
        val descView: TextView = view.findViewById(R.id.reelDescription)
        val likeBtn: ImageButton = view.findViewById(R.id.btnLike)
        val commentBtn: ImageButton = view.findViewById(R.id.btnComment)
        val shareBtn: ImageButton = view.findViewById(R.id.btnShare)
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        var player: ExoPlayer? = null
    }

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        recyclerView = rv
    }

    override fun onDetachedFromRecyclerView(rv: RecyclerView) {
        super.onDetachedFromRecyclerView(rv)
        recyclerView = null
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

        // Initialize ExoPlayer
        holder.player?.release()
        holder.player = ExoPlayer.Builder(context).build()
        holder.playerView.player = holder.player

        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        holder.player?.setMediaItem(mediaItem)
        holder.player?.prepare()
        holder.player?.playWhenReady = false
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
        holder.player = null
    }

    /** Called externally when scrolling to play/pause visible video */
    fun playVisibleVideo(layoutManager: LinearLayoutManager) {
        val first = layoutManager.findFirstCompletelyVisibleItemPosition()
        val last = layoutManager.findLastCompletelyVisibleItemPosition()
        if (first != RecyclerView.NO_POSITION) {
            for (i in first..last) {
                if (i in 0 until itemCount) {
                    val holder = layoutManager.findViewByPosition(i)?.tag as? ReelViewHolder
                    holder?.player?.playWhenReady = true
                }
            }
        }
    }

    fun stopCurrentVideo() {
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
