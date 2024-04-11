package com.mupy.soundpy.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.adds.BannersAds
import com.mupy.soundpy.components.cards.MusicCard
import com.mupy.soundpy.components.cards.TopPlaylistCard


/**
 * Tela de detalhes de uma playlist.
 *
 * @param viewModel ViewModel para acesso aos dados da aplicação.
 * @param navHostController Controlador de navegação.
 * @param context Contexto da aplicação.
 * @param backStackEntry Entrada da pilha de navegação.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PlaylistDetailScreen(
    viewModel: ContextMain,
    navHostController: NavHostController,
    context: Context,
    backStackEntry: NavBackStackEntry
) {
    val playlistId = backStackEntry.arguments?.getString("uid")?.toInt() ?: 1
    val playlists by viewModel.myPlaylists.observeAsState(arrayOf())
    val currentPlaylist by viewModel.currentPlaylist.observeAsState(null)
    val isPaused by viewModel.pause.observeAsState(false)

    val currentPlaylistData = playlists.find { it.playlist.uid == playlistId }

    var isCurrentPlaylist by remember { mutableStateOf(currentPlaylistData?.playlist?.uid == currentPlaylist?.playlist?.uid) }

    currentPlaylistData?.let { playlist ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopPlaylistCard(playlist.toPlayListData()) {
                navHostController.navigateUp()
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Implement shuffle */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.action_shuffle),
                        contentDescription = stringResource(id = R.string.progresso_linear),
                    )
                }
                IconButton(onClick = {
                    if (!isCurrentPlaylist) {
                        viewModel.movePlaylist(playlist)
                        isCurrentPlaylist = true
                    }
                    if (playlist.playlist.uid == currentPlaylist?.playlist?.uid && isPaused) {
                        viewModel.soundPy.value?.player?.pause()
                    } else {
                        viewModel.soundPy.value?.player?.play()
                    }
                }) {
                    Image(
                        painter = painterResource(
                            id = if (!(isCurrentPlaylist && isPaused)) R.drawable.action_play else R.drawable.action_pause
                        ),
                        contentDescription = stringResource(id = R.string.iniciar_playlist),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 9.dp)
            ) {
                items(playlist.musicList.reversed()) { musics ->
                    MusicCard(
                        music = musics,
                        viewModel = viewModel,
                        navHostController = navHostController,
                        context = context,
                        playlistId = playlist.playlist.uid
                    ) {}
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}