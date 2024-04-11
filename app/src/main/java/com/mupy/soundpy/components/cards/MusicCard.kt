package com.mupy.soundpy.components.cards

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.components.menus.MenuGenericCard
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.models.Menu
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.TextColor2
import com.mupy.soundpy.ui.theme.WhiteTransparent

/**
 * Card genérico para exibir informações de uma música.
 *
 * @param music A música a ser exibida no card.
 * @param viewModel O ViewModel para acesso aos dados da aplicação.
 * @param navHostController O controlador de navegação.
 * @param context O contexto da aplicação.
 * @param playlistId O ID da playlist atual (padrão: 0).
 * @param remove Uma função para remover a música (padrão: vazio).
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MusicCard(
    music: Music,
    viewModel: ContextMain,
    navHostController: NavHostController?,
    context: Context,
    playlistId: Int = 0,
    remove: (music: Music) -> Unit = {}
) {
    val soundPy by viewModel.soundPy.observeAsState(null)
    val saved by viewModel.saved.observeAsState(listOf())
    val myPlaylists by viewModel.myPlaylists.observeAsState(arrayOf())
    val currentMusic by viewModel.music.observeAsState(null)

    val modifier = Modifier
        .width(60.dp)
        .height(60.dp)
        .background(WhiteTransparent)
        .clip(RectangleShape)
    val isPlaying = music.thumb == currentMusic?.thumb

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPlaying) ColorWhite.copy(alpha = 0.15f) else Color.Transparent, RectangleShape
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                val isSaved = saved.find { it.url == music.url }
                if (isSaved != null && music.thumb != currentMusic?.thumb) {
                    viewModel.movePlaylist(myPlaylists.find { it.playlist.uid == playlistId })
                    soundPy?.open(music)
                    soundPy?.player?.play()
                }

                if (isSaved == null) {
                    viewModel.downloadFile(music = music)
                }
                navHostController?.let {
                    viewModel.navigate(it, "music")
                }
            },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier.widthIn(
                    max = (LocalConfiguration.current.screenWidthDp - 72).dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ImageComponent(
                    linkThumb = music.thumb, byteArray = music.bitImage, modifier = modifier
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = music.title,
                        color = ColorWhite,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = music.author,
                        color = TextColor2,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            ), contentPadding = PaddingValues(0.dp), onClick = {
                viewModel.setMenu(Menu(
                    true
                ) @Composable {
                    MenuGenericCard(
                        music = music,
                        context = context,
                        navController = navHostController,
                        viewModel = viewModel
                    ) {
                        remove(it)
                    }
                })
            }, shape = RectangleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.optuse),
                contentDescription = stringResource(R.string.abrir_menu_de_op_es),
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}