package com.example.newsapp.network

import com.example.newsapp.data.NewsListResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    companion object {
        const val BASE_URL = "https://storage.googleapis.com/carousell-interview-assets/android/"
    }

    @GET("carousell_news.json")
    suspend fun getNewsListsApiResponse(): Response<NewsListResponse>
}