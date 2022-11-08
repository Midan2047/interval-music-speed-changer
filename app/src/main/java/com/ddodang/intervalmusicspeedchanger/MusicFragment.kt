package com.ddodang.intervalmusicspeedchanger

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ddodang.intervalmusicspeedchanger.databinding.FragmentMusicBinding
import com.ddodang.intervalmusicspeedchanger.util.IntervalMusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding: FragmentMusicBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.ikmyung_dance)
            .into(binding.imageViewIkmyungDance)

        viewLifecycleOwner.lifecycleScope.launch {
            if(IntervalMusicPlayer.musicPlayer?.isPlaying != true) {
                IntervalMusicPlayer.musicPlayer = createMusicPlayer()
                IntervalMusicPlayer.startTimer()
            } else {
                IntervalMusicPlayer.musicList.getOrNull(IntervalMusicPlayer.playingMusicPosition)?.let { musicInfo ->
                    binding.textViewTitle.text = musicInfo.title
                    binding.textViewSinger.text = musicInfo.artist
                }
            }
        }


        binding.imageButtonPlayPause.setOnClickListener {
            if (IntervalMusicPlayer.musicPlayer?.isPlaying != true) {
                playMusic()
            } else {
                stopMusic()
            }
        }

        binding.imageButtonLoop.setOnClickListener {
            IntervalMusicPlayer.isLooping = !IntervalMusicPlayer.isLooping
        }

        binding.imageButtonShuffle.setOnClickListener {
            IntervalMusicPlayer.musicSpeed = 2f
        }

        binding.imageButtonForward.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                stopMusic()
                IntervalMusicPlayer.playingMusicPosition =
                    (IntervalMusicPlayer.playingMusicPosition + 1) % IntervalMusicPlayer.musicList.size
                IntervalMusicPlayer.musicPlayer = createMusicPlayer()
            }
        }

        binding.imageButtonRewind.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                stopMusic()
                IntervalMusicPlayer.playingMusicPosition =
                    (IntervalMusicPlayer.playingMusicPosition + IntervalMusicPlayer.musicList.size - 1) % IntervalMusicPlayer.musicList.size
                IntervalMusicPlayer.musicPlayer = createMusicPlayer()
            }
        }

        return binding.root
    }

    private fun stopMusic() {
        println("Music Stop...")
        IntervalMusicPlayer.musicPlayer?.pause()
        binding.imageButtonPlayPause.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play))
        IntervalMusicPlayer.pauseTimer()
    }

    private fun playMusic() {
        println("Music Start!")
        IntervalMusicPlayer.musicPlayer?.start()
        binding.imageButtonPlayPause.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause))
        IntervalMusicPlayer.startTimer()
    }

    private suspend fun createMusicPlayer(): MediaPlayer? {
        val currentMusic = IntervalMusicPlayer.musicList.getOrNull(IntervalMusicPlayer.playingMusicPosition) ?: return null
        withContext(Dispatchers.Main) {
            binding.textViewTitle.text = currentMusic.title
            binding.textViewSinger.text = currentMusic.artist
        }

        return IntervalMusicPlayer.musicList.getOrNull(IntervalMusicPlayer.playingMusicPosition)?.let { musicInfo ->
            withContext(Dispatchers.IO) {
                MediaPlayer.create(
                    requireContext(),
                    Uri.fromFile(File(musicInfo.location))
                ).also { mediaPlayer ->
                    mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(IntervalMusicPlayer.musicSpeed)
                    mediaPlayer.setOnCompletionListener {
                        println(it)
                        viewLifecycleOwner.lifecycleScope.launch {
                            if (!IntervalMusicPlayer.isLooping) {
                                IntervalMusicPlayer.playingMusicPosition =
                                    (IntervalMusicPlayer.playingMusicPosition + 1) % IntervalMusicPlayer.musicList.size
                            }
                            IntervalMusicPlayer.musicPlayer = createMusicPlayer()
                        }
                        it.stop()
                        it.release()
                    }
                }
            }
        }
    }
}
