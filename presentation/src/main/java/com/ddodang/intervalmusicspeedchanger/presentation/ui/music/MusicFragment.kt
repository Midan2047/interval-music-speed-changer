package com.ddodang.intervalmusicspeedchanger.presentation.ui.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.FragmentMusicBinding
import com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play.MusicPlayFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    private var _binding: FragmentMusicBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        collectEventFlow()
        return binding.root
    }

    private fun collectEventFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    MusicViewModel.Event.ShowMusicPlayFragment -> {
                        childFragmentManager.beginTransaction()
                            .add(binding.fragmentContainerMusic.id, MusicPlayFragment())
                            .commit()
                    }
                }
            }
        }
    }
}