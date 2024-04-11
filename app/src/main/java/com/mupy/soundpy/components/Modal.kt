package com.mupy.soundpy.components

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.database.MyPlaylists
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.SoundPyTheme
import com.mupy.soundpy.ui.theme.TextColor2
import com.mupy.soundpy.ui.theme.WhiteTransparent

@Composable
fun Modal(
    viewModel: ContextMain,
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = WhiteTransparent,
        unfocusedContainerColor = WhiteTransparent,
        disabledContainerColor = WhiteTransparent,
        cursorColor = ColorWhite,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )

    val showModal by viewModel.showModal.observeAsState(false)
    var textInput by remember {
        mutableStateOf("")
    }

    fun done() {
        val playlist = MyPlaylists(thumb = null, name = textInput)
        viewModel.addPlaylist(playlist)
        viewModel.setShowModal(!showModal)
    }

    if (showModal) AlertDialog(containerColor = Color.Black.copy(0.8f), onDismissRequest = {
        viewModel.setShowModal(false)
    }, title = {
        Text("Criar playlist", color = ColorWhite)
    }, text = {
        TextField(
            value = textInput,
            onValueChange = {
                textInput = it.replace(Regex("[^A-Za-z0-9 ]"), "").replace(" ", "")
            },
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .absoluteOffset(y = 0.dp),
            colors = colors,
            shape = RoundedCornerShape(50.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.nome_da_playlist),
                    color = ColorWhite,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxHeight()
                )
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { }),
            textStyle = TextStyle(color = ColorWhite)
        )
    }, confirmButton = {
        Button(
            onClick = { done() }, colors = ButtonDefaults.buttonColors(containerColor = ColorWhite)
        ) {
            Text(text = "Confirmar", color = Color.Black)
        }
    })
}

/**
 * Exemplo de preview para a tela de playlists do usuário.
 */
@Preview(showBackground = true)
@Composable
fun ModalPreview() {
    SoundPyTheme {
        val context = LocalContext.current
        Modal(
            viewModel = ContextMain(context),
        )
    }
}