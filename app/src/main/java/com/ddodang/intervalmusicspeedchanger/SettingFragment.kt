package com.ddodang.intervalmusicspeedchanger

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ddodang.intervalmusicspeedchanger.databinding.FragmentSettingsBinding
import com.ddodang.intervalmusicspeedchanger.util.IntervalMusicPlayer

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            callToDeveloper()
        } else {
            Toast.makeText(requireContext(), "전화를 하려면 허락해줘요!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.shouting_ikmyung)
            .into(binding.imageButtonContact)

        binding.textViewSetsCount.text = IntervalMusicPlayer.setCount.toString()
        binding.textViewWalkCount.text = IntervalMusicPlayer.walkingTime.toString()
        binding.textViewRunCount.text = IntervalMusicPlayer.runningTime.toString()


        binding.imageButtonIncreaseSets.setOnClickListener {
            IntervalMusicPlayer.setCount = IntervalMusicPlayer.setCount + 1
            binding.textViewSetsCount.text = IntervalMusicPlayer.setCount.toString()
        }

        binding.imageButtonReduceSets.setOnClickListener {
            IntervalMusicPlayer.setCount = (IntervalMusicPlayer.setCount - 1).coerceAtLeast(0)
            binding.textViewSetsCount.text = IntervalMusicPlayer.setCount.toString()
        }


        binding.imageButtonIncreaseWalk.setOnClickListener {
            IntervalMusicPlayer.walkingTime = IntervalMusicPlayer.walkingTime + 1
            binding.textViewWalkCount.text = IntervalMusicPlayer.walkingTime.toString()
        }

        binding.imageButtonReduceWalk.setOnClickListener {
            IntervalMusicPlayer.walkingTime = (IntervalMusicPlayer.walkingTime - 1).coerceAtLeast(0)
            binding.textViewWalkCount.text = IntervalMusicPlayer.walkingTime.toString()
        }

        binding.imageButtonIncreaseRun.setOnClickListener {
            IntervalMusicPlayer.runningTime = IntervalMusicPlayer.runningTime + 1
            binding.textViewRunCount.text = IntervalMusicPlayer.runningTime.toString()
        }

        binding.imageButtonReduceRun.setOnClickListener {
            IntervalMusicPlayer.runningTime = (IntervalMusicPlayer.runningTime - 1).coerceAtLeast(0)
            binding.textViewRunCount.text = IntervalMusicPlayer.runningTime.toString()
        }

        binding.buttonSaveTimerSettings.setOnClickListener {
            IntervalMusicPlayer.saveSettings()
        }

        binding.imageButtonContact.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
            } else {
                callToDeveloper()
            }
        }

        return binding.root
    }

    private fun callToDeveloper() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01090382047"))
        startActivity(intent)
    }
}