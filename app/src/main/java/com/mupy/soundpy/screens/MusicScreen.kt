package com.mupy.soundpy.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.AudioProgressSlider
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.components.icons.rememberArrowCircleLeft
import com.mupy.soundpy.components.icons.rememberArrowCircleRight
import com.mupy.soundpy.components.icons.rememberMotionPhotosPaused
import com.mupy.soundpy.components.icons.rememberPlayCircle
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.LineColor

/**
 * MusicScreen: Componente Jetpack Compose que exibe a tela de reprodução de música.
 *
 * @param viewModel O ViewModel [ContextMain] usado para recuperar e gerenciar dados.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MusicScreen(viewModel: ContextMain) {
    /*
    * O componente MusicScreen renderiza uma imagem de fundo com base na música atual, exibe informações sobre
    * a música (título, artista), controles de reprodução (pausar/reproduzir, próxima/anterior, repetir, mudo)
    * e uma barra de progresso. A lógica de reprodução é controlada pela ViewModel passada como parâmetro.
    */
    // isMuted: Estado que indica se o áudio está mudo ou não.
    val isMuted by viewModel.mute.observeAsState(false)
    // isRepeating: Estado que indica se a reprodução está em modo de repetição.
    val repeat by viewModel.repeat.observeAsState(false)
    // isPaused: Estado que indica se a reprodução está pausada ou não.
    val isPaused by viewModel.pause.observeAsState(false)
    // audioPlayer: Objeto que representa o player de áudio.
    val audioPlayer by viewModel.soundPy.observeAsState(null)
    // palette: Objeto que contém as cores extraídas da imagem da música.
    val palette by viewModel.palette.observeAsState(null)
    // brush: Objeto que define o gradiente de fundo da tela.
    val brush by viewModel.brush.observeAsState(listOf())
    // currentPosition: Posição atual da reprodução da música.
    val currentPosition by viewModel.currentPosition.observeAsState(0)
    // currentMusic: Objeto que representa a música atualmente sendo reproduzida.
    val currentMusic by viewModel.music.observeAsState(null)

    Box {
        ImageComponent(
            linkThumb = currentMusic?.thumb ?: "",
            byteArray = currentMusic?.bitImage,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    brush = Brush.verticalGradient(colors = brush.map { color -> color.copy(alpha = 0.5f) })
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentMusic?.title ?: stringResource(id = R.string.carregando),
                    fontWeight = FontWeight.Bold,
                    color = ColorWhite,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.8).dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = currentMusic?.author ?: stringResource(id = R.string.carregando),
                    fontWeight = FontWeight.Medium,
                    color = ColorWhite.copy(0.5f),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.8).dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                IconButton(
                    onClick = { viewModel.setRepeat(!repeat) },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = ColorWhite)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.eva_shuffle_outline),
                        contentDescription = stringResource(R.string.modo_linear),
                        modifier = Modifier.size(20.dp),
                        alpha = if (!repeat) 1f else 0.5f
                    )
                }
                Row {
                    IconButton(
                        onClick = { viewModel.setMute(!isMuted) },
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.volume_1),
                            contentDescription = stringResource(R.string.deixar_musica_no_mudo),
                            modifier = Modifier.size(20.dp),
                            alpha = if (isMuted) 1f else 0.5f
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setRepeat(!repeat) },
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_round_repeat),
                            contentDescription = stringResource(R.string.reptir_somente_essa_musica),
                            modifier = Modifier.size(20.dp),
                            alpha = if (repeat) 1f else 0.5f
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    audioPlayer?.timeUse(currentPosition)?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            color = ColorWhite
                        )
                    }
                    audioPlayer?.timeUse(audioPlayer?.duration()?.toInt() ?: 0)?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            color = ColorWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                AudioProgressSlider(
                    viewModel = viewModel, thumbColor = Color(
                        palette?.getVibrantColor(Color.Black.hashCode()) ?: LineColor.hashCode(),
                    ), activeTrackColor = Color(
                        palette?.getVibrantColor(Color.Black.hashCode()) ?: LineColor.hashCode(),
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        @Suppress("DEPRECATION") audioPlayer?.player?.previous()
                    }, modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = rememberArrowCircleLeft(),
                        contentDescription = stringResource(R.string.ir_para_musica_passada),
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                IconButton(
                    onClick = {
                        if (!isPaused) {
                            audioPlayer?.player?.play()
                        } else {
                            audioPlayer?.player?.pause()
                        }
                        audioPlayer?.player?.isPlaying?.let { viewModel.setPause(it) }
                        viewModel.startTime()
                    }, modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        imageVector = if (isPaused) rememberMotionPhotosPaused() else rememberPlayCircle(),
                        contentDescription = if(isPaused) stringResource(R.string.pausa_musica) else stringResource(
                            R.string.play_na_musica
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                IconButton(modifier = Modifier.size(60.dp), onClick = {
                    @Suppress("DEPRECATION") audioPlayer?.player?.next()
                }) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = rememberArrowCircleRight(),
                        contentDescription = stringResource(R.string.proxima_musica),
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}