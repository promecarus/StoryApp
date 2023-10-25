package com.promecarus.storyapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.promecarus.storyapp.data.model.RemoteKeys
import com.promecarus.storyapp.data.model.Story

@Database(entities = [Story::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        fun getDatabase(context: Context): StoryDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: databaseBuilder(
                context.applicationContext, StoryDatabase::class.java, "story_database"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
