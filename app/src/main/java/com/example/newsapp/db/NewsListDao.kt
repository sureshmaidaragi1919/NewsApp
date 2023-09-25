package com.example.newsapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newsItem: NewsItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(newsItem: List<NewsItemEntity>)

    @Query("DELETE FROM NewsItemEntity WHERE id = :id")
    fun deleteNews(id: Int): Int

    @Query("DELETE FROM NewsItemEntity")
    fun nukeTable(): Int

    @Query("SELECT * FROM NewsItemEntity")
    fun getNewsList(): Flow<List<NewsItemEntity>>

    @Query("SELECT * FROM NewsItemEntity WHERE id = :id")
    fun getNewsDetails(id: Int): NewsItemEntity
}