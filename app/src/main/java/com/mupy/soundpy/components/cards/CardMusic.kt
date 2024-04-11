package com.mupy.soundpy.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mupy.soundpy.R
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.SoundPyTheme
import com.mupy.soundpy.ui.theme.WhiteTransparent

/**
 * Composable que exibe um cartão de música.
 *
 * @param music A música a ser exibida no cartão.
 * @param onClick A ação a ser executada quando o cartão é clicado.
 * @param onDelete A ação a ser executada quando o cartão é pressionado por um longo tempo.
 */
@Composable
fun CardMusic(
    music: Music?,
    onClick: (Music?) -> Unit,
    onDelete: (Music?) -> Unit
) {
    val brush = Brush.verticalGradient(
        colors = listOf(
            ColorWhite.copy(0.5f),
            ColorWhite.copy(0.4f),
            ColorWhite.copy(0.3f),
            ColorWhite.copy(0.2f),
            ColorWhite.copy(0.1f)
        )
    )

    val modifier = Modifier
        .size(185.dp)
        .padding(bottom = 16.dp)
        .clip(ShapeDefaults.Medium)
        .background(WhiteTransparent, ShapeDefaults.Medium)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick(music) },
                    onLongPress = { onDelete(music) })
            }
    ) {
        // Exibe a imagem da música, se disponível
        if (music?.bitImage != null) {
            ImageComponent(
                linkThumb = music.thumb,
                byteArray = music.bitImage,
                modifier = modifier,
                contentScale = ContentScale.FillBounds
            )
        } else {
            Box(modifier = modifier.background(brush = brush))
        }

        Text(
            text = music?.title?: stringResource(id = R.string.carregando),
            fontWeight = FontWeight.W600,
            color = ColorWhite,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }

    Spacer(modifier = Modifier.width(50.dp))
}
