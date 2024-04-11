@file:OptIn(ExperimentalMaterial3Api::class)

package com.mupy.soundpy.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.ui.theme.ColorWhite
import com.mupy.soundpy.ui.theme.WhiteTransparent

/**
 * Composable que exibe um campo de busca para pesquisar músicas.
 *
 * @param viewModel O ViewModel que contém a lógica de busca.
 * @param navController O NavController para navegação.
 */
@Composable
fun SearchInput(
    viewModel: ContextMain, navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = WhiteTransparent,
        unfocusedContainerColor = WhiteTransparent,
        disabledContainerColor = WhiteTransparent,
        cursorColor = ColorWhite,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )

    /**
     * Realiza a busca no YouTube com base na query de pesquisa atual.
     */
    fun performSearch() {
        if (searchQuery.isNotBlank()) {
            viewModel.searchYoutube(searchQuery)
        }
    }

    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        textStyle = TextStyle(color = ColorWhite),
        colors = colors,
        shape = RoundedCornerShape(30.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.perquisar),
                color = ColorWhite.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            // Limpa a barra de pesquisa
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { searchQuery = "" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpar barra",
                        tint = ColorWhite
                    )
                }
            }
        },
        trailingIcon = {
            // Butão de inici a pesquisa
            IconButton(
                onClick = { performSearch() }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.perquisar),
                    tint = ColorWhite
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { performSearch() }),
    )
}
