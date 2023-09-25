package com.example.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NewsItemEntity::class], version = 1
)
abstract class NewsDb : RoomDatabase() {
    abstract fun getNewsListDao(): NewsListDao

    companion object {
        private var INSTANCE: NewsDb? = null

        fun getDatabase(context: Context): NewsDb {
            return INSTANCE ?: synchronized(NewsDb::class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NewsDb::class.java,
                        "news_db",
                    ).build()
                }
                INSTANCE!!
            }
        }
    }

}