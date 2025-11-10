package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
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
import androidx.core.net.toUri
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.OnTouchListener

class ReelAdapter(
    private val items: List<ReelItem>,
    private val onAction: (action: ReelAction, reel: ReelItem) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private val handler = Handler(Looper.getMainLooper())

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike: View = view.findViewById(R.id.ivLikeOverlay)
        val btnComment: View = view.findViewById(R.id.btnComment)
        val btnShare: View = view.findViewById(R.id.btnShare)
        val btnPlayPause: ImageView = view.findViewById(R.id.btnPlayPause)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val progressBar: SeekBar = view.findViewById(R.id.reelProgressBar)
        val ivVolumeOverlay: ImageView = view.findViewById(R.id.ivVolumeOverlay)
        val ivLikeOverlay: ImageView = view.findViewById(R.id.ivLikeOverlay)

        var player: ExoPlayer? = null
        var progressRunnable: Runnable? = null

        // Gesture detector for tap/double tap
        val gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                player?.let {
                    if (it.volume > 0f) {
                        it.volume = 0f
                        showVolumeIcon(true)
                    } else {
                        it.volume = 1f
                        showVolumeIcon(false)
                    }
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                showLikeIcon()
                player?.let {
                    // Optional: you can also toggle play/pause here or just show like
                }
                onAction(ReelAction.LIKE, items[adapterPosition])
                return true
            }
        })

        init {
            playerView.setOnTouchListener(OnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            })
        }

        private fun showVolumeIcon(muted: Boolean) {
            ivVolumeOverlay.setImageResource(if (muted) R.drawable.ic_volume_off else R.drawable.ic_volume_on)
            ivVolumeOverlay.visibility = View.VISIBLE
            ivVolumeOverlay.alpha = 1f
            ivVolumeOverlay.animate().alpha(0f).setDuration(800).withEndAction {
                ivVolumeOverlay.visibility = View.GONE
            }.start()
        }

        private fun showLikeIcon() {
            ivLikeOverlay.visibility = View.VISIBLE
            ivLikeOverlay.alpha = 1f
            ivLikeOverlay.animate().alpha(0f).setDuration(800).withEndAction {
                ivLikeOverlay.visibility = View.GONE
            }.start()
        }
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

        // Release old player before setting new
        holder.player?.release()
        holder.progressRunnable?.let { handler.removeCallbacks(it) }

        // Setup ExoPlayer
        val player = ExoPlayer.Builder(context).build()
        holder.player = player
        holder.playerView.player = player
        holder.playerView.useController = false
        holder.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        holder.playerView.setKeepContentOnPlayerReset(true)

        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = false
        player.repeatMode = Player.REPEAT_MODE_ONE

        holder.tvDescription.text = reel.description ?: ""

        holder.btnLike.setOnClickListener { onAction(ReelAction.LIKE, reel) }
        holder.btnComment.setOnClickListener { onAction(ReelAction.COMMENT, reel) }
        holder.btnShare.setOnClickListener { onAction(ReelAction.SHARE, reel) }

        holder.playerView.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                holder.btnPlayPause.apply {
                    visibility = View.VISIBLE
                    animate().alpha(1f).setDuration(200).start()
                }
            } else {
                player.play()
                holder.btnPlayPause.apply {
                    animate().alpha(0f).setDuration(200).withEndAction { visibility = View.GONE }
                        .start()
                }
            }
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                holder.btnPlayPause.visibility = if (isPlaying) View.GONE else View.VISIBLE
            }
        })

        holder.progressRunnable = object : Runnable {
            override fun run() {
                if (player.duration > 0) {
                    val progress = ((player.currentPosition * 100) / player.duration).toInt()
                    holder.progressBar.progress = progress
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(holder.progressRunnable!!)

        holder.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && player.duration > 0) {
                    val seekPosition = (progress / 100f) * player.duration
                    player.seekTo(seekPosition.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.release()
        holder.player = null
        holder.progressRunnable?.let { handler.removeCallbacks(it) }
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

