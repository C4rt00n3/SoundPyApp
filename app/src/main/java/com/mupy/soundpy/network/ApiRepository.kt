package com.mupy.soundpy.network

import com.mupy.soundpy.models.Musics
import com.mupy.soundpy.models.PlayListData
import com.mupy.soundpy.models.PlayLists
import com.mupy.soundpy.models.Stream
import okhttp3.ResponseBody

class ApiRepository {
    private val api = Api.ReqMusic.service

    suspend fun search(query: String): Musics {
        return api.serach(query)
    }

    suspend fun getPlaylists(query: String): Array<PlayListData> {
        return api.getPlaylists(query)
    }

    suspend fun playlist(link: String): PlayLists {
        return api.playlist(link)
    }

    suspend fun download(link: String): ResponseBody {
        return api.download(link)
    }

    suspend fun stream(link: String): Stream {
        return api.stream(link)
    }
}