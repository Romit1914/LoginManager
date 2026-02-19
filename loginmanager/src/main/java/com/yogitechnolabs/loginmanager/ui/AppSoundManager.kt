package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.yogitechnolabs.loginmanager.R

object AppSoundManager {

    private var soundPool: SoundPool? = null
    private val soundMap = HashMap<Int, Int>()
    private var isInitialized = false
    private var volume = 1.0f

    /**
     * Call once (Application / MainActivity)
     */
    fun init(context: Context) {
        if (isInitialized) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sounds
        soundMap[R.raw.sound_click] =
            soundPool!!.load(context, R.raw.sound_click, 1)

        soundMap[R.raw.sound_success] =
            soundPool!!.load(context, R.raw.sound_success, 1)

        soundMap[R.raw.sound_error] =
            soundPool!!.load(context, R.raw.sound_error, 1)

        isInitialized = true
    }

    fun play(soundResId: Int) {
        if (!isInitialized) return

        val soundId = soundMap[soundResId] ?: return
        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun setVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isInitialized = false
    }
}
