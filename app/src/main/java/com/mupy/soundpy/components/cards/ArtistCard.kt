package com.mupy.soundpy.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.database.PlaylistWithMusic
import com.mupy.soundpy.ui.theme.ColorWhite

/**
 * Composable que representa um cartão de artista.
 *
 * @param playlistWithMusic O objeto [PlaylistWithMusic] contendo informações sobre o artista e suas músicas.
 * @param navController O [NavHostController] usado para navegar para a lista de reprodução do artista.
 * @param navCall A callBack [(Int) -> Unit] é usada para navegar entre telas
 */
@Composable
fun ArtistCard(playlistWithMusic: PlaylistWithMusic, navCall: (Int) -> Unit) {
    // Modificador para o botão
    val buttonModifier = Modifier
        .background(Color.Transparent, RectangleShape)
        .padding(0.dp)

    // Modificador para a imagem do artista
    val imageModifier = Modifier
        .size(128.dp)
        .clip(CircleShape)
    Button(
        onClick = { navCall(playlistWithMusic.playlist.uid) },
        modifier = buttonModifier
    ) {
        Column(
            modifier = Modifier.padding(end = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exibir imagem do artista ou gradiente de cores se não houver imagem
            val music = playlistWithMusic.musicList.lastOrNull()
            ImageComponent(
                linkThumb = music?.thumb ?: "",
                byteArray = music?.bitImage,
                modifier = imageModifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exibir o nome do artista
            Text(
                text = playlistWithMusic.playlist.name ?: "",
                fontSize = 12.sp,
                color = ColorWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
