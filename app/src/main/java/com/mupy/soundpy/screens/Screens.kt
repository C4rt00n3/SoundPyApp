package com.mupy.soundpy.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.components.Header
import com.mupy.soundpy.components.Modal
import com.mupy.soundpy.components.Permission
import com.mupy.soundpy.components.Player
import com.mupy.soundpy.components.SnackBarHost
import com.mupy.soundpy.components.menus.BarMenu
import com.mupy.soundpy.ui.theme.WhiteTransparent

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Screens(
    navController: NavHostController, context: Context, viewModel: ContextMain
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val palette by viewModel.palette.observeAsState(null)
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    // A surface container using the 'background' color from the theme
    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = if (currentRoute != "music") Color.Black else Color(
            palette?.getDarkVibrantColor(
                Color.Black.hashCode()
            ) ?: 0
        )
    )

    println(currentRoute)

    Scaffold(
        topBar = {
            if (currentRoute != "open_playlists/{uid}" && currentRoute != "playlist/{url}") Box {
                Header(
                    viewModel = viewModel,
                    context,
                    navController,
                )
            }
        },
        containerColor = Color.Black,
        contentColor = Color.Black,
        snackbarHost = {
            SnackBarHost(
                viewModel = viewModel,
                color = if (currentRoute == "music") Color.Black.copy(0.5f) else Color.Black
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(if (currentRoute == "music") Color.Transparent else Color.Black)
                    .padding(horizontal = 9.dp)
            ) {
                Player(viewModel = viewModel, navController = navController)
                BarMenu(
                    viewModel = viewModel, navController
                )
            }
        },
    ) { padding ->
        Modal(viewModel = viewModel)
        NavHost(
            modifier = Modifier
                .background(Color.Black)
                .padding(
                    if (currentRoute != "playlist" && currentRoute != "open_playlists/{uid}" && currentRoute != "music") padding else PaddingValues(
                        0.dp
                    )
                ), navController = navController, startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(viewModel, navController)
            }
            composable("music") {
                MusicScreen(viewModel)
            }
            composable("playlist/{url}") {
                PlaylistScreen(
                    viewModel = viewModel,
                    navHostController = navController,
                    context = context,
                    backStackEntry = it
                )
            }
            composable("search") {
                SearchScreen(
                    viewModel = viewModel, navHostController = navController, context = context
                )
            }
            composable("favorites") {
                MyMusicsScreen(
                    context = context, navHostController = navController, viewModel = viewModel
                )
            }
            composable("open_playlists/{uid}") { backStackEntry ->
                PlaylistDetailScreen(
                    viewModel = viewModel,
                    context = context,
                    navHostController = navController,
                    backStackEntry = backStackEntry
                )
            }
        }
        Permission(
            viewModel = viewModel, context = context
        )
    }
}