package com.example.newsapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsItemEntity(
        @PrimaryKey
        var id: String,
        var title: String? = null,
        var description: String? = null,
        var bannerUrl: String? = null,
        var timeCreated: Long? = null,
        var rank: Int? = null,
        var displayTime: String? = null
)