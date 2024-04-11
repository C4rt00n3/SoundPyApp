@file:Suppress("UNCHECKED_CAST")

package com.mupy.soundpy.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.CarouselImage
import com.mupy.soundpy.components.Title
import com.mupy.soundpy.components.adds.BannersAds
import com.mupy.soundpy.components.cards.ArtistCard
import com.mupy.soundpy.components.cards.CardMusic
import com.mupy.soundpy.database.Music
import kotlin.contracts.contract


/**
 * Composable function representing the home screen of the application.
 *
 * @param viewModel The ViewModel containing the data for the home screen.
 * @param navController The NavController for navigating between screens.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HomeScreen(
    viewModel: ContextMain, navController: NavHostController
) {
    // Observes the states of the viewModel
    val myPlaylist by viewModel.myPlaylists.observeAsState(arrayOf())
    val playListsCards by viewModel.playlistsCards.observeAsState(arrayOf())
    val soundPy by viewModel.soundPy.observeAsState(null)
    val currentPlaylist by viewModel.currentPlaylist.observeAsState(null)

    // Defines the structure of the main screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        Title(stringResource(R.string.recenly_played), padding = 20)
        // Horizontal list of cards for recently played music
        LazyRow {
            val list: MutableList<Music?> =
                (myPlaylist.getOrNull(0)?.musicList?.reversed()?.toMutableList() ?: mutableListOf()) as MutableList<Music?>

            if (list.size < 5) for (i in 0..(5 - list.size)) {
                list.add(null)
            }

            items(
                list
            ) { music ->
                CardMusic(music = music, onClick = {
                    if (currentPlaylist?.playlist?.uid != 1) viewModel.movePlaylist(null)
                    val get = soundPy?.getMetadata()
                    if (it?.id != get?.id ) it?.let { soundPy?.open(it) }
                    soundPy?.player?.play()
                    viewModel.navigate(navController, "music")
                }, onDelete = { it?.let { it1 -> viewModel.deleteMusic(it1) {} } })
            }
        }
        // Carousel of playlist images
        CarouselImage(playListsCards, navController, viewModel)
        // Title for the user's playlists section
        Title(text = stringResource(id = R.string.minhas_playlists))
        // Horizontal list of user's playlists cards
        LazyRow {
            items(myPlaylist) { playlist ->
                ArtistCard(playlist) {
                    viewModel.navigate(navController, "open_playlists/$it")
                }
            }
        }
        // Loads the user's playlists on startup
        LaunchedEffect(Unit) {
            viewModel.getPlaylists()
        }
    }
}