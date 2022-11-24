package com.ddodang.intervalmusicspeedchanger.presentation.ui.music_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.FragmentMusicListBinding
import com.ddodang.intervalmusicspeedchanger.presentation.model.LoadingState
import com.ddodang.intervalmusicspeedchanger.presentation.ui.dialog.LoadingDialog
import com.ddodang.intervalmusicspeedchanger.presentation.util.MusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicListFragment : Fragment() {

    private val viewModel: MusicListViewModel by viewModels()

    private var _binding: FragmentMusicListBinding? = null
    private val binding: FragmentMusicListBinding
        get() = _binding!!

    @Inject
    lateinit var musicPlayer: MusicPlayer

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val adapter = MusicListAdapter(
        onClick = { music ->
            musicPlayer.setMusicList(viewModel.musicListFlow.value)
            findNavController().navigate(
                MusicListFragmentDirections.actionFragmentMusicListToFragmentMusic(
                    music
                )
            )
        },
        onDelete = { music ->
            viewModel.deleteMusic(music)
        }
    )

    private val addItemLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { intent ->
            val fileUri = intent.data?.data ?: return@registerForActivityResult
            viewModel.copyMusic(fileUri.toString())
        }

    override fun onStart() {
        super.onStart()
        setFlowListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        viewModel.loadMusicList()

        binding.imageButtonAddMusic.setOnClickListener {
            addItemLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            })
        }

        binding.recyclerViewMusic.adapter = adapter
        viewModel.loadMusicList()

        return binding.root
    }

    private fun setFlowListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                viewModel.musicListFlow.collect { musicList ->
                    adapter.submitList(musicList)
                }
            }
            launch {
                viewModel.loadingFlow.collect { loadingState ->
                    when (loadingState) {
                        is LoadingState.Show -> loadingDialog.show(loadingState.message)
                        LoadingState.NotShowing -> loadingDialog.dismiss()
                    }
                }
            }
        }
    }
}