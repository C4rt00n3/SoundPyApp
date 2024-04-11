package com.mupy.soundpy.database

import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.RoomDatabase
import kotlin.concurrent.thread

@Database(entities = [MyPlaylists::class, Music::class], version = 6, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun service(): DaoMyPlaylists
}