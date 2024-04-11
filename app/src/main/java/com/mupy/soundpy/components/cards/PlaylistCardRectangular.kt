package com.mupy.soundpy.components.cards

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.database.PlaylistWithMusic
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.WhiteTransparent

/**
 * Composable que exibe um cartão retangular de uma playlist.
 *
 * @param viewModel O ViewModel para acessar os dados e a lógica do aplicativo.
 * @param playlist A playlist com as músicas a serem exibidas no cartão.
 * @param navHostController O controlador de navegação para navegar para a tela da playlist.
 * @param context O contexto atual da aplicação.
 */
@Composable
fun PlaylistCardRectangular(
    viewModel: ContextMain,
    playlist: PlaylistWithMusic,
    navHostController: NavHostController,
    context: Context
) {
    val modifier = Modifier
        .width(60.dp)
        .height(60.dp)
        .background(WhiteTransparent)
        .clip(ShapeDefaults.Medium)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(ShapeDefaults.Medium)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    val uid = playlist.playlist.uid
                    navHostController.navigate("open_playlists/$uid")
                })
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageComponent(
                linkThumb = playlist.musicList.getOrNull(0)?.thumb ?: "",
                byteArray = playlist.musicList.getOrNull(0)?.bitImage,
                modifier = modifier
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = playlist.playlist.name ?: "",
                maxLines = 1,
                fontSize = 18.sp,
                color = ColorWhite,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (playlist.playlist.uid != 1) {
            IconButton(onClick = {
                viewModel.removePlaylist(playlist)
                Toast.makeText(
                    context, context.getText(R.string.musica_adicionada), Toast.LENGTH_SHORT
                ).show()
            }) {
                Image(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = stringResource(R.string.adiciona_na_playlist),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
