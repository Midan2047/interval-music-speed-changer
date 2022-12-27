package com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.FragmentMusicPlayBinding
import com.ddodang.intervalmusicspeedchanger.presentation.service.MusicService
import com.ddodang.intervalmusicspeedchanger.presentation.ui.music.MusicViewModel
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayFragment : Fragment() {

    private val viewModel: MusicPlayViewModel by activityViewModels()

    private var _binding: FragmentMusicPlayBinding? = null
    private val binding: FragmentMusicPlayBinding
        get() = _binding!!

    @Inject
    lateinit var musicPlayer: MusicPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMusicPlayBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.ikmyung_dance)
            .into(binding.imageViewIkmyungDance)

        binding.imageButtonForward.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                requireContext().startService(Intent(requireContext(), MusicService::class.java).apply {
                    action = MusicService.Constants.ACTION.NEXT
                })
            }
        }


        binding.imageButtonRewind.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                requireContext().startService(Intent(requireContext(), MusicService::class.java).apply {
                    action = MusicService.Constants.ACTION.PREVIOUS
                })
            }
        }

        binding.imageButtonPlayPause.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                requireContext().startService(Intent(requireContext(), MusicService::class.java).apply {
                    action = MusicService.Constants.ACTION.TOGGLE_PLAY
                })
            }
        }

        return binding.root
    }
}
