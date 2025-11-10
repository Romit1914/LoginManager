package com.yogitechnolabs.loginmanager.ui.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.model.ReelItem

class ReelAdapter(
    private val items: List<ReelItem>,
    private val onAction: (action: ReelAction, reel: ReelItem) -> Unit
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
        val btnComment: ImageView = view.findViewById(R.id.btnComment)
        val btnShare: ImageView = view.findViewById(R.id.btnShare)
        val btnPlayPause: ImageView = view.findViewById(R.id.btnPlayPause)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val progressBar: SeekBar = view.findViewById(R.id.reelProgressBar)
        val ivVolumeOverlay: ImageView = view.findViewById(R.id.ivVolumeOverlay)
        val ivLikeOverlay: ImageView = view.findViewById(R.id.ivLikeOverlay)

        var player: ExoPlayer? = null
        var progressRunnable: Runnable? = null

        val gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
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
            playerView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }

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

        fun showVolumeIcon(muted: Boolean) {
            ivVolumeOverlay.setImageResource(if (muted) R.drawable.ic_volume_off else R.drawable.ic_volume_on)
            ivVolumeOverlay.visibility = View.VISIBLE
            ivVolumeOverlay.alpha = 1f
            ivVolumeOverlay.animate().alpha(0f).setDuration(800).withEndAction {
                ivVolumeOverlay.visibility = View.GONE
            }.start()
        }

        fun showLikeIcon() {
            ivLikeOverlay.visibility = View.VISIBLE
            ivLikeOverlay.alpha = 1f
            ivLikeOverlay.animate().alpha(0f).setDuration(800).withEndAction {
                ivLikeOverlay.visibility = View.GONE
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reel_item_layout, parent, false)

        // Set height to match parent so full screen item
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ReelViewHolder(view)
    }

    @OptIn(UnstableApi::class)
    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val context = holder.view.context
        val reel = items[position]

        holder.player?.release()
        holder.progressRunnable?.let { handler.removeCallbacks(it) }

        val player = ExoPlayer.Builder(context).build()
        holder.player = player
        holder.playerView.player = player
        holder.playerView.useController = false
        holder.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        holder.playerView.setKeepContentOnPlayerReset(true)

        val mediaItem = MediaItem.fromUri(Uri.parse(reel.videoUrl))
        player.setMediaItem(mediaItem)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()

        player.playWhenReady = true
        holder.btnPlayPause.visibility = View.GONE

        holder.tvDescription.text = reel.description ?: ""

        // GestureDetector for long press to pause + single/double tap already handled in ViewHolder init

        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                holder.player?.pause()
                holder.btnPlayPause.apply {
                    visibility = View.VISIBLE
                    alpha = 1f
                }
            }
        })

        holder.playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            // Forward single/double tap handled in ViewHolder gestureDetector
            holder.gestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (holder.player?.isPlaying == false) {
                        holder.player?.play()
                        holder.btnPlayPause.animate().alpha(0f).setDuration(200)
                            .withEndAction { holder.btnPlayPause.visibility = View.GONE }
                            .start()
                    }
                }
            }
            true
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                holder.btnPlayPause.visibility = if (isPlaying) View.GONE else View.VISIBLE
            }
        })

        holder.progressRunnable = object : Runnable {
            override fun run() {
                val p = holder.player
                if (p != null) {
                    val duration = p.duration
                    if (duration > 0 && duration != C.TIME_UNSET) {
                        val progress = ((p.currentPosition * 100) / duration).toInt()
                        holder.progressBar.progress = progress.coerceIn(0, 100)
                    }
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(holder.progressRunnable!!)

        holder.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = holder.player
                if (fromUser && p != null) {
                    val duration = p.duration
                    if (duration > 0 && duration != C.TIME_UNSET) {
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
}

enum class ReelAction { LIKE, COMMENT, SHARE }
