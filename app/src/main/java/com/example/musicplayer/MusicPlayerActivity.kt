package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GestureDetectorCompat
import java.io.IOException
import java.util.concurrent.TimeUnit

class MusicPlayerActivity : AppCompatActivity() {
    private lateinit var mDetector: GestureDetectorCompat
    var player: RelativeLayout? = null
    var titleTv: TextView? = null
    var artist: TextView? = null
    var currentTimeTv: TextView? = null
    var totalTimeTv: TextView? = null
    var seekBar: SeekBar? = null
    var pausePlay: ImageView? = null
    var nextBtn: ImageView? = null
    var previousBtn: ImageView? = null
    var songsList: ArrayList<AudioModel>? = null
    var currentSong: AudioModel? = null
    var moresongbutton: Button? = null
    var mediaPlayer: MediaPlayer? = MyMediaPlayer.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        player = findViewById(R.id.player)
        titleTv = findViewById(R.id.song_title)
        artist = findViewById(R.id.song_artist)
        currentTimeTv = findViewById(R.id.current_time)
        totalTimeTv = findViewById(R.id.total_time)
        seekBar = findViewById(R.id.seek_bar)
        pausePlay = findViewById(R.id.pause_play)
        nextBtn = findViewById(R.id.next)
        previousBtn = findViewById(R.id.previous)
        moresongbutton = findViewById(R.id.more_song_button)
        titleTv?.setSelected(true)
        songsList = intent.getSerializableExtra("LIST") as ArrayList<AudioModel>?
        setResourcesWithMusic()
        mDetector = GestureDetectorCompat(this, GestureListener())
        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    seekBar?.setProgress(mediaPlayer!!.currentPosition)
                    currentTimeTv?.setText(convertToMMSS(mediaPlayer!!.currentPosition.toString()))
                    if (mediaPlayer!!.isPlaying) {
                        pausePlay?.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                    } else {
                        pausePlay?.setImageResource(R.drawable.ic_baseline_play_circle_24)
                    }
                }
                Handler().postDelayed(this, 100)
            }
        })

        mDetector = GestureDetectorCompat(this, GestureListener())

        moresongbutton?.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            if (mediaPlayer!!.isPlaying) {
                pausePlay()
            }
            if (currentSong?.artist == "<unknown>") {
                val newArtist = currentSong?.artist!!.replace("<unknown>", "unknown")
                intent.putExtra("artist", newArtist)
            } else {
                intent.putExtra("artist", currentSong?.artist)
            }
            startActivity(intent)
        }

        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(p0: MotionEvent) {
            if (p0.x > 500) {
                playNextSong()
            } else {
                playPreviousSong()
            }
        }
    }

    fun setResourcesWithMusic() {
        currentSong = songsList!![MyMediaPlayer.currentIndex]
        titleTv?.setText(currentSong!!.title)
        if (currentSong!!.artist == "<unknown>") {
            val newArtist = currentSong!!.artist.replace("<unknown>", "unknown")
            artist?.setText(newArtist)
        } else {
            artist?.setText(currentSong!!.artist)
        }
        totalTimeTv!!.text = convertToMMSS(currentSong!!.duration)
        pausePlay!!.setOnClickListener { pausePlay() }
        nextBtn!!.setOnClickListener { playNextSong() }
        previousBtn!!.setOnClickListener { playPreviousSong() }
        playMusic()
    }

    private fun playMusic() {
        mediaPlayer!!.reset()
        try {
            mediaPlayer!!.setDataSource(currentSong?.path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            seekBar!!.progress = 0
            seekBar!!.max = mediaPlayer!!.duration
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun playNextSong() {
        if (MyMediaPlayer.currentIndex === songsList!!.size - 1) return
        MyMediaPlayer.currentIndex += 1
        mediaPlayer!!.reset()
        setResourcesWithMusic()
    }

    private fun playPreviousSong() {
        if (MyMediaPlayer.currentIndex === 0) return
        MyMediaPlayer.currentIndex -= 1
        mediaPlayer!!.reset()
        setResourcesWithMusic()
    }

    private fun pausePlay() {
        if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause() else mediaPlayer!!.start()
    }

    companion object {
        fun convertToMMSS(duration: String): String {
            val millis = duration.toLong()
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
            )
        }
    }
}