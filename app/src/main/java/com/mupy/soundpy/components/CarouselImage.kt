package com.mupy.soundpy.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.cards.CardNewRelease
import com.mupy.soundpy.models.PlayListData

/**
 * Composable que exibe um carrossel de imagens de playlists.
 *
 * @param list A lista de playlists a serem exibidas no carrossel.
 * @param navHostController O controlador de navegação para navegar para detalhes da playlist.
 * @param viewModel O view model usado para interagir com os dados das playlists.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselImage(
    playlists: Array<PlayListData>, navHostController: NavHostController, viewModel: ContextMain
) {
    val mocArray = arrayOf(
        PlayListData(
            "https://lh3.googleusercontent.com/18eW5KsqS0J0Q9pX3y6KqrfrsJB5-U_LMbEJ_s2SwGSeCm2Th_XMebciVMg8Upg372kTotCy8QSh6T4h=w544-h544-l90-rj",
            "RDCLAK5uy_k5faEHND0iXJljeASESqJ3A8UtRr2FL00",
            "Topnejo",
            ""
        ), PlayListData(
            "https://yt3.googleusercontent.com/mhawOLp1YtaUXhAyQnvGIqNWP9oQ9Ry7QaVXEd_ymnTAC4eZ0pVHOIX0HD5ZZuW6mj1r--rFqWNQ=s576",
            "PLNyUJbuBiyw0SmnPYy4QfkDnpgq85fJBl",
            "TRAP BRASIL 2023",
            ""
        ), PlayListData(
            "https://lh3.googleusercontent.com/Nbv9b6PTaELOo14LJO90khFgsXf62QStHsJtpaV1P0yLJwcskFCaONFLdaXGQYH7e5-7iMfhsx4tIQ=w544-h544-l90-rj",
            "RDCLAK5uy_nRxkdjoJYfKXKh4HyRtpuHKfmIfSH2khY",
            "Funk de Natal",
            ""
        ), PlayListData(
            "https://lh3.googleusercontent.com/w8QDcpITg-64iylxia0Z4oWzbmlkHdSeSNyGslc_0ZcJgCtgLHkhugunsDRh_t87UQadn_si6-gPpvI=w544-h544-l90-rj",
            "RDCLAK5uy_nZiG9ehz_MQoWQxY5yElsLHCcG0tv9PRg",
            "Os maiores sucessos do rock clássico",
            ""
        ), PlayListData(
            "https://lh3.googleusercontent.com/ih5QdpqpkVNRXtD-joBWj3jo1woxAXJFyAoA3hWYNWAKX0M9B825HH2VOh7aDX-unf67oyCyJGN9TljR=w544-h544-l90-rj",
            "RDCLAK5uy_nmS3YoxSwVVQk9lEQJ0UX4ZCjXsW_psU8",
            "Pop's Biggest Hits",
            ""
        )
    )
    val pagerState = rememberPagerState(0, 0.05f) { if(playlists.isEmpty()) mocArray.size else playlists.size }

    Title(text = stringResource(R.string.new_release))

    if (playlists.isNotEmpty()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            PlaylistCard(if(playlists.isEmpty()) mocArray[page] else playlists[page], viewModel, navHostController)
        }
    }
}

/**
 * Composable para exibir um card de playlist.
 *
 * @param playlist [PlayListData] contendo os dados da playlist a ser exibida no card.
 * @param viewModel O view model usado para interagir com os dados das playlists.
 * @param navHostController O controlador de navegação para navegar para detalhes da playlist.
 */
@Composable
private fun PlaylistCard(
    playlist: PlayListData, viewModel: ContextMain, navHostController: NavHostController
) {
    Card(
        colors = CardDefaults.cardColors(Color.Transparent),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CardNewRelease(playlist, viewModel, navHostController)
    }
}