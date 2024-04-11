package com.mupy.soundpy.utils

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.bumptech.glide.Glide
import com.mupy.soundpy.database.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class Utils {
    /**
     * Cria um arquivo .mp3 em um diretório interno.
     *
     * @param fileName O nome do arquivo.
     * @param bytes O array de bytes do arquivo.
     * @param context O contexto da aplicação.
     * @return O objeto [Music] representando o arquivo de música criado.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun createFile(fileName: String, bytes: ByteArray, context: Context): Music {
        try {
            val internalDir = context.filesDir
            val musicDirectory = File(internalDir, "saved").apply { mkdirs() }
            val cleanedFileName = fileName.replace(Regex("[^A-Za-z0-9 ]"), "").replace(" ", "") + ".mp3"
            val file = File(musicDirectory, "${cleanedFileName}.mp3")

            FileOutputStream(file).use { fos ->
                fos.write(bytes)
            }
            Log.d("MusicUtils", "Bytes saved in: ${file.absolutePath}")
            return fileToMusic(file, context)
        } catch (e: IOException) {
            Log.e("MusicUtils", "Error creating file", e)
            throw e
        }
    }

    /**
     * Obtém a imagem de uma música utilizando o Glide de forma assíncrona.
     *
     * @param music A música da qual a imagem será obtida.
     * @param context O contexto da aplicação.
     * @param onLoad O callback a ser chamado após a imagem ser carregada.
     */
    fun getImg(music: Music, context: Context, onLoad: ((Bitmap) -> Unit)?) {
        if (music.bitImage == null) {
            runBlocking {
                val img = withContext(Dispatchers.IO) {
                    Glide.with(context)
                        .asBitmap()
                        .load(music.thumb)
                        .submit()
                        .get()
                }
                onLoad?.invoke(img)
            }
        }
    }

    @TypeConverter
    fun compressBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return byteArray?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }

    /**
     * Extrai metadados de um arquivo de música e retorna um objeto Music.
     *
     * @param file O arquivo de música a ser processado.
     * @param context O contexto da aplicação.
     * @return Um objeto Music com os metadados do arquivo.
     */
    private fun fileToMusic(file: File, context: Context): Music {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(file.path)

        val title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
        val artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
        val album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
        val picture = metadataRetriever.embeddedPicture

        val thumbnailUrl = extractThumbnailUrl(album)

        val music = Music(
            author = artist,
            thumb = thumbnailUrl,
            title = title,
            url = extractUrl(album, thumbnailUrl),
            name = file.name,
            directory = file.path,
            bitImage = picture,
            playlistId = 0
        )

        return music.copy(bitImage = picture)
    }

    /**
     * Extrai a URL da thumbnail do álbum, se existir, a partir da string de metadados do álbum.
     *
     * @param album A string de metadados do álbum.
     * @return A URL da thumbnail, ou uma string vazia se não encontrada.
     */
    private fun extractThumbnailUrl(album: String): String {
        val thumbnailUrlRegex = Regex("https://[^,\\s]+")
        val thumbnailMatch = thumbnailUrlRegex.find(album)
        return thumbnailMatch?.value?.trim() ?: ""
    }

    /**
     * Extrai a URL da música a partir da string de metadados do álbum, removendo a parte da thumbnail.
     *
     * @param album A string de metadados do álbum.
     * @param thumbnailUrl A URL da thumbnail.
     * @return A URL da música.
     */
    private fun extractUrl(album: String, thumbnailUrl: String): String {
        return album.replace("thumbnail_url = $thumbnailUrl, url = ", "")
    }

    /**
     * Obtém a lista de arquivos de música salvos no diretório interno.
     *
     * @param context O contexto da aplicação.
     * @return A lista de arquivos de música.
     */
    fun getMusicsSaved(context: Context): List<Music> {
        return getMusicFiles(context).map { fileToMusic(it, context) }
    }

    /**
     * Obtém a lista de arquivos de música no diretório interno.
     *
     * @param context O contexto da aplicação.
     * @return A lista de arquivos de música.
     */
    private fun getMusicFiles(context: Context): List<File> {
        return runCatching {
            val internalDir = context.filesDir
            val musicDirectory = File(internalDir, "saved").apply { mkdirs() }
            musicDirectory.listFiles { file ->
                file.isFile && file.extension.lowercase(Locale.ROOT) == "mp3"
            }?.sortedByDescending { it.lastModified() }?.toList() ?: emptyList()
        }.getOrElse {
            Log.e("MusicUtils", "Error getting music files", it)
            emptyList()
        }
    }

    /**
     * Deleta um arquivo de música.
     *
     * @param music O objeto [Music] representando o arquivo de música a ser deletado.
     */
    fun deleteMusic(music: Music) {
        val file = File(music.directory)
        if (file.exists()) file.delete()
    }
}
