package com.mupy.soundpy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.mupy.soundpy.R
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.utils.Utils

@Composable
fun ImageComponent(
    linkThumb: String,
    byteArray: ByteArray?,
    modifier: Modifier,
    contentDescription: String = "",
    preview: Boolean = false,
    contentScale: ContentScale = ContentScale.Companion.Crop,
) {
    // Gradiente para a imagem padrão do artista
    val brush = Brush.verticalGradient(
        colors = listOf(
            ColorWhite.copy(0.5f),
            ColorWhite.copy(0.4f),
            ColorWhite.copy(0.3f),
            ColorWhite.copy(0.2f),
            ColorWhite.copy(0.1f)
        )
    )
    val utils = Utils()
    if (preview) Image(
        painter = painterResource(id = R.mipmap.deadpoll),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
    else if (byteArray == null && linkThumb.isNotBlank()) AsyncImage(
        model = linkThumb,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
    )
    else if(linkThumb.isBlank() && byteArray == null)
        Box(
            modifier = modifier.background(brush)
        )
    else utils.toBitmap(byteArray)?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}