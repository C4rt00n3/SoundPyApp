package com.mupy.soundpy.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.ui.theme.LineColor
import com.mupy.soundpy.ui.theme.TrackColor
import com.mupy.soundpy.utils.SoundPy

/**
 * Composable que exibe um slider para mostrar e alterar o progresso da reprodução de áudio.
 *
 * @param viewModel O ViewModel que contém o estado do player de áudio.
 * @param thumbColor A cor do indicador (thumb) do slider.
 * @param activeTrackColor A cor da pista (track) do slider quando o thumb está arrastado.
 * @param inactiveTrackColor A cor da pista (track) do slider quando o thumb não está arrastado.
 * @param modifier O modificador que será aplicado ao slider.
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AudioProgressSlider(
    viewModel: ContextMain,
    thumbColor: Color = LineColor,
    activeTrackColor: Color = LineColor,
    inactiveTrackColor: Color = TrackColor,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val soundPlayer: SoundPy? by viewModel.soundPy.observeAsState(null)
    val progress: Float by viewModel.progress.observeAsState(0f)

    Slider(
        value = progress,
        onValueChange = { newProgress ->
            viewModel.setProgress(newProgress)
            val duration = soundPlayer?.duration() ?: 0
            val newPosition = (duration * newProgress).toLong()

            if (duration > 0 && newPosition >= 0 && newPosition <= duration) {
                soundPlayer?.player?.seekTo(newPosition)
            }
        },
        valueRange = 0f..1f,
        steps = 100,
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth().height(8.dp)
    )
}
