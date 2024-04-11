package com.mupy.soundpy.components.menus

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.database.MyPlaylists
import com.mupy.soundpy.database.PlaylistWithMusic
import com.mupy.soundpy.models.Menu
import com.mupy.soundpy.utils.SoundPy

/**
 * Componente que exibe um menu de opções para interagir com uma música, incluindo baixar, excluir, adicionar a uma playlist, etc.
 *
 * @param music A música para a qual exibir o menu.
 * @param navController O controlador de navegação para realizar a navegação entre as telas.
 * @param context O contexto do aplicativo.
 * @param viewModel O ViewModel principal do aplicativo.
 * @param remove Uma função para remover a música.
 */
@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MenuGenericCard(
    music: Music,
    navController: NavHostController?,
    context: Context,
    viewModel: ContextMain,
    remove: (Music) -> Unit
) {
    val saved by viewModel.saved.observeAsState(listOf())
    val myPlaylist by viewModel.myPlaylists.observeAsState(arrayOf())
    val player by viewModel.soundPy.observeAsState(
        SoundPy(
            LocalContext.current,
            PlaylistWithMusic(playlist = MyPlaylists(null, ""), musicList = mutableListOf()),
            viewModel
        )
    )
    var isSaved by remember { mutableStateOf(saved.find { it.url == music.url } == null) }
    var expand by remember { mutableStateOf(false) }

    val permissionStateRead =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val permissionStateWrite =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    Option(
        load = true,
        onClick = {
            checkPermissions(permissionStateRead)
            checkPermissions(permissionStateWrite)
            if (isSaved) {
                viewModel.download(music, myPlaylist[0]) {
                    isSaved = !isSaved
                    it()
                }
            } else {
                val callback = {
                    player.player.stop()
                    viewModel.deleteMusic(music) { it() }
                    viewModel.setMenu(Menu(false) @Composable {})
                }

                val index = myPlaylist[0].musicList.reversed().indexOf(music)
                val currentMediaItemIndex = player.player.currentMediaItemIndex
                if (index == currentMediaItemIndex) player.player.stop()
                callback()
                remove(music)
            }
        },
        text = if (isSaved) stringResource(id = R.string.baixar) else stringResource(id = R.string.delete),
        contentDescription = if (isSaved) stringResource(id = R.string.baixar_a_musica) + music.title else stringResource(
            id = R.string.remover_musica_da_playlist
        ) + music.title,
        painter = painterResource(id = if (isSaved) R.drawable.save_down else R.drawable.delete),
        expand = null,
    )
    Option(
        onClick = {
            music.bitImage?.let {
                viewModel.setPlaylistImage(it)
            }
            viewModel.setShowModal(true)
        },
        text = stringResource(R.string.criar_playlist),
        contentDescription = stringResource(R.string.abri_modal_de_crian_o_de_playlist),
        painter = painterResource(id = R.drawable.add),
        expand = null,
    )
    Option(
        onClick = {
            expand = !expand
        },
        text = stringResource(R.string.adicionar_em_uma_playlist),
        contentDescription = stringResource(R.string.adicionar_em_uma_playlist),
        painter = painterResource(id = R.drawable.list),
    ) {
        SubOption(
            expand = expand,
            context = context,
            myPlaylist = myPlaylist,
            navController = navController,
            onClickRemove = {
                viewModel.removePlaylist(it)
            },
            onClick = {
                viewModel.download(music, PlaylistWithMusic(it, mutableListOf())) {}
            },
            viewModel = viewModel,
            enableDownload = true
        )
    }
}

/**
 * Verifica se as permissões necessárias foram concedidas e solicita permissão caso contrário.
 *
 * @param permissionState O estado da permissão a ser verificado e solicitado.
 * @return True se a permissão foi concedida, false caso contrário.
 */
@OptIn(ExperimentalPermissionsApi::class)
private fun checkPermissions(permissionState: PermissionState): Boolean {
    val isGranted = permissionState.status.isGranted
    if (!isGranted) permissionState.launchPermissionRequest()
    return isGranted
}
