package com.mupy.soundpy

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import com.mupy.soundpy.components.adds.showInterstialAd
import com.mupy.soundpy.database.AppDatabase
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.database.MyPlaylists
import com.mupy.soundpy.database.PlaylistWithMusic
import com.mupy.soundpy.models.Menu
import com.mupy.soundpy.models.PlayListData
import com.mupy.soundpy.network.ApiViewModel
import com.mupy.soundpy.utils.SoundPy
import com.mupy.soundpy.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ContextMain(
    private val context: Context, private val dataBase: AppDatabase? = null
) : ApiViewModel() {
    private val utils = Utils()
    private var playlistImage: ByteArray? = null

    private val _currentPlaylist = MutableLiveData<PlaylistWithMusic?>(null)
    val currentPlaylist: LiveData<PlaylistWithMusic?> = _currentPlaylist

    private val _currentPosition = MutableLiveData(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private val _soundPy: MutableLiveData<SoundPy> = MutableLiveData(
        SoundPy(
            context, currentPlaylist.value ?: PlaylistWithMusic(
                playlist = MyPlaylists(null, context.getString(R.string.sem_nada)),
                mutableListOf()
            ), this
        )
    )
    val soundPy: LiveData<SoundPy> = _soundPy

    private val _palette = MutableLiveData<Palette?>(null)
    val palette: LiveData<Palette?> = _palette

    private val _brush = MutableLiveData<List<Color>>(listOf())
    val brush: LiveData<List<Color>> = _brush

    private val _pause = MutableLiveData(_soundPy.value?.player?.isPlaying == true)
    val pause: LiveData<Boolean> = _pause

    private val _mute = MutableLiveData(false)
    val mute: LiveData<Boolean> = _mute

    private val _menu = MutableLiveData(Menu(false) @Composable {})
    val menu: LiveData<Menu> = _menu

    private val _repeat = MutableLiveData(false)
    val repeat: LiveData<Boolean> = _repeat

    private val _progress = MutableLiveData(0f)
    val progress: LiveData<Float> = _progress

    // private val _stream = MutableLiveData(StreamYt(""))
    // val stream: LiveData<StreamYt> = _stream

    private val _showModal = MutableLiveData(false)
    val showModal: LiveData<Boolean> = _showModal

    private val _music = MutableLiveData<Music?>(null)
    val music: LiveData<Music?> = _music

    private val _saved = MutableLiveData(utils.getMusicsSaved(context))
    val saved: LiveData<List<Music>> = _saved

    private val _myPlaylists = MutableLiveData(arrayOf<PlaylistWithMusic>())
    val myPlaylists: LiveData<Array<PlaylistWithMusic>> = _myPlaylists

    private val _playlistsCards = MutableLiveData(
        arrayOf<PlayListData>()
    )
    val playlistsCards: LiveData<Array<PlayListData>> = _playlistsCards

    /**
     * Obtém as playlists relacionadas aos gêneros mais ouvidos no Brasil.
     * Atualiza [_playlistsCards] com o resultado.
     */
    fun getPlaylists() {
        viewModelScope.launch {
            try {
                // Chama o método do repositório para obter as playlists.
                val result =
                    repository.getPlaylists(context.getString(R.string.generos_mais_ouvidos_no_brasil))

                // Se o resultado não estiver vazio, atualiza o valor de _playlistsCards.
                if (result.isNotEmpty()) {
                    _playlistsCards.value = result
                }
            } catch (exception: Exception) {
                Log.e("CoroutineException", "Erro na corrotina", exception)
            }
        }
    }

    /**
     * Define a imagem da playlist.
     *
     * @param image Um array de bytes representando a imagem.
     */
    fun setPlaylistImage(image: ByteArray?) {
        playlistImage = image
    }

    /**
     * Define a música atual e atualiza a paleta de cores com base na imagem da capa, se disponível.
     *
     * @param music A música a ser definida como atual.
     */
    fun setMusic(music: Music?) {
        _music.value = music
        music?.bitImage?.let { byteImage ->
            utils.toBitmap(byteImage)?.let { bitmap ->
                val palette = Palette.from(bitmap).generate()
                _palette.value = palette
                _brush.value = listOf(
                    Color(palette.getDarkVibrantColor(Color.Black.hashCode())),
                    Color(palette.getVibrantColor(Color.Black.hashCode())),
                    Color(palette.getDominantColor(Color.Black.hashCode())),
                    Color(palette.getLightMutedColor(Color.Black.hashCode())),
                )
            }
        }
    }

    /**
     * Define se o áudio está mudo ou não e ajusta o volume do reprodutor de áudio.
     *
     * @param isMute True se o áudio estiver mudo, false caso contrário.
     */
    fun setMute(isMute: Boolean) {
        _mute.value = isMute
        _soundPy.value?.setVolume(if (!isMute) 1f else 0f)
    }

    /**
     * Define se a reprodução da música deve ser repetida ou não.
     *
     * @param isRepeat True se a reprodução deve ser repetida, false caso contrário.
     */
    fun setRepeat(isRepeat: Boolean) {
        _repeat.value = isRepeat
        _soundPy.value?.reapt(isRepeat)
    }

    /**
     * Define o menu atual.
     *
     * @param menu O menu a ser definido como atual.
     */
    fun setMenu(menu: Menu) {
        _menu.value = menu
    }

    /**
     * Define se o modal deve ser exibido ou não.
     *
     * @param isShown True se o modal deve ser exibido, false caso contrário.
     */
    fun setShowModal(isShown: Boolean) {
        _showModal.value = isShown
    }

    /**
     * Define a posição atual da reprodução da música.
     *
     * @param position A posição atual da reprodução da música.
     */
    fun setCurrentPosition(position: Int) {
        _currentPosition.value = position
    }

    /**
     * Atualiza a lista de músicas salvas no estado do aplicativo.
     */
    private fun updateSavedMusicList() {
        _saved.value = utils.getMusicsSaved(context)
    }

    /**
     * Inicia o monitoramento do tempo de reprodução da música.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun startTime() {
        viewModelScope.launch {
            _soundPy.value?.let { soundPy ->
                // Obtém a duração total da música.
                val duration = soundPy.duration().toFloat()

                // Enquanto a música estiver pausada, atualiza a posição de reprodução.
                while (_pause.value == true) {
                    val currentPosition = soundPy.currentPosition().toFloat()
                    _currentPosition.value = currentPosition.toInt()

                    // Calcula o progresso atual da música.
                    val progressNow = currentPosition / duration

                    // Atualiza o progresso da música, se necessário.
                    if (progressNow > (_progress.value ?: 0f)) {
                        setProgress(progressNow)
                    }

                    // Aguarda 1 segundo antes de verificar novamente.
                    delay(1000)
                }
            }
            // Cancela a coroutine após a execução.
            this.cancel()
        }
    }

    /**
     * Remove uma playlist da lista de playlists do usuário.
     *
     * @param playlist A playlist a ser removida.
     */
    fun removePlaylist(playlist: PlaylistWithMusic) {
        viewModelScope.launch {
            // Filtra a lista de playlists para remover a playlist especificada.
            _myPlaylists.value =
                _myPlaylists.value?.filter { it.playlist.name != playlist.playlist.name }
                    ?.toTypedArray()

            // Deleta a playlist do banco de dados.
            dataBase?.service()?.delete(playlist.playlist)

            // Cancela a coroutine após a execução.
            this.cancel()
        }
    }

    /**
     * Adiciona uma nova playlist à lista de playlists do usuário.
     *
     * @param playlist A playlist a ser adicionada.
     */
    fun addPlaylist(playlist: MyPlaylists) {
        viewModelScope.launch {
            // Cria a nova playlist no banco de dados.
            dataBase?.service()?.createMyPlaylist(playlist.thumb, playlist.name!!)

            // Atualiza a lista de playlists após a adição da nova playlist.
            recoilPlaylists()

            // Cancela a coroutine após a execução.
            this.cancel()
        }
    }

    /**
     * Define novo valor [progress]
     *
     * @param float novo valor do estado
     **/
    fun setProgress(float: Float) {
        _progress.value = float
    }

    /**
     * Define novo valor do [pause]
     *
     * @param boolean atualiza o valo do pause
     **/
    fun setPause(boolean: Boolean) {
        _pause.value = boolean
    }

    /**
     * Salva musica em banco de dados caso não exista
     *
     * @param music musica a ser salva
     * @return [Boolean] se existe no banco ou não
     **/
    private suspend fun saveMusicInData(music: Music): Boolean =
        withContext((viewModelScope.coroutineContext)) {
            val existsInBank = dataBase?.service()?.getMusic(music.playlistId!!, music.url)
            if (existsInBank == false) {
                dataBase?.service()?.createMusic(music)
                _soundPy.value?.addMusic(music)
            }
            existsInBank == false
        }

    /**
     * Realiza o download de uma música e a adiciona à playlist especificada, atualizando o estado do reprodutor de áudio.
     *
     * @param music A música a ser baixada.
     * @param myPlaylists A playlist à qual a música será adicionada.
     * @param callBack Uma função de retorno a ser chamada após o download da música.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun download(music: Music, myPlaylists: PlaylistWithMusic, callBack: () -> Unit) {
        viewModelScope.launch {
            try {
                // Realiza o download da música.
                val result = repository.download(music.url)

                // Cria um arquivo com os dados baixados e o salva no armazenamento local.
                val downloadedMusic = utils.createFile(music.title, result.bytes(), context)
                    .copy(playlistId = myPlaylists.playlist.uid)

                // Salva as informações do arquivo baixado no banco de dados.
                saveMusicInData(downloadedMusic)

                // Adiciona a música ao reprodutor de áudio.
                _soundPy.value?.addMusic(downloadedMusic)

                // Exibe uma mensagem indicando que a música foi baixada com sucesso.

                Toast.makeText(
                    context, context.getString(R.string.musica_baixada), Toast.LENGTH_SHORT
                ).show()
            } catch (error: Exception) {
                // Registra qualquer exceção ocorrida durante o processo e exibe um Toast de erro.
                Log.e("Error", error.toString())
                Toast.makeText(
                    context, "Erro ao baixar a música", Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Chama a função de retorno.
                callBack()
                // Atualiza a lista de arquivos após o download da música.
                getFiles()
                showInterstialAd(context)
            }
        }
    }

    /**
     * Deleta uma música do banco de dados.
     *
     * @param music A música a ser deletada.
     * @return True se a música existir no banco de dados após a deleção, false caso contrário.
     */
    private suspend fun deleteMusicInDb(music: Music): Boolean {
        return withContext(Dispatchers.IO) {
            // Deleta a música do banco de dados.
            dataBase?.service()?.deleteMusic(music.url, music.playlistId)

            // Verifica se a música ainda existe no banco de dados após a deleção.
            dataBase?.service()?.existMusic(music.url) == true
        }
    }

    /**
     * Executa uma rotina de verificação de músicas salvas e remove aquelas que não estão mais disponíveis no armazenamento.
     */
    private fun routineMusic() {
        viewModelScope.launch {
            // Obtém todas as músicas salvas.
            val savedMusics = _saved.value ?: listOf()

            // Obtém todas as músicas do banco de dados.
            val musicsInDb = dataBase?.service()?.gelAllMusics() ?: listOf()

            // Itera sobre as músicas salvas para verificar se ainda estão presentes no armazenamento.
            for (music in savedMusics) {
                var contains = false
                for (dbMusic in musicsInDb) {
                    if (dbMusic.url == music.url) {
                        contains = true
                        break
                    }
                }
                // Se a música não estiver mais presente no armazenamento, remove-a.
                if (!contains) {
                    utils.deleteMusic(music)
                }
            }

            // Atualiza o estado de músicas salvas após a remoção das músicas ausentes.
            updateSavedMusicList()

            // Cancela a coroutine após a execução.
            cancel()
        }
    }

    /**
     * Deleta uma música do armazenamento local e do banco de dados, e atualiza o estado do reprodutor de áudio.
     *
     * @param music A música a ser deletada.
     * @param callBack Uma função de retorno a ser chamada após a deleção da música.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun deleteMusic(music: Music, callBack: () -> Unit) {
        viewModelScope.launch {
            try {
                // Deleta a música do banco de dados.
                val exist = deleteMusicInDb(music)

                // Se a música não existir no banco de dados, deleta o arquivo do armazenamento local.
                if (!exist) {
                    withContext(Dispatchers.IO) {
                        val file = File(music.directory)
                        if (file.exists()) file.delete()
                    }
                }
                if (_soundPy.value?.playlist?.playlist?.uid == _currentPlaylist.value?.playlist?.uid)
                    _soundPy.value?.removedMusic(music)
                // Exibe uma mensagem indicando que a música foi removida com sucesso.
                Toast.makeText(
                    context, context.getString(R.string.musica_removida), Toast.LENGTH_SHORT
                ).show()
                // chama função de remoção

            } catch (err: Exception) {
                // Registra qualquer exceção ocorrida durante o processo.
                Log.e("Error", "$err")
            } finally {
                // Chama a função de retorno.
                callBack()

                // Cancela a coroutine após a execução.
                this.cancel()
            }
        }
    }

    /**
     * Recarrega as playlists salvas do banco de dados e atualiza os estados [_myPlaylists] e [_currentPlaylist].
     */
    fun recoilPlaylists() {
        viewModelScope.launch {
            // Recupera as playlists do banco de dados de forma assíncrona no contexto IO.
            val playlists = withContext(Dispatchers.IO) {
                dataBase?.service()?.getAllPlaylistsWithMusic()?.toList() ?: emptyList()
            }

            // Atualiza o estado _myPlaylists com as playlists recuperadas.
            _myPlaylists.value = playlists.toTypedArray()

            // Define a playlist atual como a primeira playlist com o nome correto, se existir.
            val currentPlaylistName = context.getString(R.string.minhas_musicas)
            val currentPlaylist = playlists.firstOrNull { it.playlist.name == currentPlaylistName }
            _currentPlaylist.value = currentPlaylist
        }
    }

    /**
     * Obtém as playlists salvas, atualiza o estado [_myPlaylists] e [_currentPlaylist], e executa outras operações relacionadas ao carregamento de arquivos.
     */
    private fun getFiles() {
        viewModelScope.launch {
            try {
                // Recupera as playlists do banco de dados.
                recoilPlaylists()

                // atualiza o estado [saved] de músicas salvas.
                updateSavedMusicList()

                // Obtém todas as playlists com músicas do banco de dados.
                val playlists =
                    dataBase?.service()?.getAllPlaylistsWithMusic()?.toTypedArray() ?: arrayOf()

                // Atualiza o estado das playlists.
                _myPlaylists.value = playlists

                // Define a playlist atual como a que estava sendo reproduzida anteriormente.
                _currentPlaylist.value =
                    playlists.find { it.playlist.uid == _currentPlaylist.value?.playlist?.uid }
            } catch (err: Exception) {
                // Registra qualquer exceção ocorrida durante o processo.
                Log.e("Error", err.message.toString())
            } finally {
                // Cancela a coroutine após a execução.
                this.cancel()
            }
        }
    }

    /**
     * Configura o reprodutor de áudio [_soundPy] com base na playlist atual [_currentPlaylist].
     * @return Uma [Job] que representa a execução desta operação assíncrona.
     */
    private fun setSoundPy(): Job = viewModelScope.launch {
        // Cria um novo objeto SoundPy com base na playlist atual, ou em uma playlist vazia se não houver nenhuma.
        val defaultPlaylist = PlaylistWithMusic(
            playlist = MyPlaylists(null, context.getString(R.string.sem_nada)),
            musicList = mutableListOf()
        )
        val playlistToUse = currentPlaylist.value ?: defaultPlaylist

        // Configura o valor de _soundPy com um novo objeto SoundPy.
        _soundPy.value = SoundPy(context, playlistToUse, this@ContextMain)
    }

    /**
     * Obtém as playlists salvas e executa as ações necessárias ao carregar as informações.
     */
    fun getFilesSaved() {
        viewModelScope.launch {
            // Obtém todas as playlists com músicas do banco de dados.
            val playlists = dataBase?.service()?.getAllPlaylistsWithMusic()?.toTypedArray()

            // Se houver playlists salvas, atualiza as variáveis de estado correspondentes.
            if (playlists?.isNotEmpty() == true) {
                _myPlaylists.value = playlists
                _currentPlaylist.value = playlists[0]
            }

            // Configura o estado do reprodutor de áudio.
            setSoundPy()

            // atualiza as músicas salvas no estado do aplicativo.
            updateSavedMusicList()

            // Executa a rotina de seleção e reprodução de músicas.
            routineMusic()

            // Cancela a coroutine após a execução.
            this.cancel()
        }
    }

    /**
     * Inicia o streaming de uma música, obtendo a imagem da capa da música e realizando o stream de áudio.
     *
     * @param music A música a ser transmitida.
     */
    fun stream(music: Music) {
        viewModelScope.launch {
            try {
                // Pausa a reprodução atual
                _soundPy.value?.player?.pause()
                // Define a música atual.
                setMusic(music)

                // Obtém a URL de streaming da música.
                val streamResult = repository.stream(music.url)

                // Obtém a imagem da capa da música e a comprime em um array de bytes.
                utils.getImg(music, context) { bitmap ->
                    music.bitImage = utils.compressBitmapToByteArray(bitmap)

                    // Inicia o stream de áudio da música.
                    _soundPy.value?.stream(Uri.parse(streamResult.result), music)
                }

                // Imprime informações da música no console.
                println(music)
            } catch (error: Exception) {
                // Registra e exibe um erro ao fazer streaming da música.
                Log.d("Error", "$error")
                Toast.makeText(
                    context, "Erro ao fazer streaming da música", Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Cancela a coroutine após a conclusão.
                showInterstialAd(context)
            }
        }
    }

    fun downloadFile(music: Music) {
        viewModelScope.launch {
            try {
                val stream_ = repository.stream(music.url)
                val internalDir = context.filesDir
                val musicDirectory = File(internalDir, "saved").apply { mkdirs() }
                val outputFile = File(musicDirectory, "${music.title}.mp3")

                val u = URL(stream_.result)
                val conn = u.openConnection()
                val contentLength = conn.contentLength

                if (contentLength > 0) {
                    val input = u.openStream()
                    val output = FileOutputStream(outputFile)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }

                    output.flush()
                    output.close()
                    input.close()

                    Toast.makeText(
                        context, "Download bem-sucedido", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context, "O tamanho do arquivo é inválido", Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context, "Ocorreu um erro durante o download: $e", Toast.LENGTH_SHORT
                ).show()
                Log.e("Error", "Ocorreu um erro durante o download", e)
            }
        }
    }

    /**
     * Move a playlist para reprodução, iniciando a reprodução da primeira música, se houver.
     *
     * @param playlistWithMusic A playlist com músicas a serem movidas para reprodução.
     */
    fun movePlaylist(playlistWithMusic: PlaylistWithMusic?) {
        val lastPlaylist = _myPlaylists.value?.lastOrNull()

        lastPlaylist?.let { playlist ->
            _soundPy.value?.releasePlayerAndNotification()
            _soundPy.value = SoundPy(context, playlistWithMusic ?: playlist, this)
            _soundPy.value?.player?.play()
        }
    }

    /**
     * Navega para uma rota específica no [NavHostController], se a rota atual for diferente da rota desejada.
     *
     * @param navHostController O [NavHostController] que controla a navegação.
     * @param routeId O identificador da rota para a qual deseja navegar.
     */
    fun navigate(navHostController: NavHostController, routeId: String) {
        // Obtém o identificador da rota atualmente em exibição.
        val currentRoute = navHostController.currentBackStackEntry?.id

        // Navega para a nova rota apenas se ela for diferente da rota atual.
        if (currentRoute != routeId) {
            navHostController.navigate(routeId)
        }
    }
}