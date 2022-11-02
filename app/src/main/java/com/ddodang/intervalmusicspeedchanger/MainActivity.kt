package com.ddodang.intervalmusicspeedchanger

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ddodang.intervalmusicspeedchanger.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : Activity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        Glide.with(this)
            .asGif()
            .load(R.raw.ikmyung_dance)
            .into(binding.imageViewIkmyungDance)


        mediaPlayer = MediaPlayer.create(this, R.raw.nxde)

        binding.imageButtonPlayPause.setOnClickListener { view ->
            val imageButton = view as? ImageButton ?: return@setOnClickListener

            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause))
            } else {
                mediaPlayer.pause()
                imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play))
            }
        }

        binding.imageButtonLoop.setOnClickListener {
        }
    }
}