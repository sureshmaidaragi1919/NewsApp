package com.example.newsapp.repo

import com.example.newsapp.data.NewsItemModel
import com.example.newsapp.data.NewsListResponse
import com.example.newsapp.db.NewsItemEntity
import com.example.newsapp.db.NewsListDao
import com.example.newsapp.network.ApiCallHandler
import com.example.newsapp.network.ApiService
import com.example.newsapp.network.NetworkResult
import com.example.newsapp.util.getTimeAgo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NewsListRepository @Inject constructor(
        private val apiService: ApiService,
        private val newsListDao: NewsListDao,
) : ApiCallHandler() {

    suspend fun getNewsList(): Flow<NetworkResult<List<NewsItemModel>>> {

        return flow {
            val result = makeApiCall {
                apiService.getNewsListsApiResponse()
            }
            when (result) {
                is NetworkResult.Success -> {
                    newsListDao.insertAll(mapToEntity(result.data)) //insert into db
                    emit(NetworkResult.Success(mapToModelData(result.data))) //send result to UI
                }

                is NetworkResult.SuccessWithNoResult -> {
                    newsListDao.getNewsList().collect {
                        if (it.isNotEmpty()) { //if old data exist send it to UI
                            emit(NetworkResult.Success(mapFromEntityToModel(it)))
                        } else {
                            emit(NetworkResult.SuccessWithNoResult(result.message!!))//let it crash to so it helps to debug if msg is null
                            return@collect
                        }
                    }
                }

                is NetworkResult.Error -> {
                    newsListDao.getNewsList().collect {
                        if (it.isNotEmpty()) { //if old data exist send it to UI
                            emit(NetworkResult.Success(mapFromEntityToModel(it)))
                        } else {
                            emit(NetworkResult.Error(result.message!!))
                            return@collect
                        }
                    }
                }

                else -> {

                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun mapFromEntityToModel(list: List<NewsItemEntity>): List<NewsItemModel> {
        return list.map {
            NewsItemModel(
                id = it.id,
                title = it.title.toString(),
                description = it.description.toString(),
                rank = it.rank ?: 0,
                timeCreated = it.timeCreated ?: 0L,
                bannerUrl = it.bannerUrl.toString(),
                displayTime = it.displayTime.toString()

            )
        }

    }

    private fun mapToEntity(data: NewsListResponse?): List<NewsItemEntity> {
        return data?.map {
            NewsItemEntity(id = it.id.orEmpty(),
                title = it.title.orEmpty(),
                description = it.description.orEmpty(),
                bannerUrl = it.bannerUrl.orEmpty(),
                timeCreated = it.timeCreated ?: 0L,
                rank = it.rank ?: 0,
                displayTime = it.timeCreated?.let { it1 -> getTimeAgo(it1) } ?: "Invalid time"

            )
        } ?: kotlin.run { emptyList() }

    }

    private fun mapToModelData(data: NewsListResponse?): List<NewsItemModel> {
        return data?.map {
            NewsItemModel(id = it.id.orEmpty(),
                title = it.title.orEmpty(),
                description = it.description.orEmpty(),
                bannerUrl = it.bannerUrl.orEmpty(),
                timeCreated = it.timeCreated ?: 0,
                rank = it.rank ?: 0,
                displayTime = it.timeCreated?.let { it1 -> getTimeAgo(it1) } ?: "Invalid time"

            )
        } ?: kotlin.run { emptyList() }
    }
}