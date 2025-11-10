package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.View.OnTouchListener
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

class ReelAdapter(
    private val items: List<ReelItem>,
    private val onAction: (action: ReelAction, reel: ReelItem) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private val handler = Handler(Looper.getMainLooper())

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)                // fixed id
        val btnComment: ImageView = view.findViewById(R.id.btnComment)
        val btnShare: ImageView = view.findViewById(R.id.btnShare)
        val btnPlayPause: ImageView = view.findViewById(R.id.btnPlayPause)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val progressBar: SeekBar = view.findViewById(R.id.reelProgressBar)
        val ivVolumeOverlay: ImageView = view.findViewById(R.id.ivVolumeOverlay)
        val ivLikeOverlay: ImageView = view.findViewById(R.id.ivLikeOverlay)   // separate overlay image

        var player: ExoPlayer? = null
        var progressRunnable: Runnable? = null

        // Gesture detector for tap/double tap
        private val gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                player?.let {
                    val muted = it.volume <= 0f
                    if (muted) {
                        it.volume = 1f
                        showVolumeIcon(false)
                    } else {
                        it.volume = 0f
                        showVolumeIcon(true)
                    }
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                showLikeIcon()
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onAction(ReelAction.LIKE, items[pos])
                }
                return true
            }
        })

        init {
            // attach touch listener to PlayerView to feed GestureDetector
            playerView.setOnTouchListener(OnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                // allow view to handle other touch actions (e.g., long press) if needed
                true
            })

            // Button click listeners that use bindingAdapterPosition safely
            btnLike.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onAction(ReelAction.LIKE, items[pos])
                showLikeIcon()
            }
            btnComment.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onAction(ReelAction.COMMENT, items[pos])
            }
            btnShare.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onAction(ReelAction.SHARE, items[pos])
            }
        }

        private fun showVolumeIcon(muted: Boolean) {
            try {
                ivVolumeOverlay.setImageResource(if (muted) R.drawable.ic_volume_off else R.drawable.ic_volume_on)
            } catch (_: Exception) { /* resource safety */ }
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

        // ensure full-screen item height (helps full-viewport carousel behavior)
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

        // cleanup previous
        holder.player?.release()
        holder.progressRunnable?.let { handler.removeCallbacks(it) }

        // create new ExoPlayer for this holder
        val player = ExoPlayer.Builder(context).build()
        holder.player = player
        holder.playerView.player = player
        holder.playerView.useController = false
        holder.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        holder.playerView.setKeepContentOnPlayerReset(true)

        // media item (supports .mp4, .m3u8 etc. depending on player config)
        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = false
        player.repeatMode = Player.REPEAT_MODE_ONE

        holder.tvDescription.text = reel.description ?: ""

        // Play/Pause overlay click - toggle play state
        holder.playerView.setOnClickListener {
            holder.player?.let { p ->
                if (p.isPlaying) {
                    p.pause()
                    holder.btnPlayPause.apply {
                        visibility = View.VISIBLE
                        animate().alpha(1f).setDuration(200).start()
                    }
                } else {
                    p.play()
                    holder.btnPlayPause.apply {
                        animate().alpha(0f).setDuration(200).withEndAction { visibility = View.GONE }
                            .start()
                    }
                }
            }
        }

        // keep play/pause icon consistent with player state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                holder.btnPlayPause.visibility = if (isPlaying) View.GONE else View.VISIBLE
            }
        })

        // progress updater
        holder.progressRunnable = object : Runnable {
            override fun run() {
                val p = holder.player
                if (p != null) {
                    val duration = p.duration
                    if (duration > 0 && duration != Player.TIME_UNSET) {
                        val progress = ((p.currentPosition * 100) / duration).toInt()
                        holder.progressBar.progress = progress.coerceIn(0, 100)
                    }
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(holder.progressRunnable!!)

        // seekbar listener
        holder.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = holder.player
                if (fromUser && p != null) {
                    val duration = p.duration
                    if (duration > 0 && duration != Player.TIME_UNSET) {
                        val seekPosition = (progress / 100f) * duration
                        p.seekTo(seekPosition.toLong())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.progressRunnable?.let { handler.removeCallbacks(it) }
        holder.player?.release()
        holder.player = null
        holder.playerView.player = null
    }

    fun playVisibleVideo(layoutManager: LinearLayoutManager) {
        // find the fully visible item (or first visible if none fully visible)
        val center = layoutManager.findFirstCompletelyVisibleItemPosition().takeIf { it != RecyclerView.NO_POSITION }
            ?: layoutManager.findFirstVisibleItemPosition()

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
