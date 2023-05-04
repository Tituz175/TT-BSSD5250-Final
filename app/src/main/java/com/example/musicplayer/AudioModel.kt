package com.example.musicplayer

import java.io.Serializable


class AudioModel(
    var path: String,
    var title: String,
    var duration: String,
    var artist: String,
) :
    Serializable