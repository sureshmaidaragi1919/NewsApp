package com.example.newsapp.data


import com.google.gson.annotations.SerializedName

class NewsListResponse : ArrayList<NewsListItemResponse>()

data class NewsListItemResponse(
        @SerializedName("id")
        val id: String? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("description")
        val description: String? = null,
        @SerializedName("banner_url")
        val bannerUrl: String? = null,
        @SerializedName("time_created")
        val timeCreated: Long? = null,
        @SerializedName("rank")
        val rank: Int? = null
)