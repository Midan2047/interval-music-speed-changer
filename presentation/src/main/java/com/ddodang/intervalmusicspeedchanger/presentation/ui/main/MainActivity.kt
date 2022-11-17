package com.ddodang.intervalmusicspeedchanger.presentation.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ddodang.intervalmusicspeedchanger.presentation.databinding.ActivityMainBinding
import com.ddodang.intervalmusicspeedchanger.presentation.util.IntervalMusicPlayer

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        IntervalMusicPlayer.initialize(this)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerMain.id) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationViewMain.setupWithNavController(navController)
    }
}