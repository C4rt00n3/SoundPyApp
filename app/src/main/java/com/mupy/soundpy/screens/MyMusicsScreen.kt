package com.mupy.soundpy.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.adds.BannersAds
import com.mupy.soundpy.components.cards.MusicCard
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.ui.theme.ColorWhite

/**
 * Função Composable que exibe a tela de músicas do usuário.
 *
 * @param viewModel O ViewModel [ContextMain] usado para recuperar e gerenciar dados.
 * @param navHostController O [NavHostController] usado para navegação dentro do aplicativo.
 * @param context O [Context] usado para acessar recursos e serviços do sistema.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MyMusicsScreen(
    viewModel: ContextMain,
    navHostController: NavHostController,
    context: Context
) {
    // Recupera as playlists do usuário a partir do ViewModel
    val myPlaylist by viewModel.myPlaylists.observeAsState(arrayOf())

    // Inicializa uma lista de objetos Music a partir da primeira playlist do usuário, invertida
    var array: List<Music> by remember { mutableStateOf(myPlaylist.getOrNull(0)?.musicList.orEmpty().reversed()) }

    // Compondo o layout da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 9.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Texto do título
        Text(
            text = stringResource(R.string.my_musics),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColorWhite,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // LazyColumn para exibir a lista de cartões de música
        LazyColumn {
            items(array) { music ->
                // Compondo um MusicCard para cada item de música na lista
                MusicCard(
                    music = music,
                    viewModel = viewModel,
                    navHostController = navHostController,
                    context = context,
                    playlistId = 1
                ) {
                    // Remove a música da lista quando o cartão é descartado
                    array = array.filter { it.id != music.id }
                }
            }
        }
    }
}
