package com.mupy.soundpy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.mupy.soundpy.components.adds.showInterstialAd
import com.mupy.soundpy.database.AppDatabase
import com.mupy.soundpy.screens.Screens
import com.mupy.soundpy.ui.theme.SoundPyTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
        )
        showInterstialAd(this)
        setContent {
            val dataBase = Room.databaseBuilder(
                this, AppDatabase::class.java, "myPlaylists"
            ).fallbackToDestructiveMigration().build()
            val navController: NavHostController = rememberNavController()
            val name = this.getString(R.string.minhas_musicas)
            runBlocking {
                val getOne = dataBase.service().getAllPlaylistsWithMusic()
                if (getOne.isEmpty()) dataBase.service().createMyPlaylist(null, name)
            }
            SoundPyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Screens(
                        navController = navController,
                        context = this,
                        viewModel = ContextMain(this, dataBase)
                    )
                }
            }
        }
    }
}
