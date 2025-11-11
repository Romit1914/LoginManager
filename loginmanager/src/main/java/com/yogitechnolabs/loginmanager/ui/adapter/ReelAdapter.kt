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
import androidx.annotation.OptIn
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

    // Keep track of active holders by position
    private val holders = mutableMapOf<Int, ReelViewHolder>()

    // Track liked state for each item (position -> liked)
    private val likedStates = mutableMapOf<Int, Boolean>()

    inner class ReelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
        val btnComment: ImageView = view.findViewById(R.id.btnComment)
        val btnShare: ImageView = view.findViewById(R.id.btnShare)
        val btnPlayPause: ImageView = view.findViewById(R.id.btnPlayPause)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val progressBar: SeekBar = view.findViewById(R.id.reelProgressBar)
        val ivLikeOverlay: ImageView = view.findViewById(R.id.ivLikeOverlay)

        var player: ExoPlayer? = null
        var progressRunnable: Runnable? = null

        val gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                player?.let {
                    if (it.isPlaying) {
                        it.pause()
                        btnPlayPause.visibility = View.VISIBLE
                    } else {
                        it.play()
                        btnPlayPause.visibility = View.GONE
                    }
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                toggleLike()
                return true
            }
        })

        init {
            playerView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }

            btnLike.setOnClickListener {
                toggleLike()
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

        fun toggleLike() {
            val pos = bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return

            // Toggle like state
            val newLikedState = !(likedStates[pos] ?: false)
            likedStates[pos] = newLikedState

            // Update UI accordingly
            updateLikeButton(newLikedState)

            // Show like animation overlay if liked
            if (newLikedState) showLikeIcon()

            // Notify listener
            onAction(ReelAction.LIKE, items[pos])
        }

        fun updateLikeButton(liked: Boolean) {
            btnLike.setImageResource(
                if (liked) R.drawable.ic_like_filled else R.drawable.ic_like_outline
            )
        }

        fun showLikeIcon() {
            ivLikeOverlay.visibility = View.VISIBLE
            ivLikeOverlay.alpha = 1f
            ivLikeOverlay.animate().alpha(0f).setDuration(800).withEndAction {
                ivLikeOverlay.visibility = View.GONE
            }.start()
        }

        fun play() {
            player?.play()
            btnPlayPause.visibility = View.GONE
        }

        fun pause() {
            player?.pause()
            btnPlayPause.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reel_item_layout, parent, false)

        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ReelViewHolder(view)
    }

    @OptIn(UnstableApi::class)
    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holders[position] = holder

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

        // Auto play on bind
        holder.play()

        holder.tvDescription.text = reel.description ?: ""

        // Update like button state when binding
        val liked = likedStates[position] ?: false
        holder.updateLikeButton(liked)

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

        val position = holder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            holders.remove(position)
            likedStates.remove(position)
        }
    }

    // Call this from your Activity/Fragment on scroll or page change
    fun playVideoAtPosition(position: Int) {
        holders.forEach { (pos, holder) ->
            if (pos == position) {
                holder.play()
            } else {
                holder.pause()
            }
        }
    }
}

enum class ReelAction { LIKE, COMMENT, SHARE }
