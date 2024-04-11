package com.mupy.soundpy.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.IconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.adds.BannersAds
import com.mupy.soundpy.components.icons.rememberArrowCircleLeft
import com.mupy.soundpy.components.icons.rememberArrowCircleRight
import com.mupy.soundpy.components.icons.rememberMotionPhotosPaused
import com.mupy.soundpy.components.icons.rememberPlayCircle
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.LineColor
import com.mupy.soundpy.utils.SoundPy

/**
 * Composable que exibe o player de música.
 *
 * @param viewModel O ViewModel que contém os dados necessários para o player.
 * @param navController O NavController usado para navegação.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Player(
    viewModel: ContextMain,
    navController: NavHostController,
) {
    // Observa o estado atual da música e do player
    val music: Music? by viewModel.music.observeAsState(null)
    val soundPy: SoundPy? by viewModel.soundPy.observeAsState(null)
    val pause by viewModel.pause.observeAsState(false)
    val palette by viewModel.palette.observeAsState(null)
    val brush by viewModel.brush.observeAsState(listOf())

    BannersAds()
    // Verifica se há uma música sendo reproduzida
    if (music != null) {
        // Layout principal do player
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(
                    brush = Brush.linearGradient(colors = brush.map { color -> color.copy(alpha = 0.5f) }),
                    shape = ShapeDefaults.Medium
                ),
        ) {
            // Barra de progresso da música
            AudioProgressSlider(
                viewModel = viewModel, activeTrackColor = Color(
                    palette?.getVibrantColor(Color.Black.hashCode()) ?: LineColor.hashCode(),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp), thumbColor = Color.Transparent
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Detalhes da música (imagem, título, autor)
                MusicDetails(music = music, navController = navController)
                // Controles de reprodução (voltar, pausar/reproduzir, avançar)
                MusicControls(soundPy = soundPy, pause = pause)
            }
        }
    }
}

/**
 * Composable que exibe os detalhes da música (imagem, título, autor).
 *
 * @param music A música atual sendo reproduzida.
 * @param navController O NavController usado para navegação.
 */
@Composable
private fun MusicDetails(music: Music?, navController: NavHostController) {
    // Layout dos detalhes da música
    Row(
        modifier = Modifier
            .height(60.dp)
            .clip(ShapeDefaults.Medium)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { navController.navigate("favorites") })
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Imagem da música
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.widthIn(
                max = (LocalConfiguration.current.screenWidthDp - 200).dp
            )
        ) {
            ImageComponent(
                music?.thumb ?: "",
                byteArray = music?.bitImage,
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight(),
                contentDescription = stringResource(id = R.string.imagem_da_musica) + music?.title,
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                // Título e autor da música
                Text(
                    text = music?.title ?: stringResource(R.string.carregando),
                    maxLines = 1,
                    fontSize = 16.sp,
                    color = ColorWhite,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Autor da musica
                Text(
                    text = music?.author ?: stringResource(R.string.carregando),
                    maxLines = 1,
                    fontSize = 12.sp,
                    color = ColorWhite.copy(0.6f),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Composable que exibe os controles de reprodução (voltar, pausar/reproduzir, avançar).
 *
 * @param soundPy O objeto SoundPy utilizado para controlar o player de música.
 * @param pause Indica se o player está pausado.
 */
@Composable
private fun MusicControls(soundPy: SoundPy?, pause: Boolean) {
    // Layout dos controles de reprodução
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(60.dp)
    ) {
        // Ícone de voltar
        IconButton(onClick = {
            @Suppress("DEPRECATION") soundPy?.player?.previous()
        }) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = rememberArrowCircleLeft(),
                contentDescription = stringResource(R.string.ir_para_musica_passada),
            )
        }
        // Ícone de pausar/reproduzir
        IconButton(onClick = {
            if (!pause) {
                soundPy?.player?.play()
            } else {
                soundPy?.player?.pause()
            }
        }) {
            // Ícone de pausar/reproduzir
            Image(
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Crop,
                imageVector = if (pause) rememberMotionPhotosPaused() else rememberPlayCircle(),
                contentDescription = if(pause) stringResource(R.string.pausa_musica) else stringResource(
                    R.string.play_na_musica
                ),
            )
        }
        // Ícone de avançar
        IconButton(onClick = {
            @Suppress("DEPRECATION") soundPy?.player?.next()
        }) {
            // Ícone de avançar
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = rememberArrowCircleRight(),
                contentDescription = stringResource(R.string.proxima_musica),
            )
        }
    }
}