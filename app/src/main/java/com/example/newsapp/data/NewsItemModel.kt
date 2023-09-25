package com.example.newsapp.data

data class NewsItemModel(
        val id: String,
        val title: String,
        val description: String,
        val bannerUrl: String,
        val timeCreated: Long,
        val rank: Int,
        val displayTime: String,
)
