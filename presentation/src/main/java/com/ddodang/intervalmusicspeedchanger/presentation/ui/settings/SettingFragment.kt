package com.ddodang.intervalmusicspeedchanger.presentation.ui.settings

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
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ddodang.intervalmusicspeedchanger.presentation.R
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

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

        viewModel.initialize()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Glide.with(requireContext())
            .asGif()
            .load(R.raw.shouting_ikmyung)
            .into(binding.imageButtonContact)

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