package com.mupy.soundpy.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.components.ImageComponent
import com.mupy.soundpy.components.Title
import com.mupy.soundpy.components.adds.BannersAds
import com.mupy.soundpy.ui.theme.ColorWhite

@Composable
fun MyPlaylists(
    viewModel: ContextMain, context: Context, navHostController: NavHostController
) {
    val myPlaylists by viewModel.myPlaylists.observeAsState(arrayOf())
    val width = LocalConfiguration.current.screenWidthDp - 16

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Title(text = stringResource(id = R.string.minhas_playlists))
        LazyColumn {
            items(myPlaylists) {
                Button(
                    onClick = { /*TODO*/ },
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            ImageComponent(
                                linkThumb = "",
                                byteArray = it.musicList.lastOrNull()?.bitImage,
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = it.musicList.lastOrNull()?.title ?: "",
                                color = ColorWhite,
                                modifier = Modifier.widthIn(0.dp, (width - 30).dp),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (it.playlist.uid != 0) Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = context.getString(
                                R.string.deleta_playlist
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth())
            }
        }
    }
}