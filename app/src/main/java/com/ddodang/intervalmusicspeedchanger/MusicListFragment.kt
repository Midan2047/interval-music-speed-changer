package com.ddodang.intervalmusicspeedchanger

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ddodang.intervalmusicspeedchanger.databinding.FragmentMusicListBinding
import com.ddodang.intervalmusicspeedchanger.util.IntervalMusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MusicListFragment : Fragment() {

    private var _binding: FragmentMusicListBinding? = null
    private val binding: FragmentMusicListBinding
        get() = _binding!!

    private val adapter = MusicListAdapter(
        onClick = { position ->
            IntervalMusicPlayer.playingMusicPosition = position
            findNavController().navigate(R.id.action_fragment_music_list_to_fragment_music)
        },
        onDelete = { position ->
            IntervalMusicPlayer.deleteMusic(requireContext(), position)
        }
    )

    private val addItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { intent ->
        val fileUri = intent.data?.data ?: return@registerForActivityResult

        val resolver = requireContext().contentResolver
        val inputStream = resolver.openInputStream(fileUri) ?: return@registerForActivityResult
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileUri.pathSegments[1].split("/").last())
                    file.createNewFile()
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var length = 0
                    do {
                        length = inputStream.read(buffer)
                        outputStream.write(buffer, 0, length)
                    } while (length > 0)
                    outputStream.close()
                    inputStream.close()
                } catch (ex: Exception) {

                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        IntervalMusicPlayer.onMusicListChangedListeners.add {
            adapter.submitList(IntervalMusicPlayer.musicList)
        }

        binding.imageButtonAddMusic.setOnClickListener {
            addItemLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            })
        }

        binding.recyclerViewMusic.adapter = adapter
        adapter.submitList(IntervalMusicPlayer.musicList)

        return binding.root
    }
}