package com.example.shakeit.util

import android.content.Context
import android.media.MediaPlayer

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isLoopingPart = false
    private var currentResourceId: Int? = null // Traccia attualmente in riproduzione
    private var lastStartMs: Int = 0
    private var lastEndMs: Int? = null
    private var lastLoop: Boolean = true
    var isMuted: Boolean = false
        private set

    fun playSound(resourceId: Int, loop: Boolean = true, startMs: Int = 0, endMs: Int? = null) {
        if (currentResourceId == resourceId && mediaPlayer?.isPlaying == true) {
            // Se il suono è già in riproduzione, non fare nulla
            return
        }

        stopSound()
        isLoopingPart = endMs != null
        currentResourceId = resourceId
        lastStartMs = startMs
        lastEndMs = endMs
        lastLoop = loop

        if (!isMuted) {
            mediaPlayer = MediaPlayer.create(context, resourceId).apply {
                isLooping = loop && endMs == null // Loop normale se non c'è un intervallo
                start()

                if (isLoopingPart) {
                    // Controlla l'intervallo
                    seekTo(startMs)
                    setOnSeekCompleteListener {
                        start()
                    }

                    setOnCompletionListener {
                        // Quando arriva alla fine dell'intervallo, riparte
                        if (currentPosition >= endMs!!) {
                            seekTo(startMs)
                        }
                    }
                }
            }
        }
    }

    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResourceId = null
    }


    fun isPlaying(resourceId: Int): Boolean {
        return currentResourceId == resourceId && mediaPlayer?.isPlaying == true
    }
}
