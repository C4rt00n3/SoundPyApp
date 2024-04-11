package com.mupy.soundpy.utils

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.mupy.soundpy.ContextMain
import com.mupy.soundpy.R
import com.mupy.soundpy.database.Music
import com.mupy.soundpy.database.PlaylistWithMusic
import java.io.File

/**
 * Classe responsável pela reprodução de músicas.
 *
 * @property context O contexto da aplicação.
 * @property playlist A playlist atual.
 * @property viewModel O ViewModel associado à classe.
 */
class SoundPy(
    private val context: Context,
    var playlist: PlaylistWithMusic,
    private val viewModel: ContextMain,
    private var playerNotificationManager: PlayerNotificationManager = PlayerNotificationManager.Builder(
        context, 151, "soundPy_notification"
    ).setChannelImportance(NotificationManager.IMPORTANCE_MAX)
        .setChannelNameResourceId(R.string.app_name).build(),
) {
    private var order = "DESC"

    // private val utils = Utils()
    private var cache = mutableListOf<MediaItem>()
    val player = ExoPlayer.Builder(context).setHandleAudioBecomingNoisy(true).build()
    private val listener = object : Player.Listener {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            player.pause()
            viewModel.setProgress(0f)
            viewModel.setCurrentPosition(0)
            if (!player.isPlaying) player.play()
            viewModel.setMusic(getMetadata(mediaItem?.mediaMetadata))
        }

        @RequiresApi(Build.VERSION_CODES.P)
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            viewModel.setPause(isPlaying)
            if (isPlaying) {
                getMetadata().let {
                    viewModel.setMusic(it)
                }
                viewModel.startTime()
            }
            viewModel.setPause(isPlaying)
        }
    }

    init {
        initializePlayer(playlist)
    }

    /**
     * Pega duração da faixa atual
     **/
    fun duration(): Long = player.duration

    /**
     * Pega posição em que está a duração da faixa atual
     **/
    fun currentPosition(): Long = player.currentPosition

    /**
     * Organiza a lista de reprodução de acordo com a [order].
     **/
    private fun orderBy() {
        if (order == "DESC") {
            cache = cache.reversed().toMutableList()
            playlist = playlist.copy(musicList = playlist.musicList.reversed().toMutableList())
        } else if (order != "ASC") {
            cache = cache.shuffled().toMutableList()
            playlist = playlist.copy(musicList = playlist.musicList.shuffled().toMutableList())
        }
    }

    /**
     * Inicializa o player de áudio com a playlist especificada.
     *
     * @param playlist A playlist a ser reproduzida.
     */
    private fun initializePlayer(playlistP: PlaylistWithMusic) {
        loadListMediaItems(playlistP)
        orderBy()
        player.apply {
            addMediaItems(cache)
            prepare()
            addListener(listener)
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
        }
        if (cache.isNotEmpty()) showNotification()
        if (playlist.musicList.isNotEmpty()) getMetadata().let { viewModel.setMusic(it) }
    }

    /**
     * Renderiza a notificação do player.
     **/
    private fun showNotification() {
        playerNotificationManager.setPlayer(null)
        val mediaSession = MediaSessionCompat(context, "Music")
        mediaSession.isActive = true
        playerNotificationManager.apply {
            setColorized(true)
            setMediaSessionToken(mediaSession.sessionToken)
            setUseChronometer(true)
            setSmallIcon(R.mipmap.ic_launcher)
            setUseRewindActionInCompactView(true)
            setUseRewindAction(true)
            setUsePlayPauseActions(true)
            setUsePreviousActionInCompactView(true)
            setUseNextActionInCompactView(true)
            setUsePreviousActionInCompactView(true)
            setUseFastForwardAction(true)
            setUseRewindAction(true)
            setSmallIcon(R.mipmap.soud_py_icon)
            setPlayer(player)
        }
    }

    /**
     * Carrega a lista de musicas no player.
     * @param playlist playlist a ser reproduzida.
     **/
    private fun loadListMediaItems(playlist: PlaylistWithMusic) {
        // Pega as musicas
        val music = playlist.musicList

        music.apply {
            mapIndexed { int, music ->
                // adiciona ao [cache]
                cache.add(
                    // Converte uma instância de música em um item de mídia.
                    convertMusicToMediaItem(
                        Uri.parse(
                            music.directory
                        ), music
                    )
                )
            }
        }
    }

    /**
     * Converte uma instância de música em um item de mídia.
     *
     * @param uri O URI da música.
     * @param music A instância de música a ser convertida.
     * @return O item de mídia convertido.
     */
    private fun convertMusicToMediaItem(uri: Uri, music: Music): MediaItem {
        val metadata = createMetadata(music)
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(metadata)
            .build()
    }

    /**
     * Cria os metadados de mídia com base em uma instância de música.
     *
     * @param music A instância de música para a qual os metadados devem ser criados.
     * @return Os metadados de mídia criados.
     */
    private fun createMetadata(music: Music): MediaMetadata {
        return setMetaData(music)
    }

    /**
     * Define os metadados de uma música em um objeto MediaMetadata.
     *
     * @param music A música da qual os metadados serão extraídos.
     * @return Um objeto MediaMetadata contendo os metadados da música.
     */
    private fun setMetaData(music: Music): MediaMetadata = MediaMetadata.Builder().apply {
        setAlbumArtist(music.author)
        setAlbumTitle(music.title)
        setTitle(music.title)
        setArtist(music.title)
        setSubtitle(music.name)
        music.bitImage?.let {
            setArtworkData(it)
        }
        setComposer(music.id)
        setArtworkUri(Uri.parse(music.thumb))
        setDescription(music.title)
    }.build()

    /**
     * Adiciona uma música à lista de reprodução.
     *
     * @param music A música a ser adicionada.
     */
    fun addMusic(music: Music) {
        val directory = File(music.directory)

        // Verifica se o diretório da música existe ou está vazio
        if (!directory.exists() && music.directory.isBlank()) return

        // Converte a música em um objeto MediaItem
        val mediaItem = convertMusicToMediaItem(Uri.parse(music.directory), music)

        // Adiciona o MediaItem à lista de cache e ao player
        if (order == "ASC") {
            cache.add(mediaItem)
            player.addMediaItem(mediaItem)
        } else if (order == "DESC") {
            cache.add(0, mediaItem)
            player.addMediaItem(0, mediaItem)
        } else {
            val disorder = (0 until cache.size).random()
            cache.add(disorder, mediaItem)
            player.addMediaItem(disorder, mediaItem)
        }

        // Prepara o player para reprodução
        player.prepare()
    }

    /**
     * Libera os recursos do player de áudio e remove a notificação de reprodução.
     */
    fun releasePlayerAndNotification() {
        // Remove todos os itens de mídia do player
        player.removeMediaItems(0, player.mediaItemCount)

        // Remove o listener do player
        player.removeListener(listener)

        // Remove o player da notificação
        playerNotificationManager.setPlayer(null)

        // Libera os recursos do player
        player.release()

        // Limpa o cache de mídias
        cache.clear()
    }

    /**
     * Remove uma música da playlist em reprodução.
     *
     * @param music A música a ser removida.
     */
    fun removedMusic(music: Music) {
        val position = playlist.musicList.indexOf(music)
        if (position != -1) {
            // Remove a música da lista de reprodução
            playlist = playlist.copy(
                musicList = playlist.musicList.filter { it.id != music.id }.toMutableList()
            )

            // Remove a música do cache de media items
            cache.removeAt(position)

            // Atualiza os media items no player
            player.removeMediaItem(position)

            viewModel.recoilPlaylists()

            // Prepara o player para reprodução
            player.prepare()
        }
    }

    /**
     * Obtém os metadados da música atualmente em reprodução.
     *
     * @param metaData Os metadados da música, se disponíveis.
     * @return Um objeto Music contendo os metadados da música.
     */
    fun getMetadata(metaData: MediaMetadata? = player.mediaMetadata): Music {
        return Music(
            author = metaData?.artist.toString(),
            thumb = metaData?.artworkUri.toString(),
            title = metaData?.albumTitle.toString(),
            url = metaData?.description.toString(),
            name = metaData?.subtitle.toString(),
            bitImage = metaData?.artworkData,
            directory = "",
            playlistId = playlist.playlist.uid,
            id = metaData?.composer.toString()
        )
    }

    /**
     * Inicia o streaming de uma música a partir de uma URI.
     *
     * @param uri A URI da música a ser reproduzida.
     * @param music A música a ser reproduzida.
     */
    fun stream(uri: Uri, music: Music) {
        val mediaItem = convertMusicToMediaItem(uri, music)

        // Verifica se o player já está reproduzindo algo
        if (player.playWhenReady) {
            // Se estiver reproduzindo, pausa a reprodução atual
            player.pause()
        }

        // Adiciona o MediaItem à lista de cache e ao player
        cache.add(mediaItem)
        player.addMediaItem(mediaItem)

        // Prepara o player para reprodução
        player.prepare()

        // Indo para posição zero
        open(music)

        // Inicia a reprodução
        player.play()

        // Define o modo de repetição para todas as músicas
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
    }

    /**
     * Abre uma música específica para reprodução.
     *
     * @param music A música a ser aberta.
     */
    fun open(music: Music) {
        for (i in 0 until cache.size) {
            cache[i].mediaMetadata.apply {
                if (artworkUri.toString() == music.thumb) {
                    // Move para a posição da música na lista de reprodução
                    player.seekTo(i, 0L)
                    return
                }
            }
        }
    }

    /**
     * Define o volume do player de áudio.
     *
     * @param one O volume a ser definido, variando de 0.0 (mudo) a 1.0 (volume máximo).
     */
    fun setVolume(one: Float) {
        player.volume = one
    }

    /**
     * Define se o modo de repetição está ativado ou desativado.
     *
     * @param boolean Um booleano indicando se o modo de repetição está ativado.
     */
    fun reapt(boolean: Boolean) {
        if (!boolean) {
            // Se o modo de repetição estiver desativado, define o modo de repetição como desligado
            player.repeatMode = Player.REPEAT_MODE_OFF
        } else {
            // Se o modo de repetição estiver ativado, define o modo de repetição como repetir a música atual
            player.repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    /**
     * Converte um tempo em milissegundos para uma representação de tempo no formato "mm:ss".
     *
     * @param time O tempo em milissegundos.
     * @return Uma string representando o tempo no formato "mm:ss".
     */
    fun timeUse(time: Int): String {
        val seconds = time / 1000f
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes.toInt(), remainingSeconds.toInt())
    }
}