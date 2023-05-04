package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.MusicListAdapter.ViewHolder

class MusicListAdapter(songsList: ArrayList<AudioModel>, context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    var songsList: ArrayList<AudioModel>
    var context: Context

    init {
        this.songsList = songsList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val songData: AudioModel = songsList[position]
        holder.titleTextView.setText(songData.title)
        if (songData.artist == "<unknown>") {
            val newArtist = songData.artist.replace("<unknown>", "unknown")
            holder.artist.setText(newArtist)
        } else {
            holder.artist.setText(songData.artist)
        }
        if (MyMediaPlayer.currentIndex === position) {
            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"))
        } else {
            holder.titleTextView.setTextColor(Color.parseColor("#FFFFFF"))
        }
        holder.itemView.setOnClickListener { //navigate to another acitivty
            MyMediaPlayer.getInstance()?.reset()
            MyMediaPlayer.currentIndex = position
            val intent = Intent(context, MusicPlayerActivity::class.java)
            intent.putExtra("LIST", songsList)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        var iconImageView: ImageView
        var artist: TextView

        init {
            titleTextView = itemView.findViewById(R.id.music_title_text)
            iconImageView = itemView.findViewById(R.id.icon_view)
            artist = itemView.findViewById(R.id.music_artist_text)
        }
    }
}