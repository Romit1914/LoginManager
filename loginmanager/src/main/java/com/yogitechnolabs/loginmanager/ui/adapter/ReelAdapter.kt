package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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
    private val handler = Handler(Looper.getMainLooper())

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike: View = view.findViewById(R.id.btnLike)
        val btnComment: View = view.findViewById(R.id.btnComment)
        val btnShare: View = view.findViewById(R.id.btnShare)
        val btnPlayPause: ImageView = view.findViewById(R.id.btnPlayPause)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val progressBar: ProgressBar = view.findViewById(R.id.reelProgressBar)
        var player: ExoPlayer? = null
        var progressRunnable: Runnable? = null
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

        // Full-screen height
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

        // Release previous player
        holder.player?.release()
        holder.progressRunnable?.let { handler.removeCallbacks(it) }

        // Setup player
        val player = ExoPlayer.Builder(context).build()
        holder.player = player
        holder.playerView.player = player
        holder.playerView.useController = false
        holder.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        holder.playerView.setKeepContentOnPlayerReset(true)

        // Load media
        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = false
        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE

        // Description text
        holder.tvDescription.text = reel.description ?: ""

        // --- Action Buttons ---
        holder.btnLike.setOnClickListener { onAction(ReelAction.LIKE, reel) }
        holder.btnComment.setOnClickListener { onAction(ReelAction.COMMENT, reel) }
        holder.btnShare.setOnClickListener { onAction(ReelAction.SHARE, reel) }

        // --- Tap to Play/Pause ---
        holder.playerView.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                holder.btnPlayPause.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .withStartAction { holder.btnPlayPause.visibility = View.VISIBLE }
                    .start()
            } else {
                player.play()
                holder.btnPlayPause.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { holder.btnPlayPause.visibility = View.GONE }
                    .start()
            }
        }

        // --- Player State Change ---
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                holder.btnPlayPause.visibility = if (isPlaying) View.GONE else View.VISIBLE
            }
        })

        // --- Progress Bar Update ---
        holder.progressRunnable = object : Runnable {
            override fun run() {
                if (player.isPlaying && player.duration > 0) {
                    val progress = ((player.currentPosition * 100) / player.duration).toInt()
                    holder.progressBar.progress = progress
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(holder.progressRunnable!!)
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
        holder.player = null
        holder.progressRunnable?.let { handler.removeCallbacks(it) }
    }

    // --- Auto Play visible ---
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
