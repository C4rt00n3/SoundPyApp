package com.mupy.soundpy.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.components.adds.BannersAds
import com.mupy.soundpy.components.cards.MusicCard
import com.mupy.soundpy.components.cards.TopPlaylistCard
import com.mupy.soundpy.database.Music

/**
 * Função Composable que exibe a tela de uma playlist, mostrando suas músicas.
 *
 * @param viewModel O ViewModel [ContextMain] usado para recuperar e gerenciar dados.
 * @param navHostController O [NavHostController] usado para navegação dentro do aplicativo.
 * @param context O [Context] usado para acessar recursos e serviços do sistema.
 * @param backStackEntry Entrada da pilha de navegação.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PlaylistScreen(
    viewModel: ContextMain,
    navHostController: NavHostController,
    context: Context,
    backStackEntry: NavBackStackEntry
) {
    // pega o id da playlist
    val url = backStackEntry.arguments?.getString("url") ?: ""

    // Observa as mudanças nos dados da playlist selecionada
    val playlistData by viewModel.playListData.observeAsState(null)

    // Observa as mudanças nas playlists disponíveis
    val playlist by viewModel.playlists.observeAsState(emptyList())

    // Encontra a playlist atual com base na URL fornecida
    val current = playlist.find { it.link == url }

    // Estado para armazenar as músicas da playlist atual
    var musics by remember { mutableStateOf(emptyList<Music>()) }

    // Compondo o layout da tela
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cartão superior da playlist
        TopPlaylistCard(playlistData) {
            navHostController.navigateUp()
        }

        // Espaçador entre o cartão e os botões de controle
        Spacer(modifier = Modifier.height(16.dp))

        // Espaçador entre os botões e a lista de músicas
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de músicas da playlist
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 9.dp)
        ) {
            items(musics) { music ->
                MusicCard(
                    music = music,
                    viewModel = viewModel,
                    navHostController = navHostController,
                    context = context
                ) {
                    // Remove a música da lista quando o cartão é descartado
                    musics = musics.filter { it.id != music.id }
                }

                // Espaçador entre os cartões de música
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Atualiza a lista de músicas ao trocar para uma nova playlist
    if (current != null) {
        musics = current.musics
    }
}