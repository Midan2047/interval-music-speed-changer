package com.ddodang.intervalmusicspeedchanger.presentation.ui.music_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.FragmentMusicListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicListFragment : Fragment() {

    private val viewModel: MusicListViewModel by viewModels()

    private var _binding: FragmentMusicListBinding? = null
    private val binding: FragmentMusicListBinding
        get() = _binding!!

    private val adapter = MusicListAdapter(
        onClick = { music ->
            findNavController().navigate(
                MusicListFragmentDirections.actionFragmentMusicListToFragmentMusic(
                    viewModel.musicListFlow.value.toTypedArray(),
                    music
                )
            )
        },
        onDelete = { music ->
            viewModel.deleteMusic(music)
        }
    )

    private val addItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { intent ->
        val fileUri = intent.data?.data ?: return@registerForActivityResult
        val filePath = fileUri.toFile().absolutePath
        viewModel.copyMusic(filePath)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        viewModel.loadMusicList()

        binding.imageButtonAddMusic.setOnClickListener {
            addItemLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            })
        }

        binding.recyclerViewMusic.adapter = adapter
        setFlowListener()
        viewModel.loadMusicList()

        return binding.root
    }

    private fun setFlowListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.musicListFlow.collect { musicList ->
                adapter.submitList(musicList)
            }
        }
    }
}