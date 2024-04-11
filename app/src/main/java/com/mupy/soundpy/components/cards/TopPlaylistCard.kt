package com.mupy.soundpy.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mupy.soundpy.R
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.models.PlayListData
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.WhiteTransparent

/**
 * Composable que exibe um cartão de playlist com uma imagem de fundo e um botão de retorno.
 *
 * @param playlistData Os dados da playlist a serem exibidos.
 * @param onClick A ação a ser executada quando o botão de retorno for clicado.
 */
@Composable
fun TopPlaylistCard(playlistData: PlayListData?, onclick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        ImageComponent(
            contentScale = ContentScale.Crop,
            linkThumb = playlistData?.thumb ?: "",
            byteArray = null,
            contentDescription = stringResource(R.string.imagem_da_playlist),
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.75f)
                .height(300.dp)
                .background(WhiteTransparent)
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 9.dp)
                .height(300.dp)
        ) {
            IconButton(
                onClick = { onclick() },
                modifier = Modifier.clip(CircleShape),
                colors = IconButtonColors(
                    containerColor = Color.Black.copy(0.5f),
                    contentColor = ColorWhite,
                    disabledContainerColor = Color.Black.copy(0.3f),
                    disabledContentColor = ColorWhite.copy(0.5f)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = stringResource(R.string.volar_para_pagina_anterior),
                    tint = ColorWhite
                )
            }
            Text(
                text = playlistData?.title ?: "Loading...",
                fontWeight = FontWeight.W400,
                color = ColorWhite,
                fontSize = 30.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
