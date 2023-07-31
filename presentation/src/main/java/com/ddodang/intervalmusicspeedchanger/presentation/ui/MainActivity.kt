package com.ddodang.intervalmusicspeedchanger.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ddodang.intervalmusicspeedchanger.presentation.model.Screen
import com.ddodang.intervalmusicspeedchanger.presentation.ui.music.download.MusicDownloadScreen
import com.ddodang.intervalmusicspeedchanger.presentation.ui.music.list.MusicListScreen
import com.ddodang.intervalmusicspeedchanger.presentation.ui.music.play.MusicPlayScreen
import com.ddodang.intervalmusicspeedchanger.presentation.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.MusicList.route) {
                    composable(Screen.MusicList.route) {
                        MusicListScreen(onNavigate = { screen -> navController.navigate(screen.route) })
                    }
                    composable(Screen.Download.route) {
                        MusicDownloadScreen(onBackPressed = { navController.popBackStack(Screen.Download.route, inclusive = true) })
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(onBackPressed = { navController.popBackStack(Screen.Settings.route, inclusive = true) })
                    }
                }
            }
        }
    }

}