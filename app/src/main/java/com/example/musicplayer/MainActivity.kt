package com.example.musicplayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class MainActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var noMusicTextView: TextView? = null
    var songsList = ArrayList<AudioModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        noMusicTextView = findViewById(R.id.no_songs_text)
        if (!checkPermission()) {
            requestPermission()
            return
        }
        loadMusic()
    }

    private fun loadMusic() {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.TITLE
        )
        while (cursor!!.moveToNext()) {
            val songData = if (cursor.getString(2) != null) {
                AudioModel(
                    cursor.getString(1),
                    cursor.getString(0),
                    cursor.getString(2),
                    cursor.getString(3),
                )
            } else {
                AudioModel(
                    cursor.getString(1), cursor.getString(0), "", cursor.getString(3),
                )
            }
            if (File(songData.path).exists()) songsList.add(songData)
        }
        if (songsList.size == 0) {
            noMusicTextView?.visibility = View.VISIBLE
        } else {
            //recyclerview
            recyclerView?.setLayoutManager(LinearLayoutManager(this))
            recyclerView?.setAdapter(MusicListAdapter(songsList, applicationContext))
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@MainActivity,
                "READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTTINGS",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )
        }
    }


    override fun onResume() {
        super.onResume()
        if (recyclerView != null) {
            recyclerView!!.adapter = MusicListAdapter(songsList, applicationContext)
        }
    }
}


