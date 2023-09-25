package com.example.newsapp.usecase

import com.example.newsapp.data.NewsItemModel
import com.example.newsapp.network.NetworkResult
import com.example.newsapp.repo.NewsListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNewsListUseCase @Inject constructor(private val repository: NewsListRepository) {

    suspend fun getNewsList(): Flow<NetworkResult<List<NewsItemModel>>> {
        return repository.getNewsList()
    }
}